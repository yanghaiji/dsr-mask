package io.github.dsr.mask.response.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dsr.mask.core.DsrMaskStrategyRegistry;
import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.process.ResponseProcess;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class DefaultResponseProcess implements ResponseProcess {

    private static final Logger log = LoggerFactory.getLogger(DefaultResponseProcess.class);


    @Autowired
    private ObjectMapper objectMapper;

    /**
     *
     * 缓存字段，避免每次都去反射
     * key: 类
     * value: 字段
     * 缓存的目的是避免每次都去反射
     * <p>
     * | 场景       | ConcurrentHashMap | WeakHashMap |
     * | -------- | ----------------- | ----------- |
     * | 普通业务系统   | 稳定                | 稳定          |
     * | DevTools | 泄漏                | 自动回收        |
     * | 插件卸载     | 泄漏                | 自动回收        |
     * | 维护成本     | 高                 | 低           |
     * | 可解释性     | 一般                | 非常好         |
     */
    private static final Map<Class<?>, Field[]> FIELD_CACHE = Collections.synchronizedMap(new WeakHashMap<>());


    /**
     * String 类型特殊处理（JSON / 非 JSON）
     */
    @Override
    public Object processStringBody(String body) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        String trimmed = body.trim();
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            try {
                Object parsed = objectMapper.readValue(body, Object.class);
                processResponseBody(parsed);
                return objectMapper.writeValueAsString(parsed);
            } catch (Exception e) {
                log.warn("JSON字符串解析失败，返回原始字符串: {}", e.getMessage());
                return body;
            }
        }
        return body;
    }

    /**
     * 响应体统一入口
     */
    @Override
    public Object processResponseBody(Object body) {
        if (body == null) {
            return null;
        }
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());
        process(body, visited);
        return body;
    }

    /**
     * 核心递归处理逻辑
     */
    private void process(Object obj, Set<Object> visited) {
        if (obj == null || isBasicType(obj)) {
            return;
        }

        // 防止循环引用
        if (!visited.add(obj)) {
            return;
        }

        if (obj instanceof Collection<?> collection) {
            for (Object item : collection) {
                process(item, visited);
            }
            return;
        }

        if (obj instanceof Map<?, ?> map) {
            // 只处理 value，key 没必要
            for (Object value : map.values()) {
                process(value, visited);
            }
            return;
        }

        if (obj.getClass().isArray()) {
            int len = Array.getLength(obj);
            for (int i = 0; i < len; i++) {
                process(Array.get(obj, i), visited);
            }
            return;
        }

        // 普通 Java Bean
        processFields(obj, obj.getClass(), visited);
    }

    /**
     * 处理对象字段（含父类）
     */
    private void processFields(Object obj, Class<?> clazz, Set<Object> visited) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        // 先处理父类字段
        processFields(obj, clazz.getSuperclass(), visited);

        Field[] fields = FIELD_CACHE.computeIfAbsent(clazz, c -> {
            Field[] fs = c.getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
            }
            return fs;
        });

        for (Field field : fields) {
            try {
                Object value = field.get(obj);
                Mask mask = field.getAnnotation(Mask.class);

                // 先递归处理嵌套对象
                if (value != null && !isBasicType(value)) {
                    process(value, visited);
                }

                // 再处理脱敏字段
                if (mask != null && value instanceof String str) {
                    MaskStrategy strategy =
                            DsrMaskStrategyRegistry.get(mask.type());
                    if (strategy != null) {
                        field.set(obj, strategy.mask(str, mask.args()));
                    }
                }
            } catch (Exception e) {
                log.warn("process filed {} fail: {}", field.getName(), e.getMessage());
            }
        }
    }

    /**
     * 基本类型判断
     */
    private boolean isBasicType(Object obj) {
        if (obj == null) {
            return true;
        }
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive()
                || clazz.isEnum()
                || BASIC_TYPES.contains(clazz);
    }

    private static final Set<Class<?>> BASIC_TYPES = Set.of(
            String.class,
            Integer.class, Long.class, Short.class, Byte.class,
            Double.class, Float.class,
            Boolean.class, Character.class,
            Date.class,
            java.sql.Date.class,
            java.sql.Timestamp.class,
            java.time.LocalDate.class,
            java.time.LocalTime.class,
            java.time.LocalDateTime.class,
            java.time.ZonedDateTime.class,
            UUID.class
    );
}


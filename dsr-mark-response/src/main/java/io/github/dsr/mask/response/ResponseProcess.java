package io.github.dsr.mask.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.dsr.mask.core.DefaultMaskStrategyRegistry;
import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

public class ResponseProcess {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Set<Integer> scannedClasses = Collections.synchronizedSet(new HashSet<>());


    private static final Logger log = LoggerFactory.getLogger(ResponseProcess.class);

    /**
     * 字符串类型特殊处理
     */
    public Object processStringBody(String body) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        String trimmed = body.trim();
        // 判断是否是JSON格式（对象或数组）
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) ||
                (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            try {
                // 是JSON，解析为Object再处理
                Object parsed = objectMapper.readValue(body, Object.class);
                Object masked = processResponseBody(parsed);
                return objectMapper.writeValueAsString(masked);
            } catch (JsonProcessingException e) {
                log.warn("JSON字符串解析失败，返回原始字符串: {}", e.getMessage());
                return body;
            }
        } else {
            // 非JSON字符串，直接返回
            return body;
        }
    }

    public Object processResponseBody(Object body) throws JsonProcessingException {
        if (body == null) {
            return null;
        }

        // 处理基本类型
        if (isBasicType(body)) {
            return body;
        }

        // 处理集合类型
        if (body instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) body;
            List<Object> result = new ArrayList<>(collection.size());
            for (Object item : collection) {
                result.add(processResponseBody(item));
            }
            return result;
        }

        // 处理Map类型 - 关键修复2: 递归处理Map中的嵌套对象
        if (body instanceof Map<?, ?> map) {
            Map<Object, Object> result = new LinkedHashMap<>(map.size());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = processResponseBody(entry.getKey());
                Object value = processResponseBody(entry.getValue());
                result.put(key, value);
            }
            return result;
        }

        // 处理数组类型
        if (body.getClass().isArray()) {
            Object[] array = (Object[]) body;
            Object[] result = new Object[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = processResponseBody(array[i]);
            }
            return result;
        }

        // 处理普通对象
        return processObject(body);
    }

    private Object processObject(Object obj) throws JsonProcessingException {
        if (obj == null || isBasicType(obj)) {
            return obj;
        }

        // 深拷贝对象，避免修改原始对象
        String json = objectMapper.writeValueAsString(obj);
        Object copy = objectMapper.readValue(json, obj.getClass());

        scanAndProcessFields(copy);

        return copy;
    }

    private void scanAndProcessFields(Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();

        // 检查是否已经扫描过这个类
        if (scannedClasses.contains(obj.hashCode())) {
            return;
        }

        synchronized (scannedClasses) {
            if (scannedClasses.contains(obj.hashCode())) {
                return;
            }

            // 递归处理父类字段
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !superClass.equals(Object.class)) {
                scanAndProcessFields(obj); // 先处理父类
            }

            // 处理当前类的字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Mask maskAnnotation = field.getAnnotation(Mask.class);

                    Object value = field.get(obj);

                    // 关键修复3: 无论是否有@Mask，都递归处理嵌套对象
                    if (value != null && !isBasicType(value)) {
                        // 如果是集合或Map，递归处理
                        if (value instanceof Collection<?>) {
                            for (Object item : (Collection<?>) value) {
                                if (item != null && !isBasicType(item)) {
                                    scanAndProcessFields(item);
                                }
                            }
                        } else if (value instanceof Map<?, ?>) {
                            for (Object mapValue : ((Map<?, ?>) value).values()) {
                                if (mapValue != null && !isBasicType(mapValue)) {
                                    scanAndProcessFields(mapValue);
                                }
                            }
                        } else if (value.getClass().isArray()) {
                            Object[] array = (Object[]) value;
                            for (Object item : array) {
                                if (item != null && !isBasicType(item)) {
                                    scanAndProcessFields(item);
                                }
                            }
                        } else {
                            // 普通对象
                            scanAndProcessFields(value);
                        }
                    }

                    // 有@Mask注解且是字符串类型，进行掩码处理
                    if (maskAnnotation != null && value instanceof String) {
                        MaskStrategy strategy = DefaultMaskStrategyRegistry.get(maskAnnotation.type());
                        if (strategy != null) {
                            String maskedValue = strategy.mask((String) value, maskAnnotation.args());
                            field.set(obj, maskedValue);
                        }
                    }
                } catch (IllegalAccessException e) {
                    log.warn("访问字段 {} 失败: {}", field.getName(), e.getMessage());
                }
            }

            scannedClasses.add(obj.hashCode());
        }
    }

    private boolean isBasicType(Object obj) {
        if (obj == null) {
            return true;
        }

        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() ||
                clazz.equals(String.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Date.class) ||
                clazz.equals(java.sql.Date.class) ||
                clazz.equals(java.sql.Timestamp.class) ||
                clazz.equals(java.time.LocalDateTime.class) ||
                clazz.equals(java.time.LocalDate.class) ||
                clazz.equals(java.time.LocalTime.class) ||
                clazz.equals(java.time.ZonedDateTime.class) ||
                clazz.equals(UUID.class) ||
                clazz.isEnum();
    }
}

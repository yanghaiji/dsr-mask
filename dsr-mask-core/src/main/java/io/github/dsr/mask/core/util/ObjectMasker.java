package io.github.dsr.mask.core.util;

import io.github.dsr.mask.core.DsrMaskStrategyRegistry;
import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.strategy.MaskStrategy;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



public class ObjectMasker {

    /**
     * 使用 IdentityHashMap，基于对象地址判断，避免 equals/hashCode 干扰
     */
    private static final ThreadLocal<Map<Object, String>> VISITED_OBJECTS =
            ThreadLocal.withInitial(IdentityHashMap::new);

    /**
     * 缓存 Class -> Fields，避免重复反射
     */
    private static final Map<Class<?>, Field[]> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 基本类型 & 包装类型缓存
     */
    private static final Set<Class<?>> PRIMITIVE_WRAPPER_TYPES = Set.of(
            Boolean.class,
            Character.class,
            Byte.class,
            Short.class,
            Integer.class,
            Long.class,
            Float.class,
            Double.class
    );

    public static String maskObject(Object obj) {
        try {
            return maskObjectInternal(obj);
        } finally {
            VISITED_OBJECTS.get().clear();
        }
    }

    /**
     * 内部方法，用于递归处理对象
     * @param obj 要处理的对象
     * @return
     */
    private static String maskObjectInternal(Object obj) {
        if (obj == null) {
            return "null";
        }

        // 检查是否已经访问过此对象（循环引用检测）
        Map<Object, String> visited = VISITED_OBJECTS.get();
        if (visited.containsKey(obj)) {
            return "[circular reference: " + visited.get(obj) + "]";
        }

        // 记录当前对象
        String objectId = getObjectIdentifier(obj);
        visited.put(obj, objectId);

        try {
            // 处理基本类型和字符串
            if (isPrimitiveOrWrapper(obj) || obj instanceof String) {
                return formatPrimitive(obj);
            }

            // 处理集合类型
            if (obj instanceof Collection) {
                return maskCollection((Collection<?>) obj);
            }

            // 处理Map类型
            if (obj instanceof Map) {
                return maskMap((Map<?, ?>) obj);
            }

            // 处理数组
            if (obj.getClass().isArray()) {
                return maskArray(obj);
            }

            // 处理普通对象
            return maskRegularObject(obj);

        } finally {
            visited.remove(obj);
        }
    }

    /**
     * 判断对象是否为基本类型或包装类
     * @param obj
     * @return
     */
    private static boolean isPrimitiveOrWrapper(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() || PRIMITIVE_WRAPPER_TYPES.contains(clazz);
    }

    private static String formatPrimitive(Object obj) {
        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        }
        return String.valueOf(obj);
    }

    private static String escapeString(String str) {
        if (str == null) {
            return "null";
        }
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String getObjectIdentifier(Object obj) {
        return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
    }

    private static String maskCollection(Collection<?> collection) {
        String className = getSimpleClassName(collection.getClass());
        if (collection.isEmpty()) {
            return className + "[]";
        }

        StringBuilder sb = new StringBuilder(className).append("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(maskObjectInternal(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private static String maskMap(Map<?, ?> map) {
        String className = getSimpleClassName(map.getClass());
        if (map.isEmpty()) {
            return className + "{}";
        }

        StringBuilder sb = new StringBuilder(className).append("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(maskObjectInternal(entry.getKey()))
                    .append("=")
                    .append(maskObjectInternal(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String maskArray(Object array) {
        Class<?> componentType = array.getClass().getComponentType();
        String typeName = componentType.isPrimitive()
                ? componentType.getName()
                : componentType.getSimpleName();

        StringBuilder sb = new StringBuilder(typeName).append("[]{");
        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(maskObjectInternal(Array.get(array, i)));
        }
        sb.append("}");
        return sb.toString();
    }

    private static String getSimpleClassName(Class<?> clazz) {
        if (clazz == ArrayList.class || clazz == LinkedList.class) return "List";
        if (clazz == HashSet.class) return "Set";
        if (clazz == TreeSet.class) return "SortedSet";
        if (clazz == HashMap.class) return "Map";
        if (clazz == TreeMap.class) return "SortedMap";
        if (clazz == LinkedHashMap.class) return "LinkedMap";

        String simpleName = clazz.getSimpleName();
        return simpleName.isEmpty() ? clazz.getName() : simpleName;
    }

    private static String maskRegularObject(Object obj) {
        Class<?> clazz = obj.getClass();
        String className = clazz.getSimpleName();

        if (isJdkInternalClass(clazz)) {
            return className + "@" + System.identityHashCode(obj);
        }

        StringBuilder sb = new StringBuilder(className).append("{");
        boolean hasFields = false;

        for (Field field : getCachedFields(clazz)) {
            if (Modifier.isStatic(field.getModifiers())
                    || Modifier.isTransient(field.getModifiers())
                    || field.isSynthetic()) {
                continue;
            }

            try {
                Object value = field.get(obj);
                if (hasFields) {
                    sb.append(", ");
                }
                sb.append(field.getName()).append("=");

                Mask mask = field.getAnnotation(Mask.class);
                if (mask != null && value instanceof String) {
                    MaskStrategy strategy = DsrMaskStrategyRegistry.get(mask.type());
                    if (strategy != null) {
                        String masked = strategy.mask((String) value, mask.args());
                        sb.append("\"").append(escapeString(masked)).append("\"");
                    } else {
                        sb.append(maskObjectInternal(value));
                    }
                } else {
                    sb.append(maskObjectInternal(value));
                }

                hasFields = true;
            } catch (Exception e) {
                sb.append(field.getName()).append("=[ERROR: ").append(e.getMessage()).append("]");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private static Field[] getCachedFields(Class<?> clazz) {
        return FIELD_CACHE.computeIfAbsent(clazz, c -> {
            Field[] fields = c.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);
            }
            return fields;
        });
    }

    private static boolean isJdkInternalClass(Class<?> clazz) {
        String pkg = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        return pkg.startsWith("java.")
                || pkg.startsWith("javax.")
                || pkg.startsWith("jdk.")
                || pkg.startsWith("sun.")
                || clazz.getName().startsWith("com.sun.")
                || clazz.getName().contains("$");
    }
}

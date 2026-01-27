package io.github.dsr.mask.core;

import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.strategy.MaskStrategy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * 对象脱敏工具类
 * @author haiji
 */
public class ObjectMasker {

    /**
     * 线程本地变量，用于记录已经访问过的对象，防止循环引用
     */
    private static final ThreadLocal<Map<Object, String>> VISITED_OBJECTS = ThreadLocal.withInitial(HashMap::new);

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
        if (obj == null) {
            return false;
        }

        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class;
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
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
    }

    private static String maskCollection(Collection<?> collection) {
        if (collection.isEmpty()) {
            return collection.getClass().getSimpleName() + "[]";
        }

        StringBuilder sb = new StringBuilder();
        String className = getSimpleClassName(collection.getClass());
        sb.append(className).append("[");

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
        if (map.isEmpty()) {
            return map.getClass().getSimpleName() + "{}";
        }

        StringBuilder sb = new StringBuilder();
        String className = getSimpleClassName(map.getClass());
        sb.append(className).append("{");

        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(", ");
            }

            Object key = entry.getKey();
            Object value = entry.getValue();

            sb.append(maskObjectInternal(key))
                    .append("=")
                    .append(maskObjectInternal(value));

            first = false;
        }

        sb.append("}");
        return sb.toString();
    }

    private static String maskArray(Object array) {
        Class<?> componentType = array.getClass().getComponentType();
        String typeName = componentType.isPrimitive() ?
                componentType.getName() :
                componentType.getSimpleName();

        StringBuilder sb = new StringBuilder();
        sb.append(typeName).append("[]{");

        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object item = java.lang.reflect.Array.get(array, i);
            sb.append(maskObjectInternal(item));
        }

        sb.append("}");
        return sb.toString();
    }

    private static String getSimpleClassName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        if (simpleName.isEmpty()) {
            return clazz.getName();
        }

        // 简化常见集合类名
        if (clazz == ArrayList.class) {
            return "List";
        }
        if (clazz == LinkedList.class) {
            return "List";
        }
        if (clazz == HashSet.class) {
            return "Set";
        }
        if (clazz == TreeSet.class) {
            return "SortedSet";
        }
        if (clazz == HashMap.class) {
            return "Map";
        }
        if (clazz == TreeMap.class) {
            return "SortedMap";
        }
        if (clazz == LinkedHashMap.class) {
            return "LinkedMap";
        }

        return simpleName;
    }

    private static String maskRegularObject(Object obj) {
        Class<?> clazz = obj.getClass();
        String className = clazz.getSimpleName();

        // 跳过JDK内部类
        if (isJdkInternalClass(clazz)) {
            return className + "@" + System.identityHashCode(obj);
        }

        StringBuilder sb = new StringBuilder(className).append("{");

        Field[] fields = clazz.getDeclaredFields();
        boolean hasFields = false;

        for (Field field : fields) {
            // 跳过静态字段、transient字段和合成字段
            if (Modifier.isStatic(field.getModifiers()) ||
                    Modifier.isTransient(field.getModifiers()) ||
                    field.isSynthetic()) {
                continue;
            }

            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                if (hasFields) {
                    sb.append(", ");
                }

                sb.append(field.getName()).append("=");

                // 检查字段是否有@Mask注解
                Mask mask = field.getAnnotation(Mask.class);
                if (mask != null && value instanceof String) {
                    try {
                        MaskStrategy strategy = DefaultMaskStrategyRegistry.get(mask.type());
                        if (strategy != null) {
                            String maskedValue = strategy.mask((String) value, mask.args());
                            sb.append("\"").append(escapeString(maskedValue)).append("\"");
                        } else {
                            sb.append(maskObjectInternal(value));
                        }
                    } catch (Exception e) {
                        sb.append("[MASK_ERROR: ").append(e.getMessage()).append("]");
                    }
                } else {
                    sb.append(maskObjectInternal(value));
                }

                hasFields = true;
            } catch (IllegalAccessException e) {
                sb.append(field.getName()).append("=[ACCESS_ERROR]");
            } catch (Exception e) {
                sb.append(field.getName()).append("=[ERROR: ").append(e.getMessage()).append("]");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    private static boolean isJdkInternalClass(Class<?> clazz) {
        String packageName = clazz.getPackage() != null ? clazz.getPackage().getName() : "";
        return packageName.startsWith("java.") ||
                packageName.startsWith("javax.") ||
                packageName.startsWith("jdk.") ||
                packageName.startsWith("sun.") ||
                clazz.getName().startsWith("com.sun.") ||
                clazz.getName().contains("$");
    }


}
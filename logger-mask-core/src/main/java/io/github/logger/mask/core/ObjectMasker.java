package io.github.logger.mask.core;


import io.github.logger.mask.core.annotation.Mask;

import java.lang.reflect.Field;

public class ObjectMasker {


    public static String maskObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        Class<?> clazz = obj.getClass();
        StringBuilder sb = new StringBuilder(clazz.getSimpleName()).append("{");

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {

            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                sb.append(field.getName()).append("=");

                Mask mask = field.getAnnotation(Mask.class);
                if (mask != null && value instanceof String) {
                    MaskStrategy strategy =
                            DefaultMaskStrategyRegistry.get(mask.type());

                    if (strategy != null) {
                        sb.append(strategy.mask((String) value, mask.args()));
                    } else {
                        sb.append(value);
                    }
                } else {
                    sb.append(value);
                }

                sb.append(", ");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        if (sb.lastIndexOf(", ") == sb.length() - 2) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("}");
        return sb.toString();
    }
}

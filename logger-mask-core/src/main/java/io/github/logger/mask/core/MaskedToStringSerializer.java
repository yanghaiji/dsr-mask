package io.github.logger.mask.core;


public class MaskedToStringSerializer {

    public static Object wrap(Object origin) {

        if (origin == null) {
            return null;
        }
        // 跳过 String 和其他 JDK 内部类
        if (origin instanceof String) {
            return "\"" + origin + "\"";
        }

        return ObjectMasker.maskObject(origin);
    }
}

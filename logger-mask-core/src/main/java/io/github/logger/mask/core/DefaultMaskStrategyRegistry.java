package io.github.logger.mask.core;

import java.util.*;

public class DefaultMaskStrategyRegistry {

    private static final Map<String, MaskStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        register(new PhoneMaskStrategy());
    }

    public static void register(MaskStrategy strategy) {
        STRATEGY_MAP.put(strategy.type(), strategy);
    }

    public static MaskStrategy get(String type) {
        return STRATEGY_MAP.get(type);
    }
}

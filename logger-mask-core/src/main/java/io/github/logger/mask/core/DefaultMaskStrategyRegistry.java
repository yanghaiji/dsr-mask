package io.github.logger.mask.core;

import io.github.logger.mask.core.strategy.EmailMaskStrategy;
import io.github.logger.mask.core.strategy.IdcarMaskStrategy;
import io.github.logger.mask.core.strategy.MaskStrategy;
import io.github.logger.mask.core.strategy.PhoneMaskStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author haiji
 */
public class DefaultMaskStrategyRegistry /*implements MaskStrategyRegistry */{

    private static final Map<String, MaskStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        register(new PhoneMaskStrategy());
        register(new EmailMaskStrategy());
        register(new IdcarMaskStrategy());
    }

    public static void register(MaskStrategy strategy) {
        STRATEGY_MAP.put(strategy.type(), strategy);
    }

    public static MaskStrategy get(String type) {
        return STRATEGY_MAP.get(type);
    }

    public static Map<String, MaskStrategy> getStrategyMap() {
        return STRATEGY_MAP;
    }
}

package io.github.dsr.mask.core;

import io.github.dsr.mask.core.strategy.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的脱敏策略注册中心
 * @author haiji
 */
public class DefaultMaskStrategyRegistry /*implements MaskStrategyRegistry */{

    private static final Map<String, MaskStrategy> STRATEGY_MAP = new HashMap<>();

    static {
        register(new PhoneMaskStrategy());
        register(new EmailMaskStrategy());
        register(new IdcarMaskStrategy());
        register(new AddressMaskStrategy());
        register(new NameMaskStrategy());
    }

    /**
     * 注册策略
     * @param strategy 策略
     */
    public static void register(MaskStrategy strategy) {
        STRATEGY_MAP.put(strategy.type(), strategy);
    }

    /**
     * 获取策略
     * @param type 策略类型
     * @return 策略
     */
    public static MaskStrategy get(String type) {
        return STRATEGY_MAP.get(type);
    }

    /**
     * 获取策略Map
     * @return 策略Map
     */
    protected static Map<String, MaskStrategy> getStrategyMap() {
        return STRATEGY_MAP;
    }
}

package io.github.dsr.mask.core;

import io.github.dsr.mask.core.registry.MaskStrategyRegistry;
import io.github.dsr.mask.core.strategy.*;
import io.github.dsr.mask.core.strategy.builtin.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认的脱敏策略注册中心
 * @author haiji
 */
public class DsrMaskStrategyRegistry implements MaskStrategyRegistry {

    private static final Map<String, MaskStrategy> STRATEGY_MAP = new HashMap<>();

   public DsrMaskStrategyRegistry() {
       register(new PhoneMaskStrategy());
       register(new EmailMaskStrategy());
       register(new IdcarMaskStrategy());
       register(new AddressMaskStrategy());
       register(new NameMaskStrategy());
       register(new BankCardMaskStrategy());
   }

    /**
     * 注册策略
     * @param strategy 策略
     */
    public void register(MaskStrategy strategy) {
        STRATEGY_MAP.put(strategy.strategy(), strategy);
    }

    /**
     * 获取策略
     * @param type 策略类型
     * @return 策略
     */
    public MaskStrategy get(String type) {
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

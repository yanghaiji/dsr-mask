package io.github.dsr.mask.core.registry;

import io.github.dsr.mask.core.strategy.MaskStrategy;

/**
 * 脱敏策略注册中心
 * @author haiji
 */
public interface MaskStrategyRegistry {

    /**
     * 注册脱敏策略
     * @param strategy 脱敏策略
     */
    void register(MaskStrategy strategy);

    /**
     * 获取脱敏策略
     * @param type 脱敏类型
     * @return 脱敏策略
     */
    MaskStrategy get(String type);
}

package io.github.logger.mask.core.registry;

import io.github.logger.mask.core.strategy.MaskStrategy;

public interface MaskStrategyRegistry {

    void register(MaskStrategy strategy);

    MaskStrategy get(String type);
}

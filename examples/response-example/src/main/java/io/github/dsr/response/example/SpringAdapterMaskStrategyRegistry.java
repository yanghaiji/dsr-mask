package io.github.dsr.response.example;

import io.github.dsr.mask.core.registry.MaskStrategyRegistry;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import io.github.dsr.mask.core.strategy.builtin.EmailMaskStrategy;
import io.github.dsr.mask.core.strategy.builtin.IdcarMaskStrategy;
import io.github.dsr.mask.core.strategy.builtin.NameMaskStrategy;
import io.github.dsr.mask.core.strategy.builtin.PhoneMaskStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring环境适配器注册表
 * 当Spring容器中有MaskStrategy但没有MaskStrategyRegistry时使用
 */
@Component
public class SpringAdapterMaskStrategyRegistry implements MaskStrategyRegistry {

    private final Map<String, MaskStrategy> strategies = new ConcurrentHashMap<>();

    public SpringAdapterMaskStrategyRegistry() {
        // 1. 首先添加所有默认策略
        initializeDefaultStrategies();
    }

    private void initializeDefaultStrategies() {
        // 注册默认策略
        register(new PhoneMaskStrategy());
        register(new EmailMaskStrategy());
        register(new IdcarMaskStrategy());
        register(new NameMaskStrategy());
        // 可以根据需要添加更多默认策略
    }

    @Override
    public void register(MaskStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("Strategy cannot be null");
        }

        strategies.put(strategy.type(), strategy);
    }

    @Override
    public MaskStrategy get(String type) {
        return strategies.get(type);

    }

}
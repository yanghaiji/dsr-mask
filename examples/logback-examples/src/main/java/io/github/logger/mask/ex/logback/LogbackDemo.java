package io.github.logger.mask.ex.logback;

import io.github.logger.mask.core.DefaultMaskStrategyRegistry;
import io.github.logger.mask.core.constants.MaskConstants;
import io.github.logger.mask.core.strategy.MaskStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackDemo {

    private static final Logger log =
            LoggerFactory.getLogger(LogbackDemo.class);

    /**
     * 自定义 掩码的实现方式
     * step 1: 实现 {@link io.github.logger.mask.core.strategy.MaskStrategy} 接口 。（必选项）
     * step 2: 继承或者实现 {@link MaskConstants} 实现自定义掩码的类型，用{@link io.github.logger.mask.core.annotation.Mask}
     * 注解，同时作用与  {@link MaskStrategy#type()}的返回值
     * step 3: 将实现好的掩码实现方式 注册到 {@link io.github.logger.mask.core.DefaultMaskStrategyRegistry#register(MaskStrategy)}
     *
     */
    public static void main(String[] args) {

        DefaultMaskStrategyRegistry.register(new SecretMaskStrategy());

        User user = new User("张三", "13812345678", "abcdef", "1213133131@github.com");
        log.info("用户信息: {}", user);
    }
}

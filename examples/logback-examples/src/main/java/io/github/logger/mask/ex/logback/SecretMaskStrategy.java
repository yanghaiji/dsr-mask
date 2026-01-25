package io.github.logger.mask.ex.logback;

import io.github.logger.mask.core.strategy.MaskStrategy;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author haiji
 */
public class SecretMaskStrategy implements MaskStrategy {
    /**
     * 类型名，如 phone / idCard
     */
    @Override
    public String type() {
        return CustomMaskConstants.SECRET;
    }

    /**
     * 执行脱敏
     *
     * @param origin
     * @param args
     */
    @Override
    public String mask(String origin, String[] args) {
        return Base64.getEncoder().encodeToString(origin.getBytes(StandardCharsets.UTF_8));
    }
}

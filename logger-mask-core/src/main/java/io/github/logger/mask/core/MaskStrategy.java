package io.github.logger.mask.core;

public interface MaskStrategy {

    /**
     * 类型名，如 phone / idCard
     */
    String type();

    /**
     * 执行脱敏
     */
    String mask(String origin, String[] args);
}

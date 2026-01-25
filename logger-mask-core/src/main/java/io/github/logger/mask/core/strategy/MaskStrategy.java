package io.github.logger.mask.core.strategy;

/**
 * 脱敏策略
 * @author haiji
 */
public interface MaskStrategy {

    /**
     * 类型名，如 phone / idCard
     * @return 类型名
     */
    String type();

    /**
     * 执行脱敏
     * @param origin 原始数据
     * @param args 可选参数，可以配合具体的实现策略进行使用，如：脱敏修改脱敏的样式 、脱敏的位数等
     * @return 脱敏后的数据
     *
     */
    String mask(String origin, String[] args);
}

package io.github.dsr.mask.core.strategy;

/**
 * 脱敏策略
 *
 * @author haiji
 */
public interface MaskStrategy<P, R1, R2> {

    /**
     * 类型名，如 phone / idCard
     *
     * @return 类型名
     */
    R1 strategy();

    /**
     * 执行脱敏
     *
     * @param origin 原始数据
     * @param args   可选参数，可以配合具体的实现策略进行使用，如：脱敏修改脱敏的样式 、脱敏的位数等
     * @return 脱敏后的数据
     *
     */
    R2 mask(P origin, String[] args);
}

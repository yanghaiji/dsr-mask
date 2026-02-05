package io.github.dsr.mask.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 * @author haiji
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mask {

    /**
     * 脱敏策略类型（与策略绑定）
     * {@link io.github.dsr.mask.core.strategy.MaskStrategy}
     */
    String strategy();

    /**
     * 可选参数
     */
    String[] args() default {};

}

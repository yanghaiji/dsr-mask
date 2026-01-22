package io.github.logger.mask.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author haiji
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Mask {

    /**
     * 脱敏类型（与策略绑定）
     */
    String type();

    /**
     * 可选参数
     */
    String[] args() default {};
}

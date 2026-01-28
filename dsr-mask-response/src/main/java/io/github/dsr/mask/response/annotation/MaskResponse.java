package io.github.dsr.mask.response.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在Controller方法上，表示该方法的返回值需要进行掩码处理
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaskResponse {
    /**
     * 是否启用掩码处理，默认为true
     */
    boolean enabled() default true;
    
    /**
     * 自定义掩码处理器，如果需要特殊处理逻辑
     */
//    Class<? extends MaskProcessor> processor() default DefaultMaskProcessor.class;
}
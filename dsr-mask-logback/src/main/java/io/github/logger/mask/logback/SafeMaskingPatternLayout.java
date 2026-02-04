package io.github.logger.mask.logback;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.dsr.mask.core.util.ObjectMasker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全的脱敏PatternLayout，不修改原始日志事件
 * @author haiji
 */
public class SafeMaskingPatternLayout extends PatternLayout {

    private static final Logger log =
            LoggerFactory.getLogger(SafeMaskingPatternLayout.class);

    /**
     * 重写doLayout方法，对参数进行深度脱敏，并使用脱敏后的参数进行布局
     * @param event 日志事件
     * @return 布局结果
     */
    @Override
    public String doLayout(ILoggingEvent event) {
        try {
            // 首先对参数进行深度脱敏
            Object[] maskedArgs = createMaskedArgs(event.getArgumentArray());
            // 创建脱敏后的事件
            MaskedLoggingEvent secureEvent = new MaskedLoggingEvent(event, maskedArgs);
            // 使用脱敏后的事件进行布局
            // 最后对整个消息进行二次脱敏（防止遗漏）
            return super.doLayout(secureEvent);
        } catch (Exception e) {
            log.error("Error during secure logging layout", e);
            return super.doLayout(event);
        }
    }

    /**
     * 创建脱敏后的参数数组
     * @param originalArgs 原始参数数组
     * @return 脱敏后的参数数组
     */
    private Object[] createMaskedArgs(Object[] originalArgs) {
        if (originalArgs == null || originalArgs.length == 0) {
            return originalArgs;
        }

        Object[] maskedArgs = new Object[originalArgs.length];
        System.arraycopy(originalArgs, 0, maskedArgs, 0, originalArgs.length);

        for (int i = 0; i < maskedArgs.length; i++) {
            if (maskedArgs[i] != null) {
                maskedArgs[i] = ObjectMasker.maskObject(maskedArgs[i]);
            }
        }
        return maskedArgs;
    }
}
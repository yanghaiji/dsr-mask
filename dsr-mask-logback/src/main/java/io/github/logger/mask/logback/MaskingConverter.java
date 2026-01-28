package io.github.logger.mask.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.dsr.mask.core.ObjectMasker;

public class MaskingConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        try {
            // 获取原始消息模板
            String messagePattern = event.getMessage();
            Object[] args = event.getArgumentArray();

            if (args == null || args.length == 0) {
                return messagePattern;
            }

            // 创建脱敏后的参数
            Object[] maskedArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                maskedArgs[i] = ObjectMasker.maskObject(args[i]);
            }

            // 安全地格式化消息
            return safeFormat(messagePattern, maskedArgs);
        } catch (Exception e) {
            // 出错时返回原始格式化消息
            return event.getFormattedMessage();
        }
    }

    private String safeFormat(String pattern, Object[] args) {
        try {
            return String.format(pattern, args);
        } catch (Exception e) {
            // 如果格式化失败，返回原始pattern和参数
            StringBuilder sb = new StringBuilder(pattern);
            if (args != null && args.length > 0) {
                sb.append(" [");
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(args[i]);
                }
                sb.append("]");
            }
            return sb.toString();
        }
    }
}
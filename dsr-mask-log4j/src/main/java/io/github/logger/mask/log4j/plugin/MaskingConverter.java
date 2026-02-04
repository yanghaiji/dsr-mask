package io.github.logger.mask.log4j.plugin;

import io.github.dsr.mask.core.util.ObjectMasker;
import io.github.logger.mask.log4j.Log4jFormatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.message.Message;


/**
 * 脱敏日志消息转换器
 * @author haiji
 */
@Plugin(name = "MaskingConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({"mask", "m"})
public class MaskingConverter extends LogEventPatternConverter {

    private static final Logger LOGGER = LogManager.getLogger(MaskingConverter.class);

    protected MaskingConverter(String name, String style) {
        super(name, style);
    }

    /**
     * 脱敏日志消息
     * @param event
     * @param toAppendTo
     */
    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        try {
            Message message = event.getMessage();
            if (message == null || message.getFormattedMessage() == null) {
                return;
            }

            String formattedMessage = message.getFormattedMessage();
            Object[] parameters = message.getParameters();

            // 如果没有参数，直接脱敏整个消息
            if (parameters == null || parameters.length == 0) {
                String maskedMessage = ObjectMasker.maskObject(formattedMessage);
                toAppendTo.append(maskedMessage);
                return;
            }

            // 脱敏所有参数
            Object[] maskedParameters = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                maskedParameters[i] = ObjectMasker.maskObject(parameters[i]);
            }
            // 关键：使用原始消息格式和脱敏参数重新格式化
            try {
                // 使用原始格式和脱敏后的参数重新格式化
                String result = Log4jFormatMessage.formatMessage(((MutableLogEvent) message).getFormat(), maskedParameters);
                toAppendTo.append(result);
            } catch (Exception e) {
                // 备用方案：手动替换占位符
                String result = formattedMessage;
                for (Object param : maskedParameters) {
                    int index = result.indexOf("{}");
                    if (index != -1) {
                        result = result.substring(0, index) + param +
                                result.substring(index + 2);
                    } else {
                        break;
                    }
                }
                toAppendTo.append(result);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to mask log message", e);
            try {
                toAppendTo.append(event.getMessage().getFormattedMessage());
            } catch (Exception ex) {
                toAppendTo.append("[MASKING_ERROR]");
            }
        }
    }


    /**
     * 关键：必须添加这个静态工厂方法
     * @param config
     * @param options
     * @return
     */
    public static MaskingConverter newInstance(final Configuration config, final String[] options) {
        return new MaskingConverter("mask", null);
    }
}
package io.github.logger.mask.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.dsr.mask.core.ObjectMasker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全自动脱敏Converter，安全处理所有日志参数
 * @author haiji
 */
public class AutoMaskingConverter extends ClassicConverter {

    private static final Logger log =
            LoggerFactory.getLogger(AutoMaskingConverter.class);

    @Override
    public String convert(ILoggingEvent event) {
        try {
            // 获取已经格式化的完整消息
            String formattedMessage = event.getFormattedMessage();

            // 如果消息为空，直接返回
            if (formattedMessage == null || formattedMessage.isEmpty()) {
                return formattedMessage;
            }

            // 对整个消息进行脱敏处理（处理可能遗漏的敏感信息）
            return ObjectMasker.maskObject(formattedMessage).toString();
        } catch (Exception e) {
            // 出错时返回原始消息，避免日志丢失
            log.error(e.getMessage(), e);
            return event.getFormattedMessage();
        }
    }

}
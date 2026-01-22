package io.github.logger.mask.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import io.github.logger.mask.core.MaskedToStringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.event.KeyValuePair;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.util.List;
import java.util.Map;

/**
 * 线程安全的日志事件包装器，用于安全地脱敏参数
 */
public class MaskedLoggingEvent implements ILoggingEvent {
    private static final Logger log =
            LoggerFactory.getLogger(MaskedLoggingEvent.class);
    private final ILoggingEvent originalEvent;
    private final Object[] maskedArgs;
    private String cachedFormattedMessage;

    public MaskedLoggingEvent(ILoggingEvent originalEvent, Object[] maskedArgs) {
        this.originalEvent = originalEvent;
        this.maskedArgs = maskedArgs;
    }

    @Override
    public String getMessage() {
        return originalEvent.getMessage();
    }

    @Override
    public Object[] getArgumentArray() {
        return maskedArgs;
    }

    @Override
    public String getThreadName() {
        return originalEvent.getThreadName();
    }

    @Override
    public Level getLevel() {
        return originalEvent.getLevel();
    }

    @Override
    public String getLoggerName() {
        return originalEvent.getLoggerName();
    }

    @Override
    public LoggerContextVO getLoggerContextVO() {
        return originalEvent.getLoggerContextVO();
    }

    @Override
    public IThrowableProxy getThrowableProxy() {
        return originalEvent.getThrowableProxy();
    }

    @Override
    public StackTraceElement[] getCallerData() {
        return originalEvent.getCallerData();
    }

    @Override
    public boolean hasCallerData() {
        return originalEvent.hasCallerData();
    }

    @Override
    public Marker getMarker() {
        return originalEvent.getMarker();
    }

    /**
     * @return
     */
    @Override
    public List<Marker> getMarkerList() {
        return originalEvent.getMarkerList();
    }

    @Override
    public Map<String, String> getMDCPropertyMap() {
        return originalEvent.getMDCPropertyMap();
    }

    @Override
    public Map<String, String> getMdc() {
        return originalEvent.getMdc();
    }

    @Override
    public long getTimeStamp() {
        return originalEvent.getTimeStamp();
    }

    /**
     * @return
     */
    @Override
    public int getNanoseconds() {
        return originalEvent.getNanoseconds();
    }

    @Override
    public long getSequenceNumber() {
        return originalEvent.getSequenceNumber();
    }

    /**
     * @return
     */
    @Override
    public List<KeyValuePair> getKeyValuePairs() {
        return originalEvent.getKeyValuePairs();
    }

    /**
     *
     */
    @Override
    public void prepareForDeferredProcessing() {
        originalEvent.prepareForDeferredProcessing();
    }

    @Override
    public String getFormattedMessage() {
        if (cachedFormattedMessage == null) {
            synchronized (this) {
                if (cachedFormattedMessage == null) {
                    cachedFormattedMessage = formatMessageWithFallback();
                }
            }
        }
        return cachedFormattedMessage;
    }

    private String formatMessageWithFallback() {
        String messagePattern = originalEvent.getMessage();

        // 快速路径：没有参数或参数为空
        if (messagePattern == null || maskedArgs == null || maskedArgs.length == 0) {
            return originalEvent.getFormattedMessage();
        }

        try {
            // 尝试1：使用MessageFormatter格式化
            return tryFormatWithMessageFormatter(messagePattern, maskedArgs);
        } catch (Exception e1) {
            log.debug("MessageFormatter格式化失败，尝试String.format", e1);

            try {
                // 尝试2：使用String.format格式化（兼容%格式）
                return tryFormatWithStringFormat(messagePattern, maskedArgs);
            } catch (Exception e2) {
                log.debug("String.format格式化失败", e2);

                // 尝试3：简单拼接（最后的回退方案）
                return fallbackToSimpleFormat(messagePattern, maskedArgs);
            }
        }
    }

    private String tryFormatWithMessageFormatter(String pattern, Object[] args) {
        int placeholderCount = countPlaceholders(pattern);

        if (placeholderCount == 0) {
            return pattern;
        }

        // 调整参数数量以匹配占位符
        Object[] adjustedArgs = adjustArguments(args, placeholderCount);

        FormattingTuple result = MessageFormatter.arrayFormat(pattern, adjustedArgs);

        // 处理异常
        if (result.getThrowable() != null) {
            throw new RuntimeException("格式化失败", result.getThrowable());
        }

        return result.getMessage();
    }

    private String tryFormatWithStringFormat(String pattern, Object[] args) {
        // 检查是否有%格式化符
        if (!pattern.contains("%")) {
            return pattern;
        }

        // 转换参数为字符串，避免类型问题
        Object[] stringArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            stringArgs[i] = args[i] != null ? args[i].toString() : "null";
        }

        return String.format(pattern, stringArgs);
    }

    private String fallbackToSimpleFormat(String pattern, Object[] args) {
        // 简单替换第一个占位符
        String result = pattern;
        for (int i = 0; i < args.length && result.contains("{}"); i++) {
            result = result.replaceFirst("\\{}",
                    args[i] != null ? args[i].toString() : "null");
        }
        return result;
    }

    private Object[] adjustArguments(Object[] originalArgs, int targetCount) {
        if (originalArgs.length == targetCount) {
            return originalArgs;
        }

        Object[] adjustedArgs = new Object[targetCount];

        if (originalArgs.length > targetCount) {
            // 截断多余参数
            System.arraycopy(originalArgs, 0, adjustedArgs, 0, targetCount);
        } else {
            // 用null填充不足的参数
            System.arraycopy(originalArgs, 0, adjustedArgs, 0, originalArgs.length);
            for (int i = originalArgs.length; i < targetCount; i++) {
                adjustedArgs[i] = null;
            }
        }

        return adjustedArgs;
    }

    private int countPlaceholders(String message) {
        if (message == null) return 0;

        int count = 0;
        int index = 0;
        while ((index = message.indexOf("{}", index)) != -1) {
            count++;
            index += 2;
        }
        return count;
    }

}
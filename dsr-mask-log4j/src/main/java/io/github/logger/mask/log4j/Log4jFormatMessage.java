package io.github.logger.mask.log4j;

/**
 * 日志消息格式化
 * @author haiji
 */
public class Log4jFormatMessage {

    /**
     * 格式化日志消息
     *
     * @param pattern 日志消息模板
     * @param arguments 日志参数
     * @return 格式化后的日志消息
     */
    public static String formatMessage(String pattern, Object[] arguments) {
        if (arguments == null || arguments.length == 0) {
            return pattern;
        }

        StringBuilder result = new StringBuilder(pattern);
        int argIndex = 0;

        while (argIndex < arguments.length) {
            int placeholderIndex = result.indexOf("{}");
            if (placeholderIndex == -1) {
                break;
            }

            String argValue = arguments[argIndex] != null ? arguments[argIndex].toString() : "null";
            result.replace(placeholderIndex, placeholderIndex + 2, argValue);
            argIndex++;
        }

        // 如果还有未使用的参数，添加到末尾
        while (argIndex < arguments.length) {
            if (!result.isEmpty() && !result.toString().endsWith(" ")) {
                result.append(" ");
            }
            result.append(arguments[argIndex] != null ? arguments[argIndex].toString() : "null");
            argIndex++;
        }

        return result.toString();
    }
}

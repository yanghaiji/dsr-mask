package io.github.dsr.mask.core.strategy.builtin;

import io.github.dsr.mask.core.constants.AmountEnum;
import io.github.dsr.mask.core.constants.MaskConstants;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 金额掩码工具类
 * 提供多种金额掩码策略，适用于敏感信息展示场景
 */
public class AmountMaskStrategy implements MaskStrategy<BigDecimal, String, String> {

    @Override
    public String strategy() {
        return MaskConstants.AMOUNT;
    }

    @Override
    public String mask(BigDecimal amount, String[] args) {
        if (amount == null) {
            return null;
        }
        String result = null;
        if (args.length == 4) {
            String strategy = args[0];
            String maskChar = args[1];
            String keepSymbol = args[2];
            String percentage = args[3];
            result = maskAmount(amount,
                    MaskConfig.builder()
                            .strategy(AmountEnum.getByCode(strategy))
                            .maskChar(maskChar)
                            .keepSymbol(Boolean.parseBoolean(keepSymbol))
                            .percentage(Double.parseDouble(percentage))
                            .build());
        }else if (args.length == 3){
            String strategy = args[0];
            String maskChar = args[1];
            String keepSymbol = args[2];
            result = maskAmount(amount,
                    MaskConfig.builder()
                            .strategy(AmountEnum.getByCode(strategy))
                            .maskChar(maskChar)
                            .keepSymbol(Boolean.parseBoolean(keepSymbol))
                            .build());
        }else {
            //默认智能掩码  输出: ¥12*****7.89
            result = maskAmount(amount);
        }
        return result;
    }


    /**
     * 掩码配置类
     */
    public static class MaskConfig {
        private AmountEnum strategy = AmountEnum.SMART_MASK;
        private int keepFirst = 2;      // 保留前几位
        private int keepLast = 2;       // 保留后几位
        private int keepBoth = 1;       // 保留前后各几位
        private double percentage = 0.3; // 保留百分比
        private String maskChar = "*";    // 掩码字符
        private boolean keepDecimal = true; // 是否保留小数部分
        private boolean keepSymbol = true;  // 是否保留货币符号
        private Locale locale = Locale.CHINA; // 本地化设置
        private int threshold = 10000;  // 智能掩码阈值

        // 构建器模式
        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final MaskConfig config = new MaskConfig();

            public Builder strategy(AmountEnum strategy) {
                config.strategy = strategy;
                return this;
            }

            public Builder keepFirst(int keepFirst) {
                config.keepFirst = keepFirst;
                return this;
            }

            public Builder keepLast(int keepLast) {
                config.keepLast = keepLast;
                return this;
            }

            public Builder keepBoth(int keepBoth) {
                config.keepBoth = keepBoth;
                return this;
            }

            public Builder percentage(double percentage) {
                config.percentage = Math.max(0.1, Math.min(0.9, percentage));
                return this;
            }

            public Builder maskChar(String maskChar) {
                config.maskChar = maskChar;
                return this;
            }

            public Builder keepDecimal(boolean keepDecimal) {
                config.keepDecimal = keepDecimal;
                return this;
            }

            public Builder keepSymbol(boolean keepSymbol) {
                config.keepSymbol = keepSymbol;
                return this;
            }

            public Builder locale(Locale locale) {
                config.locale = locale;
                return this;
            }

            public Builder threshold(int threshold) {
                config.threshold = threshold;
                return this;
            }

            public MaskConfig build() {
                return config;
            }
        }

        // Getters
        public AmountEnum getStrategy() {
            return strategy;
        }

        public int getKeepFirst() {
            return keepFirst;
        }

        public int getKeepLast() {
            return keepLast;
        }

        public int getKeepBoth() {
            return keepBoth;
        }

        public double getPercentage() {
            return percentage;
        }

        public String getMaskChar() {
            return maskChar;
        }

        public boolean isKeepDecimal() {
            return keepDecimal;
        }

        public boolean isKeepSymbol() {
            return keepSymbol;
        }

        public Locale getLocale() {
            return locale;
        }

        public int getThreshold() {
            return threshold;
        }
    }

    // 默认配置
    private final MaskConfig DEFAULT_CONFIG = MaskConfig.builder().build();

    /**
     * 金额掩码入口方法（使用默认配置）
     */
    public String maskAmount(BigDecimal amount) {
        return maskAmount(amount, DEFAULT_CONFIG);
    }

    /**
     * 金额掩码入口方法（自定义配置）
     */
    public String maskAmount(BigDecimal amount, MaskConfig config) {
        if (amount == null) {
            return null;
        }

        try {
            // 格式化金额
            String formattedAmount = formatAmount(amount, config);

            // 应用掩码策略
            return applyMaskStrategy(formattedAmount, config);
        } catch (Exception e) {
            // 异常时返回安全值
            return "***";
        }
    }

    /**
     * 字符串金额掩码
     */
    public String maskAmount(String amountStr, MaskConfig config) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return amountStr;
        }

        try {
            // 解析字符串为BigDecimal
            BigDecimal amount = parseAmount(amountStr, config.getLocale());
            return maskAmount(amount, config);
        } catch (Exception e) {
            // 如果解析失败，尝试直接处理字符串
            return maskStringAmount(amountStr, config);
        }
    }

    /**
     * 格式化金额
     */
    private String formatAmount(BigDecimal amount, MaskConfig config) {
        NumberFormat format = NumberFormat.getCurrencyInstance(config.getLocale());

        // 设置小数位数
        if (!config.isKeepDecimal()) {
            format.setMaximumFractionDigits(0);
            format.setMinimumFractionDigits(0);
        }

        return format.format(amount);
    }

    /**
     * 应用掩码策略
     */
    private String applyMaskStrategy(String amount, MaskConfig config) {
        // 分离货币符号和金额数值
        String[] parts = splitAmountAndSymbol(amount, config.getLocale());
        String symbol = parts[0];
        String numericPart = parts[1];

        // 根据策略进行掩码
        String maskedNumeric = switch (config.getStrategy()) {
            case KEEP_FIRST -> maskKeepFirst(numericPart, config);
            case KEEP_LAST -> maskKeepLast(numericPart, config);
            case KEEP_BOTH_ENDS -> maskKeepBothEnds(numericPart, config);
            case PERCENTAGE -> maskByPercentage(numericPart, config);
            case FULL_MASK -> maskFull(numericPart, config);
            case SMART_MASK -> smartMask(numericPart, config);
        };

        // 重新组合
        return config.isKeepSymbol() ? symbol + maskedNumeric : maskedNumeric;
    }

    /**
     * 保留前n位掩码
     */
    private String maskKeepFirst(String amount, MaskConfig config) {
        if (amount.length() <= config.getKeepFirst()) {
            return amount;
        }

        String visiblePart = amount.substring(0, config.getKeepFirst());
        String maskedPart = String.valueOf(config.getMaskChar())
                .repeat(amount.length() - config.getKeepFirst());

        return visiblePart + maskedPart;
    }

    /**
     * 保留后n位掩码
     */
    private String maskKeepLast(String amount, MaskConfig config) {
        if (amount.length() <= config.getKeepLast()) {
            return amount;
        }

        String visiblePart = amount.substring(amount.length() - config.getKeepLast());
        String maskedPart = String.valueOf(config.getMaskChar())
                .repeat(amount.length() - config.getKeepLast());

        return maskedPart + visiblePart;
    }

    /**
     * 保留前后各n位掩码
     */
    private String maskKeepBothEnds(String amount, MaskConfig config) {
        int keep = config.getKeepBoth();
        if (amount.length() <= keep * 2) {
            return amount;
        }

        String firstPart = amount.substring(0, keep);
        String lastPart = amount.substring(amount.length() - keep);
        int maskLength = amount.length() - keep * 2;
        String maskedPart = String.valueOf(config.getMaskChar()).repeat(maskLength);

        return firstPart + maskedPart + lastPart;
    }

    /**
     * 按百分比掩码
     */
    private String maskByPercentage(String amount, MaskConfig config) {
        int keepCount = (int) (amount.length() * config.getPercentage());
        keepCount = Math.max(1, Math.min(keepCount, amount.length() - 1));

        return maskKeepFirst(amount,
                MaskConfig.builder()
                        .keepFirst(keepCount)
                        .maskChar(config.getMaskChar())
                        .build());
    }

    /**
     * 完全掩码
     */
    private String maskFull(String amount, MaskConfig config) {
        return String.valueOf(config.getMaskChar()).repeat(amount.length());
    }

    /**
     * 智能掩码
     */
    private String smartMask(String amount, MaskConfig config) {
        // 移除非数字字符（如千位分隔符）
        String cleanAmount = amount.replaceAll("[^\\d.]", "");

        try {
            BigDecimal num = new BigDecimal(cleanAmount);

            // 根据金额大小选择不同的掩码策略
            if (num.abs().compareTo(BigDecimal.valueOf(config.getThreshold())) > 0) {
                // 大金额：保留前后各1位
                return maskKeepBothEnds(amount,
                        MaskConfig.builder()
                                .keepBoth(1)
                                .maskChar(config.getMaskChar())
                                .build());
            } else {
                // 小金额：保留前2位
                return maskKeepFirst(amount,
                        MaskConfig.builder()
                                .keepFirst(2)
                                .maskChar(config.getMaskChar())
                                .build());
            }
        } catch (Exception e) {
            // 解析失败，使用保守策略
            return maskKeepBothEnds(amount, config);
        }
    }

    /**
     * 分离货币符号和金额数值
     */
    private String[] splitAmountAndSymbol(String amount, Locale locale) {
        // 匹配货币符号
        Pattern pattern = Pattern.compile("([^\\d.,-]+)?([\\d.,-]+)");
        Matcher matcher = pattern.matcher(amount);

        if (matcher.find()) {
            String symbol = matcher.group(1) != null ? matcher.group(1) : "";
            String numeric = matcher.group(2) != null ? matcher.group(2) : amount;
            return new String[]{symbol, numeric};
        }

        return new String[]{"", amount};
    }

    /**
     * 解析字符串金额
     */
    private BigDecimal parseAmount(String amountStr, Locale locale) {
        try {
            // 移除货币符号和千位分隔符
            String cleanStr = amountStr.replaceAll("[^\\d.-]", "");
            // 处理本地化小数点（如欧洲的逗号）
            if (locale != null && !Locale.US.equals(locale)) {
                cleanStr = cleanStr.replace(',', '.');
            }
            return new BigDecimal(cleanStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountStr, e);
        }
    }

    /**
     * 字符串金额直接掩码（当无法解析为BigDecimal时使用）
     */
    private String maskStringAmount(String amountStr, MaskConfig config) {
        // 尝试识别数字部分
        Pattern pattern = Pattern.compile("(\\d[\\d.,]*)");
        Matcher matcher = pattern.matcher(amountStr);

        if (matcher.find()) {
            String numericPart = matcher.group(1);
            String maskedNumeric = smartMask(numericPart, config);
            return matcher.replaceFirst(maskedNumeric);
        }

        // 未找到数字，返回完全掩码
        return maskFull(amountStr, config);
    }

    /**
     * 批量处理金额掩码
     */
    public List<String> batchMaskAmounts(List<BigDecimal> amounts, MaskConfig config) {
        if (amounts == null) {
            return Collections.emptyList();
        }

        return amounts.stream()
                .map(amount -> maskAmount(amount, config))
                .collect(Collectors.toList());
    }

    /**
     * 验证掩码结果是否安全
     */
    public boolean isMaskSecure(String maskedAmount, int minMaskChars) {
        if (maskedAmount == null) {
            return false;
        }

        // 计算掩码字符数量
        long maskCount = maskedAmount.chars()
                .filter(c -> c == '*' || c == '#' || c == 'X')
                .count();

        // 计算数字可见比例
        long digitCount = maskedAmount.chars()
                .filter(Character::isDigit)
                .count();

        return maskCount >= minMaskChars &&
                (digitCount == 0 || digitCount <= maskedAmount.length() * 0.3);
    }


}
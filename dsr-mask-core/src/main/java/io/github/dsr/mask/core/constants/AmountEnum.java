package io.github.dsr.mask.core.constants;

/**
 * 掩码策略枚举
 */
public enum AmountEnum {
    // 保留前几位
    KEEP_FIRST("1", "保留前n位"),
    // 保留后几位
    KEEP_LAST("2", "保留后n位"),
    // 保留前后各几位
    KEEP_BOTH_ENDS("3", "保留前后各n位"),
    // 百分比掩码（如只显示前50%）
    PERCENTAGE("4", "按百分比保留"),
    // 完全掩码
    FULL_MASK("5", "完全掩码"),
    // 智能掩码（根据金额大小自动选择策略）
    SMART_MASK("6", "智能掩码");

    private final String code;
    private final String desc;

    AmountEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AmountEnum getByCode(String code) {
        for (AmountEnum value : AmountEnum.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
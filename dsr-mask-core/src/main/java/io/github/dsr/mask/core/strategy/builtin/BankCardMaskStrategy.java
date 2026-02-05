package io.github.dsr.mask.core.strategy.builtin;

import io.github.dsr.mask.core.constants.MaskConstants;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import org.apache.commons.lang3.StringUtils;

public class BankCardMaskStrategy implements MaskStrategy {

    @Override
    public String strategy() {
        return MaskConstants.BANK;
    }

    /**
     * 银行卡号掩码 - 显示前6位和后4位
     * @param cardNumber 银行卡号
     * @return 掩码后的卡号，如：622848******1234
     */
    @Override
    public String mask(String cardNumber, String[] args) {
        if (cardNumber == null || cardNumber.length() < 10) {
            return cardNumber; // 长度不足，直接返回
        }
        //可自定义显示位数
        if (args.length >= 2) {
            return maskCardNumber(cardNumber, Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        }else {
            // 保留前6位和后4位，中间用*代替
            return  StringUtils.deleteWhitespace (cardNumber).replaceAll("(?<=\\d{6})\\d(?=\\d{4})", "*");
        }

    }



    /**
     * 银行卡号掩码 - 可自定义显示位数
     * @param cardNumber 银行卡号
     * @param prefixLength 显示前几位
     * @param suffixLength 显示后几位
     * @return 掩码后的卡号
     */
    private static String maskCardNumber(String cardNumber, int prefixLength, int suffixLength) {
        if (cardNumber == null || cardNumber.length() < prefixLength + suffixLength) {
            return cardNumber;
        }

        int maskLength = cardNumber.length() - prefixLength - suffixLength;

        // 添加前几位

        return cardNumber.substring(0, prefixLength) +

                // 添加掩码
                "*".repeat(Math.max(0, maskLength)) +

                // 添加后几位
                cardNumber.substring(cardNumber.length() - suffixLength);
    }


}
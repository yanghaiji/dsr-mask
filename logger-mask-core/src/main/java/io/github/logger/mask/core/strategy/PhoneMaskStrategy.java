package io.github.logger.mask.core.strategy;

import io.github.logger.mask.core.constants.MaskConstants;

import java.util.regex.Pattern;

/**
 * @author haiji
 */
public class PhoneMaskStrategy implements MaskStrategy {


    private static final Pattern PATTERN =  Pattern.compile("(\\b1\\d{2})\\d{4}(\\d{4}\\b)");

    @Override
    public String type() {
        return MaskConstants.PHONE;
    }

    @Override
    public String mask(String origin, String[] args) {
        return PATTERN.matcher(origin).replaceAll("$1****$2");
    }
}

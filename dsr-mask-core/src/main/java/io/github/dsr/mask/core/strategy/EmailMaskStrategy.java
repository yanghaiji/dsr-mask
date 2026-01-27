package io.github.dsr.mask.core.strategy;

import io.github.dsr.mask.core.constants.MaskConstants;

import java.util.regex.Pattern;

/**
 * @author haiji
 */
public class EmailMaskStrategy implements MaskStrategy {

    private static final Pattern PATTERN =
            Pattern.compile("(\\w{2})\\w+(@\\w+)");

    @Override
    public String type() {
        return MaskConstants.EMAIL;
    }

    @Override
    public String mask(String origin, String[] args) {
        return PATTERN.matcher(origin).replaceAll("$1****$2");
    }


}

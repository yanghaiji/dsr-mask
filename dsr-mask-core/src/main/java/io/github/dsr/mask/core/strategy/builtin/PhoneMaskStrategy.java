package io.github.dsr.mask.core.strategy.builtin;

import io.github.dsr.mask.core.constants.MaskConstants;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author haiji
 */
public class PhoneMaskStrategy implements MaskStrategy<String, String, String> {


    private static final Pattern PATTERN =  Pattern.compile("(\\b1\\d{2})\\d{4}(\\d{4}\\b)");

    @Override
    public String strategy() {
        return MaskConstants.PHONE;
    }

    @Override
    public String mask(String origin, String[] args) {
        return PATTERN.matcher(StringUtils.deleteWhitespace (origin)).replaceAll("$1****$2");
    }
}

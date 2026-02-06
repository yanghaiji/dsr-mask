package io.github.dsr.mask.core.strategy.builtin;

import io.github.dsr.mask.core.constants.MaskConstants;
import io.github.dsr.mask.core.strategy.MaskStrategy;

import java.util.regex.Pattern;

/**
 * @author haiji
 */
public class EmailMaskStrategy implements MaskStrategy<String, String, String>{

    private static final Pattern PATTERN =
            Pattern.compile("(\\w{2})\\w+(@\\w+)");

    @Override
    public String strategy() {
        return MaskConstants.EMAIL;
    }

    @Override
    public String mask(String origin, String[] args) {
        return PATTERN.matcher(origin).replaceAll("$1****$2");
    }


}

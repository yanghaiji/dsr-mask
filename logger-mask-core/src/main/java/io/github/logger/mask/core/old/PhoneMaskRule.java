package io.github.logger.mask.core.old;


import java.util.regex.Pattern;

public class PhoneMaskRule implements MaskRule {

    private static final Pattern PATTERN =
            Pattern.compile("(\\b1\\d{2})\\d{4}(\\d{4}\\b)");

    @Override
    public boolean support(MaskContext context) {
        return true;
    }

    @Override
    public String apply(String message) {
        return PATTERN.matcher(message).replaceAll("$1****$2");
    }
}

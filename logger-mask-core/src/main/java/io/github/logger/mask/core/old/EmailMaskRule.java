package io.github.logger.mask.core.old;


import java.util.regex.Pattern;

public class EmailMaskRule implements MaskRule {

    private static final Pattern PATTERN =
            Pattern.compile("(\\w{2})\\w+(@\\w+)");

    @Override
    public boolean support(MaskContext context) {
        return true;
    }

    @Override
    public String apply(String message) {
        return PATTERN.matcher(message).replaceAll("$1****$2");
    }
}

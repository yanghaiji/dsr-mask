package io.github.logger.mask.core.old;


import java.util.regex.Pattern;

public class IdCardMaskRule implements MaskRule {

    private static final Pattern PATTERN =
            Pattern.compile("(\\b\\d{6})\\d{8}(\\w{4}\\b)");

    @Override
    public boolean support(MaskContext context) {
        return true;
    }

    @Override
    public String apply(String message) {
        return PATTERN.matcher(message).replaceAll("$1********$2");
    }
}

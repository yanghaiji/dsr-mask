package io.github.logger.mask.core.strategy;

import io.github.logger.mask.core.constants.MaskConstants;

import java.util.regex.Pattern;

/**
 * @author haiji
 */
public class IdcarMaskStrategy implements MaskStrategy {


    private static final Pattern PATTERN =
            Pattern.compile("(\\b\\d{6})\\d{8}(\\w{4}\\b)");

    @Override
    public String type() {
        return MaskConstants.ID_CAR;
    }

    @Override
    public String mask(String origin, String[] args) {
        return PATTERN.matcher(origin).replaceAll("$1********$2");
    }



}

package io.github.logger.mask.core.strategy;

import io.github.logger.mask.core.constants.MaskConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.util.regex.Pattern;

/**
 * @author haiji
 */
public class NameMaskStrategy implements MaskStrategy {



    @Override
    public String type() {
        return MaskConstants.NAME;
    }

    @Override
    public String mask(String fullName, String[] args) {
        if (StringUtils.isNotBlank(fullName)) {
            String name = StringUtils.left(fullName, 1);
            return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
        }
        return fullName;
    }


}

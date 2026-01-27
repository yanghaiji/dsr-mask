package io.github.dsr.mask.core.strategy;

import io.github.dsr.mask.core.constants.MaskConstants;
import org.apache.commons.lang3.StringUtils;

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

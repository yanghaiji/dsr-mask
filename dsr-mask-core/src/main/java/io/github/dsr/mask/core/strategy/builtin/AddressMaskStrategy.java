package io.github.dsr.mask.core.strategy.builtin;

import io.github.dsr.mask.core.constants.MaskConstants;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * @author haiji
 */
public class AddressMaskStrategy implements MaskStrategy<String, String, String> {



    @Override
    public String strategy() {
        return MaskConstants.ADDRESS;
    }

    @Override
    public String mask(String address, String[] args) {
        if (StringUtils.isNotBlank(address)) {
            return StringUtils.left(address, 3).
                    concat(StringUtils.removeStart(
                            StringUtils.leftPad(StringUtils.right(address, address.length()-11),
                                    StringUtils.length(address), "*"), "***"));
        }
        return address;
    }


}

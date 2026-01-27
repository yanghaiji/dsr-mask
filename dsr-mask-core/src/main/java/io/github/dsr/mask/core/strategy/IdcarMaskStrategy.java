package io.github.dsr.mask.core.strategy;

import io.github.dsr.mask.core.constants.MaskConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author haiji
 */
public class IdcarMaskStrategy implements MaskStrategy {


    @Override
    public String type() {
        return MaskConstants.ID_CAR;
    }

    @Override
    public String mask(String idNumber, String[] args) {
        if (StringUtils.isNotBlank(idNumber)) {
            if (idNumber.length() == MaskConstants.ID_CAR_LENGTH_15){
                idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{3})", "$1******$2");
            }
            if (idNumber.length() == MaskConstants.ID_CAR_LENGTH_18){
                idNumber = idNumber.replaceAll("(\\w{6})\\w*(\\w{3})", "$1*********$2");
            }
        }
        return idNumber;
    }



}

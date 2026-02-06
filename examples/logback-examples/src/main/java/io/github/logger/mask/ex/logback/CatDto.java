package io.github.logger.mask.ex.logback;


import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.constants.MaskConstants;

import java.math.BigDecimal;

public class CatDto {

    @Mask(strategy = MaskConstants.NAME)
    private String name;

    @Mask(strategy = MaskConstants.BANK)
    private String bankCard;

    @Mask(strategy = MaskConstants.AMOUNT)
    private BigDecimal amount;

    @Mask(strategy = MaskConstants.AMOUNT,
            args = {
                    /**
                     * 掩码的位数 如果使用默认的策略这里的数据必须与 {@link io.github.dsr.mask.core.constants.AmountEnum}一致
                     */
                    "4",
                    // 掩码的样式
                    "#",
                    // 是否保留货币符号
                    "true"
            })
    private BigDecimal amountOther;

    public CatDto(String name, String bankCard, BigDecimal amount, BigDecimal amountOther) {
        this.name = name;
        this.bankCard = bankCard;
        this.amount = amount;
        this.amountOther = amountOther;
    }
}

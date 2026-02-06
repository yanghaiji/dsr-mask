package io.github.logger.mask.ex.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class AmountDemo {

    private static final Logger log = LoggerFactory.getLogger(AmountDemo.class);


    public static void main(String[] args) {
        CatDto amount = new CatDto("张三", "1101901999090909", new BigDecimal("10020.99"), new BigDecimal("2242432.12"));

        log.info("CatDto: {}", amount);
    }
}

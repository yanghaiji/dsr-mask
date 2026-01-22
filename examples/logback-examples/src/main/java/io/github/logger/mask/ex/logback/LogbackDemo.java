package io.github.logger.mask.ex.logback;

import org.slf4j.*;

public class LogbackDemo {

    private static final Logger log =
            LoggerFactory.getLogger(LogbackDemo.class);

    public static void main(String[] args) {
        User user = new User("张三", "13812345678", "abcdef");
        log.info("用户信息: {}", user);
    }
}

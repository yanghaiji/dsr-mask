package io.github.logger.mask.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Log4jDemo {

    private static Logger log = LogManager.getLogger(Log4jDemo.class);

    public static void main(String[] args) {

        User user = new User("张三", "13812345678", "abcdef", "1213133131@github.com");
        User user2 = new User("张三", "13812345678", "abcdef", "1213133131@github.com");

        List<User> userList = List.of(user, user2);


        log.info("test log4j");
        log.info("test {} , test p2 :{}","log4j","log4j2");
        log.info("用户信息: {}", user);
        log.info("用户信息集合: {}", userList);
        log.info("用户信息 {},用户信息集合: {}",user, userList);
    }
}

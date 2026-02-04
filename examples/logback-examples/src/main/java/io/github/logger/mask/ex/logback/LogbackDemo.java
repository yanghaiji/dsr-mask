package io.github.logger.mask.ex.logback;

import io.github.dsr.mask.core.annotation.Mask;
import io.github.dsr.mask.core.DsrMaskStrategyRegistry;
import io.github.dsr.mask.core.constants.MaskConstants;
import io.github.dsr.mask.core.strategy.MaskStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class LogbackDemo {

    private static final Logger log = LoggerFactory.getLogger(LogbackDemo.class);

    /**
     * 自定义 掩码的实现方式
     * <br>
     * step 1: 实现 {@link MaskStrategy} 接口 。（必选项）
     * <br>
     * step 2: 继承或者实现 {@link MaskConstants} 实现自定义掩码的类型，用{@link Mask}
     * 注解，同时作用与  {@link MaskStrategy#type()}的返回值
     * <br>
     * step 3: 将实现好的掩码实现方式 注册到 {@link DsrMaskStrategyRegistry#register(MaskStrategy)}
     *
     * <br>
     * 注意：
     * 当你是多级嵌套时 就不要重写对象的toString方法 会导致 StackOverflowError，如果非要重写 可以简写 {@link User#toString()}
     *
     */
    public static void main(String[] args) {

        DsrMaskStrategyRegistry.register(new SecretMaskStrategy());

        User user = new User("张三", "13812345678", "abcdef", "1213133131@github.com","北京市朝阳区CBD中国尊13层","110190199909090909");
        User user2 = new User("王二麻", "13812345678", "abcdef", "1213133131@github.com","北京市朝阳区CBD中国尊16层","110190199909090908");

        List<User> userList = List.of(user, user2);

//        for (int i = 0; i < 1000; i++) {
//            log.info("用户信息: {}", user);
//            log.info("用户信息集合: {}", userList);
//        }

        log.info("用户信息: {}", user);
        log.info("用户信息集合: {}", userList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", user2);
        map.put("key3", userList);

        log.info("用户信息 map: {}", userList);


        user.setList(userList);

        log.info("用户信息 嵌套: {}", userList);


    }
}

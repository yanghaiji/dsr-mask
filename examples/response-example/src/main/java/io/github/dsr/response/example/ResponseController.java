package io.github.dsr.response.example;

import io.github.dsr.mask.response.annotation.MaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ResponseController {


    @MaskResponse
    @PostMapping("/hello")
    public HashMap<String, Object> hello() {

        User user = new User("张三", "13812345678", "abcdef", "1213133131@github.com","北京市朝阳区CBD中国尊13层","110190199909090909");
        User user2 = new User("王二麻", "13812345678", "abcdef", "1213133131@github.com","北京市朝阳区CBD中国尊16层","110190199909090908");
        User user3 = new User("王二麻", "13812345678", "abcdef", "1213133131@github.com","北京市朝阳区CBD中国尊16层","110190199909090908");

        List<User> userList = List.of(user, user2);
        List<User> userList2 = List.of(user3, user2);

        user.setList(userList2);

        log.info("用户信息: {}", user);
        log.info("用户信息集合: {}", userList);

        HashMap<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", user2);
        map.put("key3", userList);
        log.info("用户信息 map: {}", userList);

        return map;
    }
}

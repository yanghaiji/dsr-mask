# Logger Mask

Logger Mask 是一个用于保护敏感信息的 Java 日志脱敏工具。它能够自动识别并遮蔽日志中的敏感数据，如手机号、邮箱地址、身份证号、银行卡号等，从而确保日志输出符合数据安全和隐私保护的要求。

## 功能特性

- **自动脱敏**：自动识别并遮蔽敏感数据，无需手动配置
- **多种脱敏规则**：支持手机号、邮箱地址、身份证号、银行卡号等多种敏感数据类型的脱敏
- **自定义脱敏规则**：允许开发者自定义脱敏规则和模式
- **零侵入性**：基于注解的方式使用，对原有代码无侵入

## 使用方法

### 添加依赖
在 `pom.xml` 中添加：
```java

      <dependency>
            <groupId>io.github.logger.mask</groupId>
            <artifactId>logger-mask-logback</artifactId>
      </dependency>
              
------------------------------------------------------------
      
      <dependency>
            <groupId>io.github.logger.mask</groupId>
            <artifactId>logger-mask-log4j</artifactId>
      </dependency>
      

```


### 使用


### logback基本用法

```java

public class LogbackDemo {

    private static final Logger log = LoggerFactory.getLogger(LogbackDemo.class);

    /**
     * 自定义 掩码的实现方式
     * <br>
     * step 1: 实现 {@link io.github.logger.mask.core.strategy.MaskStrategy} 接口 。（必选项）
     * <br>
     * step 2: 继承或者实现 {@link MaskConstants} 实现自定义掩码的类型，用{@link io.github.logger.mask.core.annotation.Mask}
     * 注解，同时作用与  {@link MaskStrategy#type()}的返回值
     * <br>
     * step 3: 将实现好的掩码实现方式 注册到 {@link io.github.logger.mask.core.DefaultMaskStrategyRegistry#register(MaskStrategy)}
     *
     * <br>
     * 注意：
     * 当你是多级嵌套时 就不要重写对象的toString方法 会导致 StackOverflowError，如果非要重写 可以简写 {@link User#toString()}
     *
     */
    public static void main(String[] args) {

        DefaultMaskStrategyRegistry.register(new SecretMaskStrategy());

        User user = new User("张三", "13812345678", "abcdef", "1213133131@github.com","北京市朝阳区CBD中国尊13层","110190199909090909");
        User user2 = new User("王二麻", "13812345678", "abcdef", "1213133131@github.com","北京市朝阳区CBD中国尊16层","110190199909090908");

        List<User> userList = List.of(user, user2);

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

```

## 脱敏规则

| 数据类型 | 示例输入               | 脱敏后输出 |
|------|--------------------|------------|
| 姓名   | 张三                 | "张* |
| 手机号  | 13812345678        | 138****5678 |
| 邮箱   | example@test.com   | e****@test.com |
| 身份证  | 110123199001011234 | 110123********1234 |
| 住址   | 北京市朝阳区CBD中国尊13层    | 北京市********尊13层 |

## 配置选项
 配置文件示例：
详细参考:
 - [logback.xml](https://github.com/yanghaiji/logger-mask/blob/main/examples/logback-examples/src/main/resources/logback-test.xml)
 - [log4j2.xml](https://github.com/yanghaiji/logger-mask/blob/main/examples/log4j-examples/src/main/resources/log4j2.xml)
```xml
<configuration scan="true" scanPeriod="60 seconds">
    <property name="LOG_HOME" value="logs"/>
    <property name="APP_NAME" value="myapp"/>

    <!-- 定义脱敏PatternLayout -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="io.github.logger.mask.logback.SafeMaskingPatternLayout">
                <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>
            </layout>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

## 开源社区

我们非常欢迎社区贡献！如果您想为 Logger Mask 项目做出贡献，请参考以下指南：

### 如何参与

1. Fork 仓库
2. 创建功能分支 (`git checkout -b feature/xxxxFeature`)
3. 提交您的更改 (`git commit -m 'Add some xxxxFeature'`)
4. 推送到分支 (`git push origin feature/xxxxFeature`)
5. 创建 Pull Request

### 社区支持

- **报告问题**：如果您发现了 bug 或有问题，请在 GitHub Issues 中报告
- **功能请求**：想要新功能？请在 Issues 中提出功能请求
- **文档改进**：帮助改进文档，让项目更容易理解和使用

### 行为准则

为了确保社区和谐，我们遵循标准的行为准则。所有参与者都应该尊重他人，保持专业态度，避免任何形式的歧视或骚扰。

### 贡献者指南

- 代码风格：遵循 Google Java Style Guide
- 测试覆盖率：确保新增功能有适当的单元测试
- 文档：为新功能提供清晰的文档说明

### 致谢

感谢所有为本项目做出贡献的开发者们！

## 许可证

本项目采用 Apache2.0 许可证 - 详见 [LICENSE](./LICENSE) 文件

## 贡献

欢迎提交 Issue 和 Pull Request 来帮助我们改进这个项目。

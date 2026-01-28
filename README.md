# Data Security Runtime Mask (DSR Mask)

DSR Mask 是一个用于保护敏感信息的 Java 全局返回值字段与日志脱敏工具。它能够自动识别并遮蔽日志中的敏感数据，如手机号、邮箱地址、身份证号、银行卡号等，从而确保日志输出符合数据安全和隐私保护的要求。

## 🌟 主要特性

- **自动脱敏**：自动识别并遮蔽敏感数据，无需手动配置
- **多种脱敏规则**：支持手机号、邮箱地址、身份证号、住址等多种敏感数据类型的脱敏
- **自定义脱敏规则**：允许开发者自定义脱敏规则和模式
- **零侵入性**：基于注解的方式使用，对原有代码无侵入
- **多框架支持**：支持 logback 和 log4j2 日志框架

## 📦 快速开始

### 添加依赖

在 [pom.xml]() 中添加相应的依赖：

#### Logback 用户
```xml
<dependency>
    <groupId>io.github.dsr</groupId>
    <artifactId>dsr-mask-logback</artifactId>
</dependency>
```


#### Log4j2 用户
```xml
<dependency>
    <groupId>io.github.dsr</groupId>
    <artifactId>dsr-mask-log4j</artifactId>
</dependency>
```

#### 返回值脱敏用户
```xml
<dependency>
    <groupId>io.github.dsr</groupId>
    <artifactId>dsr-mark-response</artifactId>
</dependency>
```


### 基本使用

#### 返回值示例代码

```java
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
```
效果如下：

```json
{
    "key1": "value1",
    "key2": {
        "name": "王二麻",
        "phone": "138****5678",
        "secret": "abcdef",
        "email": "12****@github.com",
        "list": null,
        "address": "北京市********尊16层",
        "idCar": "110190*********908"
    },
    "key3": [
        {
            "name": "张三",
            "phone": "138****5678",
            "secret": "abcdef",
            "email": "12****@github.com",
            "list": [
                {
                    "name": "王二麻",
                    "phone": "138****5678",
                    "secret": "abcdef",
                    "email": "12****@github.com",
                    "list": null,
                    "address": "北京市********尊16层",
                    "idCar": "110190*********908"
                }
                
            ],
            "address": "北京市********尊13层",
            "idCar": "110190*********909"
        }
    ]
}
```

#### 日志示例代码
```java
public class LogbackDemo {

    private static final Logger log = LoggerFactory.getLogger(LogbackDemo.class);

    /**
     * 自定义掩码的实现方式
     * <br>
     * step 1: 实现 {@link MaskStrategy} 接口。（必选项）
     * <br>
     * step 2: 继承或者实现 {@link MaskConstants} 实现自定义掩码的类型，
     * 使用 {@link Mask} 注解，同时作用于 {@link MaskStrategy#type()} 的返回值
     * <br>
     * step 3: 将实现好的掩码实现方式注册到 {@link DefaultMaskStrategyRegistry#register(MaskStrategy)}
     *
     * <br>
     * 注意：
     * 当多级嵌套时不要重写对象的 toString 方法，否则会导致 StackOverflowError，
     * 如果非要重写，可以简化 {@link User#toString()}
     */
    public static void main(String[] args) {

        // 注册自定义脱敏策略
        DefaultMaskStrategyRegistry.register(new SecretMaskStrategy());

        // 创建测试数据
        User user = new User("张三", "13812345678", "abcdef", 
                           "1213133131@github.com", "北京市朝阳区CBD中国尊13层", "110190199909090909");
        User user2 = new User("王二麻", "13812345678", "abcdef", 
                            "1213133131@github.com", "北京市朝阳区CBD中国尊16层", "110190199909090908");

        List<User> userList = List.of(user, user2);

        // 记录日志（敏感信息将被自动脱敏）
        log.info("用户信息: {}", user);
        log.info("用户信息集合: {}", userList);

        // 测试 Map 类型的脱敏
        HashMap<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", user2);
        map.put("key3", userList);

        log.info("用户信息 map: {}", userList);

        user.setList(userList);
        log.info("用户信息嵌套: {}", userList);
    }
}
```
效果
 ```
io.github.logger.mask.ex.logback.LogbackDemo - 用户信息 嵌套: List12[User{name="张*", phone="138****5678", secret="YWJjZGVm", email="12****@github.com", list=[circular reference: List12@283383329], address="北京市********尊13层", idCar="110190*********909"}, User{name="王**", phone="138****5678", secret="YWJjZGVm", email="12****@github.com", list=null, address="北京市********尊16层", idCar="110190*********908"}]

```


## 🔒 脱敏规则

| 数据类型 | 示例输入               | 脱敏后输出           |
|----------|------------------------|---------------------|
| 姓名     | 张三                   | 张*                |
| 手机号   | 13812345678           | 138****5678        |
| 邮箱     | example@test.com      | e****@test.com     |
| 身份证   | 110123199001011234    | 110123********1234 |
| 住址     | 北京市朝阳区CBD中国尊13层 | 北京市********尊13层 |

## ⚙️ 配置选项

### Logback 配置

创建 `logback-spring.xml` 或 `logback.xml` 配置文件：

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


### Log4j2 配置

对于 Log4j2 用户，参考项目中的配置文件示例。

1. 创建 `log4j2.xml` 配置文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration packages="io.github.logger.mask.log4j.plugin">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %mask%n"/>
        </Console>

        <File name="File" fileName="logs/app.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %mask%n"/>
        </File>
    </Appenders>

    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```

### 返回值配置

只需要在返回值上添加 @MaskResponse 注解
```java

@MaskResponse
@PostMapping("/api/hello")
public HashMap<String, Object> hello() { }
```


## 🛠️ 自定义脱敏策略

如需自定义脱敏规则，可以按照以下步骤操作：

1. **实现 MaskStrategy 接口**
```java
public class CustomMaskStrategy implements MaskStrategy {
    @Override
    public String type() {
        return "CUSTOM_TYPE";
    }

    @Override
    public String mask(Object obj) {
        // 自定义脱敏逻辑
        return "***";
    }
}
```


2. **注册策略**
```java
DefaultMaskStrategyRegistry.register(new CustomMaskStrategy());
```


3. **在实体类上使用注解**
```java
@Mask(type = "CUSTOM_TYPE")
private String sensitiveField;
```


## 🤝 贡献指南

我们非常欢迎社区贡献！如果您想为 DSR Mask 项目做出贡献，请参考以下指南：

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

### 贡献者指南

- 代码风格：遵循 Google Java Style Guide
- 测试覆盖率：确保新增功能有适当的单元测试
- 文档：为新功能提供清晰的文档说明

## 📄 许可证

本项目采用 Apache 2.0 许可证 - 详见 [LICENSE](./LICENSE) 文件

## 💬 支持

欢迎提交 Issue 和 Pull Request 来帮助我们改进这个项目。

---

> 感谢所有为本项目做出贡献的开发者们！

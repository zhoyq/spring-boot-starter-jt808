# 交通标准808协议解析二次开发包

## 介绍

这个二次开发包是基于作者之前做过项目的源码改造的，那个时候使用不是很标准，仅仅是解析车辆定位数据，
并没有实现所有的内容。现在有时间终于可以重构一下原来的项目源码。

## 版本特性

### 20200512 v1.2.2

- :boom: 兼容交通标准808协议的2011、2013、2019版本
- :boom: 基于 Spring 系列框架，充分利用 Spring 的优势，改写扩展都很简单（自定义消息包处理器）
- :anger: 修复报警和状态解析的异常

### 20200503 v1.0.1

- :boom: 处理分包粘包
- :boom: 兼容交通标准808协议的2011、2013版本
- :boom: 超长指令分包下发（一般是超过1K）
- :boom: 分包处理（上传信息分包会合并解析）

## 如何开发

- 完整开发视频请访问[重构录屏](https://space.bilibili.com/37839961)
- 基于开发包进行二次开发请访问[直播录屏](https://www.bilibili.com/video/BV1cg4y167jW/)
- 详细说明请访问我的[博客](https://www.zhoyq.com/2020/05/30/%E8%BD%A6%E8%81%94%E7%BD%91/%E3%80%90JT808%E3%80%91Spring%20Boot%20Stater%20Jt808%20%E7%AE%80%E5%8D%95%E6%BA%90%E7%A0%81%E8%A7%A3%E8%AF%BB/)
- 最小化启动项目已经开源，[欢迎访问](https://github.com/zhoyq/jt808-server-starter)
- 作者 JDK 使用的是 openJDK 12 版本，还没有在其他 JDK 版本进行测试。

**下面是基于maven简短的开发使用步骤，详细还请访问[直播录屏](https://www.bilibili.com/video/BV1cg4y167jW/)**

1. 创建新的 spring boot 项目的 pom 文件，并添加以下依赖：

```xml
<dependency>
    <groupId>com.zhoyq</groupId>
    <artifactId>spring-boot-starter-jt808</artifactId>
    <version>1.2.2</version>
</dependency>
```

2. 新建启动类，并且配置不使用Web容器（当然有需要也可以使用）。添加 `@EnableJt808Server` 注解。

```java
@SpringBootApplication
@EnableJt808Server
public class Application {
    public static void main (String[] args) {
        var app = new SpringApplication(Application.class);
        // 不使用web容器 仅启动jt808服务
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}
```

3. 实现持久化接口，项目中已经有了基于 HashMap 的会话层的接口，仍需用户提供持久化的Bean。

```java
import com.zhoyq.server.jt808.starter.service.DataService;

@Component
public class SimpleDataService implements DataService
```

4. 然还有在 `application.yml` 中添加配置：

```yaml
jt808:
  enabled: true
  use: mina
  protocol: tcp
  port: 10001
  processCount: 2
  corePoolSize: 1
  maximumPoolSize: 10
  # 单位毫秒
  keepAliveTime: 1000
  # 单位秒
  idleTime: 10
  idleCount: 6
  readBufferSize: 2048
  packageLength: 1024
  threadCorePoolSize: 1
  threadMaximumPoolSize: 10
  # 单位毫秒
  threadKeepAliveTime: 1000
  masterSize: 1
  slaveSize: 10
  tcpNoDelay: true
  keepAlive: true
```

> 建议使用 mina 配置，因为作者之前写的工程就是基于 mina 写的，当然我也写了 netty 的版本。

至此，启动程序，祝生活愉快。

## 如何扩展

因为很多时候处理逻辑并不是那么严谨，还是有需要自定义一些常规逻辑所以开放了对于单条命令的扩展方式，本节目标是使用自定义心跳包扩展，放弃原有心跳包逻辑定义。
只需要定义一个类即可，如下：

```java
import com.zhoyq.server.jt808.starter.core.Jt808Pack;
import com.zhoyq.server.jt808.starter.core.PackHandler;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 0x0002 终端心跳
 */
@Slf4j
@Jt808Pack(msgId = 0x0002)
public class HeartbeatPackHandler implements PackHandler {
    @Override
    public byte[] handle(byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody) {
        log.info("new heartbeat.");
        return ResHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x00);
    }
}
```

当然，应答需要自己组织，开发包里也提供了工具类 `ResHelper`，只要定义好类，实现 `PackHandler` 接口并且使用 `@Jt808Pack` 注解即可。
当然也可以使用这种方式定义协议之外的消息，比如使用保留的消息位定义用户自己的消息类型。

> 注意：鉴权的逻辑还没有开放，未鉴权只能访问终端注册和终端鉴权两个包处理器。

## 授权

二次开发包使用 MIT 授权，大家随便用~

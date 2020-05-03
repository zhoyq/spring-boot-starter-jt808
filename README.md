# 交通标准808协议解析二次开发包

## 介绍

这个二次开发包是基于作者之前做过项目的源码改造的，那个时候使用不是很标准，仅仅是解析车辆定位数据，
并没有实现所有的内容。现在有时间终于可以重构一下原来的项目源码。

## 特性

- 已经作了分包粘包处理
- 兼容2011版本和2013版本的交通标准808协议
- 超长指令分包下发（一般是超过1K）
- 分包处理（上传信息分包会合并解析）
- 基于 Spring 系列框架，充分利用 Spring 的优势，改写扩展都很简单（自定义消息包处理器）

## 开发

- 完整开发视频请访问[重构录屏](https://space.bilibili.com/37839961)
- 基于开发包进行二次开发请访问[直播录屏](https://www.bilibili.com/video/BV1cg4y167jW/)
- 作者 JDK 使用的是 openJDK 12 版本，还没有在其他 JDK 版本进行测试。

**下面是基于maven简短的开发使用步骤，详细还请访问[直播录屏](https://www.bilibili.com/video/BV1cg4y167jW/)**

1. 创建新的 spring boot 项目的 pom 文件，并添加以下依赖：

```xml
<dependency>
    <groupId>com.zhoyq</groupId>
    <artifactId>spring-boot-starter-jt808</artifactId>
    <version>1.0.1</version>
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

3. 实现会话和持久化接口，因为项目中目前没有实现这两个Bean所以需要用户自己创建并实现，另外这两个Bean实际上也是用户主要逻辑的载体。

```java
import com.zhoyq.server.jt808.starter.service.DataService;
import com.zhoyq.server.jt808.starter.service.SessionService;

@Component
public class SimpleDataService implements DataService
@Component
public class SimpleSessionService implements SessionService
```

4. 然还有在 `application.yml` 中添加配置：

```yaml
jt808:
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

## 授权

二次开发包使用 MIT 授权，大家随便用~

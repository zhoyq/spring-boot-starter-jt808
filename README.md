# 交通标准808协议解析二次开发包

## 介绍

这个二次开发包是基于作者之前做过项目的源码改造的，那个时候使用不是很标准，仅仅是解析车辆定位数据，
并没有实现所有的内容。现在有时间终于可以重构一下原来的项目源码。

## 版本特性

### v1.3.2-fix1 v1.3.2-jdk1.8-fix1

- 解决 SessionManagement write 方法一直返回 false 的问题

### 20210409 v1.3.2 v1.3.2-jdk1.8

- :hammer: 将消息包流水号 分配给各个连接处理
- :bug: 修复内置缓存内存泄露的风险
- :bug: 修复分包消息处理问题
- :bug: ResHelper 下发消息 支持协议 2019
- :construction: 精简部分重复代码

### 20201004 v1.3.1 v1.3.1-jdk1.8

- :arrow_up: 更新 JDK 版本到 15
- :arrow_up: 更新 SpringBoot 版本到 2.3.4.RELEASE
- :construction: 0x0801 添加多媒体数据、定位数据以及多媒体实体数据之间的关联（非兼容性更新）

### 20200704 v1.2.5 v1.2.5-jdk1.8

- :bug: 修复消息读取逻辑 （B站网友 果子狸猫么么 反馈）

### 20200701 v1.2.4 v1.2.4-jdk1.8

- :sparkles: 增加配置 `auth` 以及 `authMsgId` 用于控制权限
- :art: 调整代码，更新依赖，更新JDK到14

### 20200629 v1.2.3-jdk1.8

- :sparkles: 提供 JDK 1.8 版本的支持

### 20200512 v1.2.2

- :sparkles: 兼容交通标准808协议的2011、2013、2019版本
- :sparkles: 基于 Spring 系列框架，充分利用 Spring 的优势，改写扩展都很简单（自定义消息包处理器）
- :bug: 修复报警和状态解析的异常

### 20200503 v1.0.1

- :sparkles: 处理分包粘包
- :sparkles: 兼容交通标准808协议的2011、2013版本
- :sparkles: 超长指令分包下发（一般是超过1K）
- :sparkles: 分包处理（上传信息分包会合并解析）

## 版本升级

### 1.2.x 升级 1.3.2

`DataService` 接口有变动，重新实现 `terminalLocation`、`mediaPackage` 两个方法即可。

连个接口增加了参数 mediaId，用于 0x0801 多媒体数据上传 时，连接多媒体消息、定位数据以及实体数据。

## 如何开发

- 完整开发视频请访问[重构录屏](https://space.bilibili.com/37839961)
- 基于开发包进行二次开发请访问[直播录屏](https://www.bilibili.com/video/BV1cg4y167jW/)
- 详细说明请访问我的[博客](https://www.zhoyq.com/2020/05/30/%E8%BD%A6%E8%81%94%E7%BD%91/%E3%80%90JT808%E3%80%91Spring%20Boot%20Stater%20Jt808%20%E7%AE%80%E5%8D%95%E6%BA%90%E7%A0%81%E8%A7%A3%E8%AF%BB/)
- 最小化启动项目已经开源，[欢迎访问](https://github.com/zhoyq/jt808-server-starter)
- 作者 JDK 使用的是 openJDK 15 版本，同时还提供 openJDK 1.8 编译版本，还没有在其他 JDK 版本进行测试。

**下面是基于maven简短的开发使用步骤，详细还请访问[直播录屏](https://www.bilibili.com/video/BV1cg4y167jW/)**

1. 创建新的 spring boot 项目的 pom 文件，并添加以下依赖：

```xml
<dependency>
    <groupId>com.zhoyq</groupId>
    <artifactId>spring-boot-starter-jt808</artifactId>
    <version>1.3.2</version>
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
public class SimpleDataService implements DataService{}
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
  auth: true
  authMsgId: "0100,0102"
```

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

## FAQ

### 我下载了最小化程序并启动但是发送定位信息却没有反应？

答：首先，程序本身实现了鉴权逻辑，在没有进行终端注册和鉴权的情况下只接受这两个消息，并且返回失败应答；
其次，第一次进行终端注册的时候，会调用 `DataService.terminalRegister` 进行终端注册，此时返回的字符串就是鉴权码，
鉴权码会被缓存（推荐使用`redis`，使用默认的 `hashmap` 会在重启后丢失鉴权信息），并在终端鉴权时使用，
如果没有实现 `DataService.terminalRegister` 这部分逻辑，也会导致失败应答；
最后，我在 `1.2.4` 版本之后加入了权限控制可选配置 `auth` 和 `authMagId` 选项，`auth` 代表是否检查权限，`authMsgId` 代表
权限开启时，可以不需要权限就访问的消息ID。这样就可以自己控制需要的权限了。

### 我启动了程序，如何下发指令呢？

```java
// ...
import com.zhoyq.server.jt808.starter.core.SessionManagement;

@RestController
public class TestController {
    private SessionManagement session;
    public TestController(SessionManagement session) {
        this.session = session;
    }
    @GetMapping("/test")
    public String test(String sim, byte[] data) {
        // data 是不包含校验码以及转义的数据 因为发送之前会自动添加校验码以及转义数据 
        session.write(sim, data);
        return "下发指令成功";
    }
}
```

### 程序更新规则是怎么样的？

目前的规则就是按照版本滚动更新，旧版本（包含发布版本）不会提供升级补丁或者更新pr，作者升级会考虑兼容性，也会给出解决方案，
所以还是手动升级到最新版本比较好。另外，新的PR可以提交到 `develop` 分支 ( 如果能提交到 [github](https://github.com/zhoyq/spring-boot-starter-jt808/tree/develop) 就更好了 )，其他分支（已经固定）暂时不接受 `PR` 提交。

## 致谢

- 感谢 [B站网友 果子狸猫么么](https://space.bilibili.com/30198711) 反馈的功能性BUG
- 感谢 [GITHUB网友 大黄蜂coder](https://github.com/bigbeef) 反馈的BUG
- 感谢 [GITEE网友 杨顾](https://gitee.com/andy_yanggu) 反馈的修改意见和BUG

## 授权

二次开发包使用 MIT 授权

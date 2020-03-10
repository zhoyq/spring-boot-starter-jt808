# 交通标准808协议解析二次开发包

## 介绍

解析协议是几年前写的，在福建用过，那时候使用不是很标准，解析不校验只是把协议当作上传定位的一种方式。
作者有过在中交检测的经验，用中交检测的软件测试过，是通过了的。
这次因为疫情有时间在家整理一下原来的项目，重构了部分内容。

## 特性

* 已经作了分包粘包处理
* 兼容2011版本和2013版本的交通标准808协议
* 超长指令分包下发（一般是超过1K）
* 分包处理（上传信息分包会合并解析）
* 基于 Spring 系列框架，充分利用 Spring 的优势，改写扩展都很简单（自定义消息包处理器）

## 开发

完整开发视频请访问[重构录屏](https://space.bilibili.com/37839961)

项目是基于 Maven 构建的，但是作者并没有在中央仓库注册账号。
可以自己下载源码通过 maven 打包，源我都在 pom 里写好了，应该不会有网络不通的问题。
JDK 使用的是 openJDK 12 版本，没有在其他 JDK 版本进行测试，只有这个版本是保证没有问题的。

> 注意，项目是二次开发包，并不能直接运行，还请知悉。

打包好 jar 包，添加依赖就可以创建 Spring 项目了。
像这样：

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

按用户需要是否需要 web 服务，设置是否启动 web 服务。当然 web 相关开发就太多了，就不详细说了哈。

再启动程序之前还需要实现两个接口，并在类上使用 `@Component` 注解，这两个接口分别是:

```java
import com.zhoyq.server.jt808.starter.service.DataService;
import com.zhoyq.server.jt808.starter.service.SessionService;
```

两个接口其实是给用户托管了数据持久化和会话处理。这样我就不用处理太多业务上的事情啦~

当然还有项目配置文件，放到 `classpath` 上就行了, 当然 web 相关的其他配置，我就不管了~

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

建议使用 mina 配置，因为作者之前写的工程就是基于 mina 写的，当然我也写了 netty 的版本。

至此，像启动 spring 程序一样，启动程序，不报错就是成功啦。

最后，

祝您生活愉快。

## 授权

二次开发包使用 MIT 授权，大家随便用~

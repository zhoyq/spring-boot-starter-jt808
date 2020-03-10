/*
 *  Copyright (c) 2020. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2020.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2020. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.config;

import com.zhoyq.server.jt808.starter.core.Jt808Pack;
import com.zhoyq.server.jt808.starter.core.Jt808Server;
import com.zhoyq.server.jt808.starter.helper.CustomThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务启动配置
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/16
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {
        "com.zhoyq.server.jt808.starter"
}, includeFilters = @ComponentScan.Filter(Jt808Pack.class))
public class Jt808Starter implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private Jt808Config jt808Config;

    /**
     * 数据处理线程
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                jt808Config.getThreadCorePoolSize(),
                jt808Config.getThreadMaximumPoolSize(),
                jt808Config.getThreadKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new CustomThreadFactory("jt808-mina-thread-pool-db")
        );
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        var serverKey = "jt808_" + jt808Config.getUse() + "_" + jt808Config.getProtocol();

        if (!applicationStartedEvent.getApplicationContext().containsBean(serverKey)) {
            log.error("no jt808 server instance for key {} has been found !", serverKey);
            return ;
        }

        Jt808Server server = (Jt808Server) applicationStartedEvent.getApplicationContext().getBean(serverKey);

        if (server.start()) {
            log.info("jt808 server started on port {}", jt808Config.getPort());
        } else {
            log.error("jt808 server start failed!");
        }

    }
}

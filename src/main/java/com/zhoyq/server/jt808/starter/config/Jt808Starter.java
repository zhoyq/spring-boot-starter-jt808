/*
 *  Copyright (c) 2020. 衷于栖 All rights reserved.
 *
 *  版权所有 衷于栖 并保留所有权利 2020。
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  作者：衷于栖（feedback@zhoyq.com）
 *  博客：https://www.zhoyq.com
 *  创建时间：2020
 *
 */

package com.zhoyq.server.jt808.starter.config;

import com.zhoyq.server.jt808.starter.core.Jt808Server;
import com.zhoyq.server.jt808.starter.dto.SimAuthDto;
import com.zhoyq.server.jt808.starter.helper.CustomThreadFactory;
import com.zhoyq.server.jt808.starter.service.CacheService;
import com.zhoyq.server.jt808.starter.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.List;
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
})
public class Jt808Starter implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private Jt808Config jt808Config;
    @Autowired
    private DataService dataService;
    @Autowired
    private CacheService cacheService;

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

        if (!jt808Config.getEnabled()) {
            log.info("jt808 server is disabled !");
            return;
        }

        // 初始化 session
        List<SimAuthDto> list = dataService.simAuth();
        if(list != null){
            for (SimAuthDto sa: list) {
                cacheService.setAuth(sa.getSim(), sa.getAuth());
            }
        }

        String serverKey = "jt808_" + jt808Config.getUse() + "_" + jt808Config.getProtocol();

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

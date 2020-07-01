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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务配置
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/16
 */
@Component
@ConfigurationProperties(prefix = "jt808")
@Setter
@Getter
public class Jt808Config {
    /**
     * 使用框架
     * mina | netty
     */
    private String use = "mina";
    /**
     * 使用协议
     * tcp | udp
     */
    private String protocol = "tcp";
    /**
     * 服务端口
     */
    private Integer port = 10001;

    private Boolean auth = true;
    private String authMsgId = "0100,0102";

    // ====\/ for mina

    private Integer processCount = 2;
    private Integer corePoolSize = 1;
    private Integer maximumPoolSize = 10;
    private Integer keepAliveTime = 1000;
    private Integer idleTime = 10;
    private Integer idleCount = 6;
    private Integer readBufferSize = 2048;
    private Integer packageLength = 1024;

    // ====\/ for netty

    private Integer masterSize = 1;
    private Integer slaveSize = 10;

    // ====\/ for both

    private Boolean tcpNoDelay = true;
    private Boolean keepAlive = true;
    /**
     * rsa 超长数据处理单元 默认最长117
     */
    private Integer rsaHandleUnit = 117;
    /**
     * 是否启用
     */
    private Boolean enabled = true;

    // ====\/ for execute thread

    private Integer threadCorePoolSize = 1;
    private Integer threadMaximumPoolSize = 10;
    private Integer threadKeepAliveTime = 1000;
}

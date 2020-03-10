/*
 *  Copyright (c) 2019. 衷于栖 All rights reserved
 *  版权所有 衷于栖 并保留所有权利 2019.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  衷于栖（feedback@zhoyq.com）于 2019. 创建
 *  https://www.zhoyq.com
 */

package com.zhoyq.server.jt808.starter.mina;

import com.zhoyq.server.jt808.starter.config.Jt808Config;
import com.zhoyq.server.jt808.starter.core.Jt808Server;
import com.zhoyq.server.jt808.starter.helper.CustomThreadFactory;
import com.zhoyq.server.jt808.starter.mina.coder.Jt808CodecFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2019/12/17
 */
@Slf4j
@Component("jt808_mina_udp")
public class Jt808MinaUdpServer implements Jt808Server {

    private NioDatagramAcceptor acceptor;

    @Autowired
    private Jt808Config jt808Config;
    @Autowired
    private IoHandler handler;
    @Autowired
    private Jt808CodecFactory jt808CodecFactory;

    @Override
    public boolean start() {
        if (acceptor != null) {
            return acceptor.isActive();
        }
        acceptor = new NioDatagramAcceptor();
        acceptor.getFilterChain().addLast("executor",
                new ExecutorFilter(
                        jt808Config.getCorePoolSize(),
                        jt808Config.getMaximumPoolSize(),
                        jt808Config.getKeepAliveTime(),
                        TimeUnit.MILLISECONDS,
                        new CustomThreadFactory("jt808-mina-thread-pool")
                )
        );
        // 数据校验 以及 粘包 分包处理 过滤器
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(jt808CodecFactory));
        acceptor.setHandler(handler);
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, jt808Config.getIdleTime() );
        // 设置缓冲区大小
        acceptor.getSessionConfig().setReadBufferSize( jt808Config.getReadBufferSize() );
        try {
            acceptor.bind(new InetSocketAddress( jt808Config.getPort() ));
            return true;
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean stop() {
        acceptor.dispose(false);
        return false;
    }
}

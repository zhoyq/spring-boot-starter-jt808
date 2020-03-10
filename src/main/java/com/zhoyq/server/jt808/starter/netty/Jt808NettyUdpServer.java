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

package com.zhoyq.server.jt808.starter.netty;

import com.zhoyq.server.jt808.starter.config.Jt808Config;
import com.zhoyq.server.jt808.starter.core.Jt808Server;
import com.zhoyq.server.jt808.starter.netty.coder.Jt808NettyDecoder;
import com.zhoyq.server.jt808.starter.netty.coder.Jt808NettyEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2019/12/17
 */
@Slf4j
@Component("jt808_netty_udp")
public class Jt808NettyUdpServer implements Jt808Server {

    private Channel serverChannel;

    @Autowired
    private Jt808Config jt808Config;

    @Autowired
    private NettySessionHandler handler;

    @Autowired
    private Jt808NettyEncoder encoder;
    @Autowired
    private Jt808NettyDecoder decoder;

    @Override
    public boolean start() {
        if (serverChannel != null) {
            return serverChannel.isActive();
        }

        EventLoopGroup masterGroup = new NioEventLoopGroup(jt808Config.getMasterSize());
        Bootstrap serverBootstrap = new Bootstrap();

        serverBootstrap
                .group(masterGroup)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
//                .handler(new LoggingHandler())
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel channel) throws Exception {
                        channel.pipeline().addLast(encoder);
                        channel.pipeline().addLast(decoder);
                        channel.pipeline().addLast(handler);
                    }
                });

        try{
            ChannelFuture channelFuture = serverBootstrap.bind(jt808Config.getPort()).sync();
            serverChannel = channelFuture.channel();
            serverChannel.closeFuture().sync();
            log.info("server start with port : {}", jt808Config.getPort());
            return true;
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
            return false;
        } finally {
            masterGroup.shutdownGracefully();
        }


    }

    @Override
    public boolean stop() {
        serverChannel.close();
        return true;
    }
}

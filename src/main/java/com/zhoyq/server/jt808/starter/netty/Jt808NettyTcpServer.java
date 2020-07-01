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

package com.zhoyq.server.jt808.starter.netty;

import com.zhoyq.server.jt808.starter.config.Jt808Config;
import com.zhoyq.server.jt808.starter.core.Jt808Server;
import com.zhoyq.server.jt808.starter.netty.coder.Jt808NettyDecoder;
import com.zhoyq.server.jt808.starter.netty.coder.Jt808NettyEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.LineEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2019/12/17
 */
@Slf4j
@Component("jt808_netty_tcp")
@AllArgsConstructor
public class Jt808NettyTcpServer implements Jt808Server {

    private static ServerChannel serverChannel;
    private static EventLoopGroup masterGroup;
    private static EventLoopGroup slaveGroup;

    private Jt808Config jt808Config;
    private NettySessionHandler handler;
    private Jt808NettyEncoder encoder;
    private Jt808NettyDecoder decoder;

    @Override
    public boolean start() {
        if (serverChannel != null) {
            return serverChannel.isActive();
        }

        masterGroup = new NioEventLoopGroup(jt808Config.getMasterSize());
        slaveGroup = new NioEventLoopGroup(jt808Config.getSlaveSize());
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap
                .group(masterGroup, slaveGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(encoder);
                        socketChannel.pipeline().addLast(decoder);
                        socketChannel.pipeline().addLast(handler);
                    }
                })
                .childOption(ChannelOption.TCP_NODELAY, jt808Config.getTcpNoDelay())
                .childOption(ChannelOption.SO_KEEPALIVE, jt808Config.getKeepAlive());

        ChannelFuture channelFuture = serverBootstrap.bind(jt808Config.getPort());
        serverChannel = (ServerChannel) channelFuture.channel();
        return true;
    }

    @Override
    public boolean stop() {
        serverChannel.close();
        masterGroup.shutdownGracefully();
        slaveGroup.shutdownGracefully();
        return true;
    }
}

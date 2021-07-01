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
import com.zhoyq.server.jt808.starter.core.HandlerWrapper;
import com.zhoyq.server.jt808.starter.core.PackHandlerManagement;
import com.zhoyq.server.jt808.starter.core.SessionManagement;
import com.zhoyq.server.jt808.starter.service.CacheService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/17
 */
@Slf4j
@AllArgsConstructor
public class NettySessionHandler extends ChannelInboundHandlerAdapter {
    SessionManagement sessionManagement;
    CacheService cacheService;
    PackHandlerManagement packHandlerManagement;
    Jt808Config jt808Config;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("session exception with {}", ctx.name());
        log.warn(cause.getMessage());
        // 异常时 关闭session 等待重连
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] originData  = (byte[])msg;
        log.debug("session received msg with id {} and msg {}", ctx.name(), Arrays.toString(originData));

        HandlerWrapper handlerWrapper = new HandlerWrapper(
                sessionManagement,
                cacheService,
                packHandlerManagement,
                jt808Config
        );
        handlerWrapper.init(originData);
        handlerWrapper.handleSession(ctx);
        handlerWrapper.handleMessage();
    }
}

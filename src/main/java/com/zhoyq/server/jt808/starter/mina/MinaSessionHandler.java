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

package com.zhoyq.server.jt808.starter.mina;

import com.zhoyq.server.jt808.starter.config.Jt808Config;
import com.zhoyq.server.jt808.starter.core.HandlerWrapper;
import com.zhoyq.server.jt808.starter.core.PackHandlerManagement;
import com.zhoyq.server.jt808.starter.core.SessionManagement;
import com.zhoyq.server.jt808.starter.service.CacheService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 会话处理器
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/16
 */
@Slf4j
@AllArgsConstructor
public class MinaSessionHandler extends IoHandlerAdapter {
    SessionManagement sessionManagement;
    CacheService cacheService;
    PackHandlerManagement packHandlerManagement;
    Jt808Config jt808Config;
    /**
     * 会话空闲
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus idleStatus){
        log.debug("session idle width {} ", session.getId());
        // 一定时间后未收到信息 关闭链接
        if(session.getIdleCount( idleStatus ) > jt808Config.getIdleCount()){
            session.closeNow();
        }
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable throwable) {
        log.debug("session exception with {}", session.getId());
        log.warn(throwable.getMessage());
        // 异常时 关闭session 等待重连
        session.closeNow();
    }

    /**
     * 获取消息
     */
    @Override
    public void messageReceived(IoSession session, Object msg) {
        byte[] originData  = (byte[])msg;
        log.debug("session received msg with id {} and msg {}", session.getId(), Arrays.toString(originData));

        HandlerWrapper handlerWrapper = new HandlerWrapper(
                sessionManagement,
                cacheService,
                packHandlerManagement,
                jt808Config
        );
        handlerWrapper.init(originData);
        handlerWrapper.handleSession(session);
        handlerWrapper.handleMessage();
    }
}

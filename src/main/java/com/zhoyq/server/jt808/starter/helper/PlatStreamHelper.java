/*
 *  Copyright (c) 2021. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2021.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2021. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.helper;

import com.zhoyq.server.jt808.starter.config.Const;
import com.zhoyq.server.jt808.starter.core.PackHandlerManagement;
import com.zhoyq.server.jt808.starter.core.SessionManagement;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;

/**
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/9
 */
@Slf4j
public class PlatStreamHelper {

    /**
     * 获取 平台 流水号
     * @param phoneNum 卡号
     */
    public static Integer getPlatStreamNum(byte[] phoneNum) {
        return getPkgPlatStreamNum(phoneNum, 1);
    }

    /**
     * 获取 平台 流水号
     * 任何异常会返回 0 作为流水号
     * @param phoneNum 卡号
     * @param number 获取数量
     */
    public static Integer getPkgPlatStreamNum(byte[] phoneNum, int number) {
        if (PackHandlerManagement.APPLICATION_CONTEXT == null) {
            log.warn("application has not been init yet!");
            return 0;
        }

        SessionManagement sessionManagement;

        try {
            sessionManagement = PackHandlerManagement.APPLICATION_CONTEXT.getBean(SessionManagement.class);
        } catch (Exception e) {
            log.warn("session management has not been init yet!");
            return 0;
        }

        String phone = ByteArrHelper.toHexString(phoneNum);
        Object session = sessionManagement.get(phone);

        if (session == null) {
            log.warn("can not find session of {}", phone);
            return 0;
        }

        int ret = 0;

        if (session instanceof IoSession) {
            IoSession ioSession = (IoSession)session;
            Object streamNumber = ioSession.getAttribute(Const.PLATFORM_STREAM_NUMBER);
            if (streamNumber != null) {
                ret = (int)streamNumber;
            }
            ioSession.setAttribute(Const.PLATFORM_STREAM_NUMBER, ret + number);
        } else if (session instanceof ChannelHandlerContext){
            ChannelHandlerContext ctx = (ChannelHandlerContext)session;
            Object streamNumber = ctx.channel().attr(AttributeKey.valueOf(Const.PLATFORM_STREAM_NUMBER)).get();
            if (streamNumber != null) {
                ret = (int)streamNumber;
            }
            ctx.channel().attr(AttributeKey.valueOf(Const.PLATFORM_STREAM_NUMBER)).set(ret + number);
        } else {
            log.warn("can not identify session for {}!", phone);
        }

        return ret;
    }
}

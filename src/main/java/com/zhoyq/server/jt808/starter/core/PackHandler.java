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

package com.zhoyq.server.jt808.starter.core;

/**
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2019/12/18
 */
public interface PackHandler {
    /**
     * 解析完成后对数据进行处理的方法
     * @param phoneNum 终端号码字节数组
     * @param streamNum 平台流水号
     * @param msgId 消息ID
     * @param msgBody 消息体
     * @return byte[] 返回应答消息
     */
    byte[] handle(byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody);
}

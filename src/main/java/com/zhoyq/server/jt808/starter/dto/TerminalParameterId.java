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

package com.zhoyq.server.jt808.starter.dto;

import lombok.Getter;

/**
 * 查询终端参数项ID
 * @author zhoyq
 * @date 2018-06-27
 */
@Getter
public enum TerminalParameterId {

    /**
     * 终端心跳发送间隔 单位 s
     */
    HeartbeatSplitTime(new byte[]{0x00, 0x01}),
    /**
     * TCP 消息应答超时时间 单位 s
     */
    TcpAnswerTimeout(new byte[]{0x00, 0x02}),
    /**
     * TCP 消息重传次数
     */
    TcpReSentNumber(new byte[]{0x00, 0x03}),
    /**
     * UDP 消息应答超时时间 单位 s
     */
    UdpAnswerTimeout(new byte[]{0x00, 0x04}),
    /**
     * UDP 消息重传次数
     */
    UdpReSentNumber(new byte[]{0x00, 0x05}),
    /**
     * SMS 消息应答超时时间 单位 s
     */
    SmsAnswerTimeout(new byte[]{0x00, 0x06}),
    /**
     * SMS 消息重传次数
     */
    SmsReSentNumber(new byte[]{0x00, 0x07}),

    // 0x0008 - 0x000F 保留

    /**
     * 主服务器APN 无线通信拨号访问点
     * 若网络制式为CDMA 则该处为 PPP 拨号号码
     */
    MainServer(new byte[]{0x00, 0x10}),
    /**
     * 主服务器无线通信拨号用户名
     */
    MainServerUserName(new byte[]{0x00, 0x11}),
    /**
     * 主服务器无线通信拨号密码
     */
    MainServerPassword(new byte[]{0x00, 0x12}),
    /**
     * 主服务器地址 IP 域名，以冒号分割主机和端口。多个服务器使用分号分割
     */
    MainServerAddr(new byte[]{0x00, 0x13}),
    /**
     * 备用服务器APN
     */
    SecondaryServer(new byte[]{0x00, 0x14}),
    /**
     * 备用服务器无线通信拨号用户名
     */
    SecondaryServerUserName(new byte[]{0x00, 0x15}),
    /**
     * 备用服务器无线通信拨号密码
     */
    SecondaryServerPassword(new byte[]{0x00, 0x16}),
    /**
     * 备用服务器地址 IP 域名，以冒号分割主机和端口。多个服务器使用分号分割
     */
    SecondaryServerAddr(new byte[]{0x00, 0x17}),

    // 0x0018 - 0x0019 保留

    /**
     * 道路运输证 IC 卡认证 主 服务器 IP地址或域名
     */
    IcMainServerAddr(new byte[]{0x00, 0x1A}),
    /**
     * 道路运输证 IC 卡认证 主 服务器 TCP 端口
     */
    IcMainServerTcpPort(new byte[]{0x00, 0x1B}),
    /**
     * 道路运输证 IC 卡认证 主 服务器 UDP 端口
     */
    IcMainServerUdpPort(new byte[]{0x00, 0x1C}),
    /**
     * 道路运输证 IC 卡认证 备份 服务器 IP地址或域名，端口同主服务器
     */
    IcSecondaryServerAddr(new byte[]{0x00, 0x1D}),

    // 0x001E - 0x001F 保留

    /**
     * 位置汇报策略
     * 0 定时汇报 1 定距汇报 2 定时和定距汇报
     */
    PositionReportStrategy(new byte[]{0x00, 0x20}),
    /**
     * 位置汇报方案
     * 0 根据ACC状态 1 根据登录状态和ACC状态（先判断登录状态若登录再根据ACC状态）
     */
    PositionReportMethod(new byte[]{0x00, 0x21}),
    /**
     * 驾驶员未登录汇报时间间隔 单位 s 大于0
     */
    DriverNotLoginReportSplitTime(new byte[]{0x00, 0x22}),
    /**
     * 从服务器 APN （空时，终端应使用主服务器相同配置）
     */
    FollowServerApn(new byte[]{0x00, 0x23}),
    /**
     * 从服务器无线通信拨号用户名 （空时，终端应使用主服务器相同配置）
     */
    FollowServerUserName(new byte[]{0x00, 0x24}),
    /**
     * 从服务器无线通信拨号密码（空时，终端应使用主服务器相同配置）
     */
    FollowServerPassword(new byte[]{0x00, 0x25}),
    /**
     * 从服务器备份地址 IP 域名 以冒号分割主机和端口。多个服务器使用分号分割
     */
    FollowServerAddr(new byte[]{0x00, 0x26}),
    /**
     * 休眠时 汇报时间间隔 单位s 大于0
     */
    DormantSplitTime(new byte[]{0x00, 0x27}),
    /**
     * 紧急报警时 汇报时间间隔 单位s 大于0
     */
    EmergencyAlarmSplitTime(new byte[]{0x00, 0x28}),
    /**
     * 缺省汇报时间间隔 单位s 大于0
     */
    DefaultSplitTime(new byte[]{0x00, 0x29}),

    // 0x002A - 0x002B 保留

    /**
     * 缺省汇报距离间隔 单位m 大于0
     */
    DefaultSplitDistance(new byte[]{0x00, 0x2C}),
    /**
     * 驾驶员未登录汇报距离间隔 单位m 大于0
     */
    DriverNotLoginReportSplitDistance(new byte[]{0x00, 0x2D}),
    /**
     * 休眠时 汇报 距离间隔 单位m 大于0
     */
    DormantSplitDistance(new byte[]{0x00, 0x2E}),
    /**
     * 紧急报警时 汇报 距离间隔 单位m 大于0
     */
    EmergencyAlarmSplitDistance(new byte[]{0x00, 0x2F}),

    // TODO

    // 苏标

    /**
     * 驾驶辅助功能参数
     */
    DriveAssistance(new byte[]{(byte)0xf3, 0x64}),
    /**
     * 驾驶员行为监测功能参数
     */
    DriverMotionMonitor(new byte[]{(byte)0xf3, 0x65}),
    /**
     *
     */
    TireStatusMonitor(new byte[]{(byte)0xf3, 0x66}),
    /**
     *
     */
    ChangeRoadAssistance(new byte[]{(byte)0xf3, 0x67}),
    IntenseDrivingMonitor(new byte[]{(byte)0xf3, 0x70}),

    ;

    private byte[] value;

    TerminalParameterId(byte[] value) {
        this.value = value;
    }
}


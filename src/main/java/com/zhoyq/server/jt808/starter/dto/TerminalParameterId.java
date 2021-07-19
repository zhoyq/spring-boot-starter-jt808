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
    HeartbeatSplitTime(new byte[]{0x00, 0x00, 0x00, 0x01}),
    /**
     * TCP 消息应答超时时间 单位 s
     */
    TcpAnswerTimeout(new byte[]{0x00, 0x00, 0x00, 0x02}),
    /**
     * TCP 消息重传次数
     */
    TcpReSentNumber(new byte[]{0x00, 0x00, 0x00, 0x03}),
    /**
     * UDP 消息应答超时时间 单位 s
     */
    UdpAnswerTimeout(new byte[]{0x00, 0x00, 0x00, 0x04}),
    /**
     * UDP 消息重传次数
     */
    UdpReSentNumber(new byte[]{0x00, 0x00, 0x00, 0x05}),
    /**
     * SMS 消息应答超时时间 单位 s
     */
    SmsAnswerTimeout(new byte[]{0x00, 0x00, 0x00, 0x06}),
    /**
     * SMS 消息重传次数
     */
    SmsReSentNumber(new byte[]{0x00, 0x00, 0x00, 0x07}),

    // 0x0008 - 0x000F 保留

    /**
     * 主服务器APN 无线通信拨号访问点
     * 若网络制式为CDMA 则该处为 PPP 拨号号码
     */
    MainServer(new byte[]{0x00, 0x00, 0x00, 0x10}),
    /**
     * 主服务器无线通信拨号用户名
     */
    MainServerUserName(new byte[]{0x00, 0x00, 0x00, 0x11}),
    /**
     * 主服务器无线通信拨号密码
     */
    MainServerPassword(new byte[]{0x00, 0x00, 0x00, 0x12}),
    /**
     * 主服务器地址 IP 域名，以冒号分割主机和端口。多个服务器使用分号分割
     */
    MainServerAddr(new byte[]{0x00, 0x00, 0x00, 0x13}),
    /**
     * 备用服务器APN
     */
    SecondaryServer(new byte[]{0x00, 0x00, 0x00, 0x14}),
    /**
     * 备用服务器无线通信拨号用户名
     */
    SecondaryServerUserName(new byte[]{0x00, 0x00, 0x00, 0x15}),
    /**
     * 备用服务器无线通信拨号密码
     */
    SecondaryServerPassword(new byte[]{0x00, 0x00, 0x00, 0x16}),
    /**
     * 备用服务器地址 IP 域名，以冒号分割主机和端口。多个服务器使用分号分割
     */
    SecondaryServerAddr(new byte[]{0x00, 0x00, 0x00, 0x17}),

    // 0x0018 - 0x0019 保留

    /**
     * 道路运输证 IC 卡认证 主 服务器 IP地址或域名
     */
    IcMainServerAddr(new byte[]{0x00, 0x00, 0x00, 0x1A}),
    /**
     * 道路运输证 IC 卡认证 主 服务器 TCP 端口
     */
    IcMainServerTcpPort(new byte[]{0x00, 0x00, 0x00, 0x1B}),
    /**
     * 道路运输证 IC 卡认证 主 服务器 UDP 端口
     */
    IcMainServerUdpPort(new byte[]{0x00, 0x00, 0x00, 0x1C}),
    /**
     * 道路运输证 IC 卡认证 备份 服务器 IP地址或域名，端口同主服务器
     */
    IcSecondaryServerAddr(new byte[]{0x00, 0x00, 0x00, 0x1D}),

    // 0x001E - 0x001F 保留

    /**
     * 位置汇报策略
     * 0 定时汇报 1 定距汇报 2 定时和定距汇报
     */
    PositionReportStrategy(new byte[]{0x00, 0x00, 0x00, 0x20}),
    /**
     * 位置汇报方案
     * 0 根据ACC状态 1 根据登录状态和ACC状态（先判断登录状态若登录再根据ACC状态）
     */
    PositionReportMethod(new byte[]{0x00, 0x00, 0x00, 0x21}),
    /**
     * 驾驶员未登录汇报时间间隔 单位 s 大于0
     */
    DriverNotLoginReportSplitTime(new byte[]{0x00, 0x00, 0x00, 0x22}),
    /**
     * 从服务器 APN （空时，终端应使用主服务器相同配置）
     */
    FollowServerApn(new byte[]{0x00, 0x00, 0x00, 0x23}),
    /**
     * 从服务器无线通信拨号用户名 （空时，终端应使用主服务器相同配置）
     */
    FollowServerUserName(new byte[]{0x00, 0x00, 0x00, 0x24}),
    /**
     * 从服务器无线通信拨号密码（空时，终端应使用主服务器相同配置）
     */
    FollowServerPassword(new byte[]{0x00, 0x00, 0x00, 0x25}),
    /**
     * 从服务器备份地址 IP 域名 以冒号分割主机和端口。多个服务器使用分号分割
     */
    FollowServerAddr(new byte[]{0x00, 0x00, 0x00, 0x26}),
    /**
     * 休眠时 汇报时间间隔 单位s 大于0
     */
    DormantSplitTime(new byte[]{0x00, 0x00, 0x00, 0x27}),
    /**
     * 紧急报警时 汇报时间间隔 单位s 大于0
     */
    EmergencyAlarmSplitTime(new byte[]{0x00, 0x00, 0x00, 0x28}),
    /**
     * 缺省汇报时间间隔 单位s 大于0
     */
    DefaultSplitTime(new byte[]{0x00, 0x00, 0x00, 0x29}),

    // 0x002A - 0x002B 保留

    /**
     * 缺省汇报距离间隔 单位m 大于0
     */
    DefaultSplitDistance(new byte[]{0x00, 0x00, 0x00, 0x2C}),
    /**
     * 驾驶员未登录汇报距离间隔 单位m 大于0
     */
    DriverNotLoginReportSplitDistance(new byte[]{0x00, 0x00, 0x00, 0x2D}),
    /**
     * 休眠时 汇报 距离间隔 单位m 大于0
     */
    DormantSplitDistance(new byte[]{0x00, 0x00, 0x00, 0x2E}),
    /**
     * 紧急报警时 汇报 距离间隔 单位m 大于0
     */
    EmergencyAlarmSplitDistance(new byte[]{0x00, 0x00, 0x00, 0x2F}),
    /**
     * 拐点补传角度 小于 180
     */
    TurningPointAngle(new byte[]{0x00, 0x00, 0x00, 0x30}),
    /**
     * 电子围栏半径（非法位移阈值） 单位 m
     */
    ElectronicFenceRadius(new byte[]{0x00, 0x00, 0x00, 0x31}),
    /**
     * 违规行驶时段 精确到分 （小时 分钟 到 小时 分钟）
     */
    IllegalDrivingTime(new byte[]{0x00, 0x00, 0x00, 0x32}),

    // 0x0033 - 0x003F 保留

    /**
     * 监控平台电话号码
     */
    MonitoringPlatformPhone(new byte[]{0x00, 0x00, 0x00, 0x40}),
    /**
     * 复位电话号码
     */
    ResetPhone(new byte[]{0x00, 0x00, 0x00, 0x41}),
    /**
     * 恢复出厂设置电话号码
     */
    FactoryDataResetPhone(new byte[]{0x00, 0x00, 0x00, 0x42}),
    /**
     * 监控平台 SMS 电话号码
     */
    MonitoringPlatformSMSPhone(new byte[]{0x00, 0x00, 0x00, 0x43}),
    /**
     * 接收终端 SMS 文本报警号码
     */
    ReceivingTerminalSMSAlarmPhone(new byte[]{0x00, 0x00, 0x00, 0x44}),
    /**
     * 终端电话接听策略
     * 0 自动接听 1 ACC ON 时 自动接听 OFF 时 手动接听
     */
    TerminalPhoneStrategy(new byte[]{0x00, 0x00, 0x00, 0x45}),
    /**
     * 每次最长通话时间 单位s 0 不允许通话 0xFFFFFFFF 为 不限制
     */
    EvetyTimeLongestTime(new byte[]{0x00, 0x00, 0x00, 0x46}),
    /**
     * 当月最长通话时间 单位s 0 不允许通话 0xFFFFFFFF 为 不限制
     */
    MonthLongestTime(new byte[]{0x00, 0x00, 0x00, 0x47}),
    /**
     * 监听电话号码
     */
    MonitorPhone(new byte[]{0x00, 0x00, 0x00, 0x48}),
    /**
     * 监管平台特权短信号码
     */
    RegulatoryPlatformPrivilegePhone(new byte[]{0x00, 0x00, 0x00, 0x49}),

    // 0x004A - 0x004F 保留

    /**
     * 报警屏蔽字 与位置信息汇报消息中的报警标志相对应 1 为报警屏蔽
     */
    PoliceBlockedWords(new byte[]{0x00, 0x00, 0x00, 0x50}),
    /**
     * 报警发送文本SMS开关 与位置信息汇报消息中的报警标志相对应 1 为报警时发送文本
     */
    AlarmTextSwitch(new byte[]{0x00, 0x00, 0x00, 0x51}),
    /**
     * 报警拍摄开关 与位置信息汇报消息中的报警标志相对应 1 为报警时拍摄
     */
    PoliceShotSwitch(new byte[]{0x00, 0x00, 0x00, 0x52}),
    /**
     * 报警拍摄存储 与位置信息汇报消息中的报警标志相对应 1 为报警时拍摄存储 0 为报警时拍摄上传
     */
    PoliceShotStorageSwitch(new byte[]{0x00, 0x00, 0x00, 0x53}),
    /**
     * 关键标志 与位置信息汇报消息中的报警标志相对应 1 为关键报警
     */
    AlarmKeySymbolSwitch(new byte[]{0x00, 0x00, 0x00, 0x54}),
    /**
     * 最高速度 单位 km/h
     */
    HighestSpeed(new byte[]{0x00, 0x00, 0x00, 0x55}),
    /**
     * 超速持续时间 单位 s
     */
    OverSpeedTime(new byte[]{0x00, 0x00, 0x00, 0x56}),
    /**
     * 连续驾驶时间门限 单位 s
     */
    ContinuousDrivingTimeThreshold(new byte[]{0x00, 0x00, 0x00, 0x57}),
    /**
     * 当天累计驾驶时间门限 单位 s
     */
    DailyDrivingTimeThreshold(new byte[]{0x00, 0x00, 0x00, 0x58}),
    /**
     * 最小休息时间 单位 s
     */
    MinRestTime(new byte[]{0x00, 0x00, 0x00, 0x59}),
    /**
     * 最长停车时间 单位 s
     */
    LongestStopTime(new byte[]{0x00, 0x00, 0x00, 0x5A}),
    /**
     * 超速预警差值 单位 1/10 km/h
     */
    OverSpeedWarning(new byte[]{0x00, 0x00, 0x00, 0x5B}),
    /**
     * 疲劳驾驶预警插值 单位 s 大于 0
     */
    FatigueDrivingWarning(new byte[]{0x00, 0x00, 0x00, 0x5C}),
    /**
     * 碰撞报警参数设置
     * b7 - b0 碰撞时间 单位 ms
     * b15 - b8 碰撞加速度 单位 0.1g 范围 [0, 79] 默认 10
     */
    CollisionAlarmParameters(new byte[]{0x00, 0x00, 0x00, 0x5D}),
    /**
     * 侧翻报警参数设置 侧翻角度 单位 度 默认 30度
     */
    CartwheelAlarmParameters(new byte[]{0x00, 0x00, 0x00, 0x5E}),

    // 0x005F - 0x0063 保留

    /**
     * 定时拍照控制
     */
    TimePhotographedControl(new byte[]{0x00, 0x00, 0x00, 0x64}),
    /**
     * 定距拍照控制
     */
    DistancePhotographedControl(new byte[]{0x00, 0x00, 0x00, 0x65}),

    // 0x0066 - 0x006F 保留

    /**
     * 图像/视频 质量 范围 [1, 10] 1 标识最优质量
     */
    ImageOrVideoQuality(new byte[]{0x00, 0x00, 0x00, 0x70}),
    /**
     * 亮度 范围 [0, 255]
     */
    ImageOrVideoBrightness(new byte[]{0x00, 0x00, 0x00, 0x71}),
    /**
     * 对比度 范围 [0, 127]
     */
    ImageOrVideoContrast(new byte[]{0x00, 0x00, 0x00, 0x72}),
    /**
     * 饱和度 范围 [0, 127]
     */
    ImageOrVideoSaturation(new byte[]{0x00, 0x00, 0x00, 0x73}),
    /**
     * 色度 范围 [0, 255]
     */
    ImageOrVideoChroma(new byte[]{0x00, 0x00, 0x00, 0x74}),

    // 0x0075 - 0x007F 在808中保留 在 1078 部分有定义
    /**
     * 音视频参数设置
     */
    AudioOrVideoSetting(new byte[]{0x00, 0x00, 0x00, 0x75}),
    /**
     * 音视频通道列表设置
     */
    AudioOrVideoChannelList(new byte[]{0x00, 0x00, 0x00, 0x76}),
    /**
     * 单独视频通道参数设置
     */
    SingleVideoChannelSetting(new byte[]{0x00, 0x00, 0x00, 0x77}),
    /**
     * 特殊报警录像参数设置
     */
    SpecialVideoSetting(new byte[]{0x00, 0x00, 0x00, 0x79}),
    /**
     * 视频相关报警屏蔽字
     */
    VideoAlarmShieldWord(new byte[]{0x00, 0x00, 0x00, 0x7A}),
    /**
     * 图像分析报警参数设置
     */
    ImageAnalyzeAlarmSetting(new byte[]{0x00, 0x00, 0x00, 0x7B}),
    /**
     * 终端休眠唤醒模式设置
     */
    TerminalAwakeModeSetting(new byte[]{0x00, 0x00, 0x00, 0x7C}),

    // 1078 部分结束

    /**
     * 车辆里程表读数 单位 1 / 10 km
     */
    VehicleOdometerReading(new byte[]{0x00, 0x00, 0x00, (byte)0x80}),
    /**
     * 车辆所在省域ID
     */
    VehicleProvinceDomainId(new byte[]{0x00, 0x00, 0x00, (byte)0x81}),
    /**
     * 车辆所在市域ID
     */
    VehicleCityDomainId(new byte[]{0x00, 0x00, 0x00, (byte)0x82}),
    /**
     * 公安交通管理部门颁发的机动车牌号
     */
    LicenseNumber(new byte[]{0x00, 0x00, 0x00, (byte)0x83}),
    /**
     * 车牌颜色 0 未上牌
     */
    LicenseColor(new byte[]{0x00, 0x00, 0x00, (byte)0x84}),

    /**
     * GNSS 定位模式
     * bit0 禁用/启用 GPS 定位
     * bit1 禁用/启用 北斗 定位
     * bit2 禁用/启用 GLONASS 定位
     * bit3 禁用/启用 Galileo 定位
     */
    GnssPositioningMode(new byte[]{0x00, 0x00, 0x00, (byte)0x90}),
    /**
     * GNSS 波特率
     * 0x00 4800
     * 0x01 9600
     * 0x02 19200
     * 0x03 38400
     * 0x04 57600
     * 0x05 115200
     */
    GnssBaudRate(new byte[]{0x00, 0x00, 0x00, (byte)0x91}),
    /**
     * GNSS 模块详细定位数据输出频率
     * 0x00 500ms
     * 0x01 1000ms 默认
     * 0x02 2000ms
     * 0x03 3000ms
     * 0x04 4000ms
     */
    GnssOutputFrequency(new byte[]{0x00, 0x00, 0x00, (byte)0x92}),
    /**
     * GNSS 模块详细定位数据采集频率 单位 s
     */
    GnssSamplingFrequency(new byte[]{0x00, 0x00, 0x00, (byte)0x93}),
    /**
     * GNSS 模块详细定位数据上传方式
     * 0x00 本地存储 不上传 默认
     * 0x01 按时间间隔上传
     * 0x02 按距离间隔上传
     * 0x0B 按累计时间上传 达到传输时间后自动停止上传
     * 0x0C 按累计距离上传 达到距离后自动停止上传
     * 0x0D 按累计条数上传 达到上传条数后自动停止上传
     */
    GnssDataUploadMode(new byte[]{0x00, 0x00, 0x00, (byte)0x94}),
    /**
     * GNSS 模块详细定位数据上传设置
     * 0x01 单位s
     * 0x02 单位m
     * 0x0B 单位s
     * 0x0C 单位m
     * 0x0D 单位条
     */
    GnssDataUploadSetting(new byte[]{0x00, 0x00, 0x00, (byte)0x95}),

    /**
     * CAN 总线通道1 采集时间间隔 单位 ms 0表示不采集
     */
    CanBusOneAcquisitionTimeInterval(new byte[]{0x00, 0x00, 0x01, 0x00}),
    /**
     * CAN 总线通道1 上传时间间隔 单位 s 0表示不上传
     */
    CanBusOneUploadTimeInterval(new byte[]{0x00, 0x00, 0x01, 0x01}),
    /**
     * CAN 总线通道2 采集时间间隔 单位 ms 0表示不采集
     */
    CanBusTwoAcquisitionTimeInterval(new byte[]{0x00, 0x00, 0x01, 0x02}),
    /**
     * CAN 总线通道2 上传时间间隔 单位 s 0表示不上传
     */
    CanBusTwoUploadTimeInterval(new byte[]{0x00, 0x00, 0x01, 0x03}),

    /**
     * CAN 总线ID单独采集设置
     * bit63 - 32 此ID采集时间间隔 ms 0表示不采集
     * bit31 CAN 通道号 0 CAN1 1 CAN2
     * bit30 帧类型 0 标准帧 1 扩展帧
     * bit29 数据采集方式 0 原始数据 1 采集区间的计算值
     * bit28 - 0 CAN 总线ID
     */
    CanIdAcquisitionSetting(new byte[]{0x00, 0x00, 0x01, 0x10}),

    // 0x0111 - 0x01FF 用于其他CAN总线ID单独采集设置

    // 0xF000 - 0xFFFF 厂商自定义

    /**
     * 苏标：驾驶辅助功能参数
     */
    DriveAssistance(new byte[]{0x00, 0x00, (byte)0xf3, 0x64}),
    /**
     * 苏标：驾驶员行为监测功能参数
     */
    DriverMotionMonitor(new byte[]{0x00, 0x00, (byte)0xf3, 0x65}),
    /**
     * 苏标：轮胎状态监测功能参数
     */
    TireStatusMonitor(new byte[]{0x00, 0x00, (byte)0xf3, 0x66}),
    /**
     * 苏标：变道决策辅助功能参数
     */
    ChangeRoadAssistance(new byte[]{0x00, 0x00, (byte)0xf3, 0x67}),
    /**
     * 苏标：激烈驾驶检测功能参数
     */
    IntenseDrivingMonitor(new byte[]{0x00, 0x00, (byte)0xf3, 0x70}),

    /**
     * 苏标：终端MAC地址 字母使用大写
     */
    TerminalMacAddr(new byte[]{0x00, 0x00, (byte) 0xff, 0x00}),

    /**
     * 苏标：扩展制造商ID 主动安全智能防控终端制造商编码
     */
    ExtManufacturerId(new byte[]{0x00, 0x00, (byte) 0xff, 0x01}),

    /**
     * 苏标：扩展终端型号 主动安全智能防控终端型号 位数不足时 后补0
     */
    ExtTerminalModelAddr(new byte[]{0x00, 0x00, (byte) 0xff, 0x02}),

    ;

    private byte[] value;

    TerminalParameterId(byte[] value) {
        this.value = value;
    }

    public static TerminalParameterId VALUE_OF(byte[] parameterId) {
        TerminalParameterId[] values = TerminalParameterId.values();
        for (TerminalParameterId id: values) {
            byte[] value = id.getValue();
            if (parameterId[0] == value[0] &&
                    parameterId[1] == value[1] &&
                    parameterId[2] == value[2] &&
                    parameterId[3] == value[3]
            ) {
                return id;
            }
        }
        return null;
    }
}


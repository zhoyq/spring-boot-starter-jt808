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

package com.zhoyq.server.jt808.starter.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/19
 */
@Setter
@Getter
public class SuDriveAssistanceAlarm implements SuAlarm{

    /**
     * 报警ID 从0开始循环累加
     */
    private int alarmId;
    /**
     * 标志状态
     * 0x00 不可用
     * 0x01 开始
     * 0x02 结束
     */
    private int alarmStatus;
    /**
     * 报警、事件类型
     * 0x01 前向碰撞预警
     * 0x02 车道偏离报警
     * 0x03 车距过近报警
     * 0x04 行人碰撞报警
     * 0x05 频繁变道报警
     * 0x06 道路标识超限报警
     * 0x07 障碍物报警
     * 0x08 驾驶辅助功能失效报警
     * 0x10 道路标志识别事件
     * 0x11 主动抓拍事件
     * 0x09 - 0x0F 0x12 - 0xFF 用户自定义
     */
    private int alarmType;
    /**
     * 报警级别
     * 0x01 一级报警
     * 0x02 二级报警
     */
    private int alarmLevel;
    /**
     * 前车车速
     * 单位 km/h
     * 范围 [0,250]
     * 仅报警类型为 0x01 0x02 时候有效 不可用时填 0x00
     */
    private int frontSpeed;
    /**
     * 前车/行人距离
     * 单位 100ms
     * 范围 [0,100]
     * 仅报警类型为 0x01 0x02 0x04 时候有效 不可用时填 0x00
     */
    private int frontDistance;
    /**
     * 偏离类型
     * 0x01 左侧偏离
     * 0x02 右侧偏离
     * 仅报警类型为 0x02 时有效 不可用时填 0x00
     */
    private int deviationType;
    /**
     * 道路标志识别类型
     * 0x01 限速标志
     * 0x02 限高标志
     * 0x03 限重标志
     * 仅报警类型为 0x06 0x10 时候有效 不可用时填 0x00
     */
    private int roadSignIdentificationCategory;
    /**
     * 道路标志识别数据
     * 不可用时填 0x00
     */
    private int roadSignIdentificationData;
    /**
     * 车速
     * 单位 km/h
     * 范围 [0, 250]
     */
    private int speed;
    /**
     * 高程
     * 单位 m
     */
    private int elevation;
    /**
     * 纬度
     * 以度为单位的纬度值乘以 10^6 精确到百万分之一度
     */
    private double latitude;
    /**
     * 经度
     * 以度为单位的经度值乘以 10^6 精确到百万分之一度
     */
    private double longitude;
    /**
     * 日期时间
     * YYMMDDhhmmss+8
     */
    private String datetime;
    /**
     * 车辆状态
     */
    private SuStatusInfo vehicleStatus;
    /**
     * 报警标识号
     */
    private SuAlarmIdentificationNumber alarmIdentificationNumber;

    @Override
    public SuAlarmIdentificationNumber alarmIdentificationNumber() {
        return alarmIdentificationNumber;
    }

    /**
     * TODO 从字节数组中获取 对象数据
     */
    public static SuDriveAssistanceAlarm fromBytes(byte[] data) {
        return null;
    }

}

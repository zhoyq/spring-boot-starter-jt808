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

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 驾驶员辅助功能报警
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/19
 */
@Slf4j
@Setter
@Getter
public class SuDriveAssistanceAlarm implements SuAlarm{

    /**
     * 报警ID 从0开始循环累加
     */
    private byte[] alarmId;

    public int alarmId() {
        return ByteArrHelper.fourbyte2int(alarmId);
    }

    /**
     * 标志状态
     * 0x00 不可用
     * 0x01 开始
     * 0x02 结束
     */
    private byte alarmStatus;

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
     * 0x09 - 0x0F 用户自定义
     * 0x10 道路标志识别事件
     * 0x11 主动抓拍事件
     * 0x12 - 0xFF 用户自定义
     */
    private byte alarmType;

    /**
     * 报警级别
     * 0x01 一级报警
     * 0x02 二级报警
     */
    private byte alarmLevel;

    /**
     * 前车车速
     * 单位 km/h
     * 范围 [0,250]
     * 仅报警类型为 0x01 0x02 时候有效 不可用时填 0x00
     */
    private byte frontSpeed;

    /**
     * 前车/行人距离
     * 单位 100ms
     * 范围 [0,100]
     * 仅报警类型为 0x01 0x02 0x04 时候有效 不可用时填 0x00
     */
    private byte frontDistance;

    /**
     * 偏离类型
     * 0x01 左侧偏离
     * 0x02 右侧偏离
     * 仅报警类型为 0x02 时有效 不可用时填 0x00
     */
    private byte deviationType;

    /**
     * 道路标志识别类型
     * 0x01 限速标志
     * 0x02 限高标志
     * 0x03 限重标志
     * 仅报警类型为 0x06 0x10 时候有效 不可用时填 0x00
     */
    private byte roadSignIdentificationCategory;

    /**
     * 道路标志识别数据
     * 不可用时填 0x00
     */
    private byte roadSignIdentificationData;

    /**
     * 车速
     * 单位 km/h
     * 范围 [0, 250]
     */
    private byte speed;

    /**
     * 高程
     * 单位 m
     */
    private byte[] elevation;

    /**
     * 纬度
     * 以度为单位的纬度值乘以 10^6 精确到百万分之一度
     */
    private byte[] latitude;

    public double latitude() {
        return Jt808Helper.bytes2latlon(this.latitude);
    }

    /**
     * 经度
     * 以度为单位的经度值乘以 10^6 精确到百万分之一度
     */
    private byte[] longitude;

    public double longitude() {
        return Jt808Helper.bytes2latlon(this.longitude);
    }

    /**
     * 日期时间
     * YYMMDDhhmmss+8
     */
    private byte[] datetime;

    public Date datetime() {
        return Jt808Helper.bytes2date(this.datetime);
    }

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

    public byte[] toBytes() {
        return ByteArrHelper.union(
                alarmId,
                new byte[]{
                        alarmStatus, alarmType, alarmLevel, frontSpeed, frontDistance,
                        deviationType, roadSignIdentificationCategory, roadSignIdentificationData, speed
                },
                elevation,
                latitude,
                longitude,
                datetime,
                vehicleStatus.toBytes(),
                alarmIdentificationNumber.toBytes()
        );
    }

    /**
     * 从字节数组中获取 对象数据
     */
    public static SuDriveAssistanceAlarm fromBytes(byte[] data) {
        if (data.length  != 47) {
            log.warn("SuDriveAssistanceAlarm fromBytes data is too long!");
            return null;
        }

        SuDriveAssistanceAlarm ins = new SuDriveAssistanceAlarm();
        ins.setAlarmId(ByteArrHelper.subByte(data, 0, 4));
        ins.setAlarmStatus(data[4]);
        ins.setAlarmType(data[5]);
        ins.setAlarmLevel(data[6]);
        ins.setFrontSpeed(data[7]);
        ins.setFrontDistance(data[8]);
        ins.setDeviationType(data[9]);
        ins.setRoadSignIdentificationCategory(data[10]);
        ins.setRoadSignIdentificationData(data[11]);
        ins.setSpeed(data[12]);
        ins.setElevation(ByteArrHelper.subByte(data, 13, 15));
        ins.setLatitude(ByteArrHelper.subByte(data, 15, 19));
        ins.setLongitude(ByteArrHelper.subByte(data, 19, 23));
        ins.setDatetime(ByteArrHelper.subByte(data, 23, 29));
        ins.setVehicleStatus(SuStatusInfo.fromBytes(ByteArrHelper.subByte(data, 29, 31)));
        ins.setAlarmIdentificationNumber(SuAlarmIdentificationNumber.fromBytes(ByteArrHelper.subByte(data, 31, 47)));
        return ins;
    }

}

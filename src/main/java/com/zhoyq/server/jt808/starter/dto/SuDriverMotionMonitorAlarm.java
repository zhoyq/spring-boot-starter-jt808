package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 驾驶员行为监测
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-20
 */
@Slf4j
@Setter
@Getter
public class SuDriverMotionMonitorAlarm implements SuAlarm{
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
     * 0x01 疲劳驾驶报警
     * 0x02 接打手持电话报警
     * 0x03 抽烟报警
     * 0x04 长时间不目视前方报警
     * 0x05 未检测到驾驶员报警
     * 0x06 双手同时脱离方向盘报警
     * 0x07 驾驶员行为监测功能失效报警
     * 0x08 - 0x0F 用户自定义
     * 0x10 自动抓拍事件
     * 0x11 驾驶员变更事件
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
     * 疲劳程度
     * 范围 [0, 10]
     * 数值越大表示疲劳程度越严重
     * 仅在报警类型为 0x01 时有效，不可用时填 0x00
     */
    private byte fatigue;

    // 四字节预留

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
                        alarmStatus, alarmType, alarmLevel, fatigue, 0x00, 0x00, 0x00, 0x00, speed
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
    public static SuDriverMotionMonitorAlarm fromBytes(byte[] data) {
        if (data.length  != 47) {
            log.warn("SuDriverMotionMonitorAlarm fromBytes data is too long!");
            return null;
        }

        SuDriverMotionMonitorAlarm ins = new SuDriverMotionMonitorAlarm();
        ins.setAlarmId(ByteArrHelper.subByte(data, 0, 4));
        ins.setAlarmStatus(data[4]);
        ins.setAlarmType(data[5]);
        ins.setAlarmLevel(data[6]);
        ins.setFatigue(data[7]);
        // 四字节预留
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

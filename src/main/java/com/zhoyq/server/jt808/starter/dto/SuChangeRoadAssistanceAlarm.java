package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 变道决策辅助报警
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-20
 */
@Slf4j
@Setter
@Getter
public class SuChangeRoadAssistanceAlarm implements SuAlarm {

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
     * 0x01 后方接近报警
     * 0x02 左侧后方接近报警
     * 0x03 右侧后方接近报警
     */
    private byte alarmType;

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
                        alarmStatus, alarmType, speed
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
    public static SuChangeRoadAssistanceAlarm fromBytes(byte[] data) {
        if (data.length  != 41) {
            log.warn("SuChangeRoadAssistanceAlarm fromBytes data is too long!");
            return null;
        }

        SuChangeRoadAssistanceAlarm ins = new SuChangeRoadAssistanceAlarm();
        ins.setAlarmId(ByteArrHelper.subByte(data, 0, 4));
        ins.setAlarmStatus(data[4]);
        ins.setAlarmType(data[5]);
        // 四字节预留
        ins.setSpeed(data[6]);
        ins.setElevation(ByteArrHelper.subByte(data, 7, 9));
        ins.setLatitude(ByteArrHelper.subByte(data, 9, 13));
        ins.setLongitude(ByteArrHelper.subByte(data, 13, 17));
        ins.setDatetime(ByteArrHelper.subByte(data, 17, 23));
        ins.setVehicleStatus(SuStatusInfo.fromBytes(ByteArrHelper.subByte(data, 23, 25)));
        ins.setAlarmIdentificationNumber(SuAlarmIdentificationNumber.fromBytes(ByteArrHelper.subByte(data, 25, 41)));
        return ins;
    }
}

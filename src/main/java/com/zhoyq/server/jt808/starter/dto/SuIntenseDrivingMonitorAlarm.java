package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 激烈驾驶报警
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-20
 */
@Slf4j
@Setter
@Getter
public class SuIntenseDrivingMonitorAlarm implements SuAlarm{
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
     * 0x01 急加速报警
     * 0x02 急减速报警
     * 0x03 急转弯报警
     * 0x04 怠速报警
     * 0x05 异常熄火报警
     * 0x06 空挡滑行报警
     * 0x07 发动机超转报警
     * 0x12 - 0xFF 用户自定义
     */
    private byte alarmType;

    /**
     * 报警时间阈值
     * 单位s
     */
    private byte[] alarmTimeLimit;

    /**
     * 报警阈值1
     * 当报警类型为 0x01 - 0x03 时，该位为报警重力加速度阈值 单位 1/100g
     * 当报警类型为 0x04 - 0x07 时，该位为报警车速阈值，单位 km/h
     */
    private byte[] alarmLimit1;

    /**
     * 报警阈值2
     * 当报警类型为 0x01 - 0x03 时，该位预留
     * 当报警类型为 0x04 - 0x07 时，该位为报警发动机转速阈值 单位 RPM
     */
    private byte[] alarmLimit2;

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
                new byte[]{ alarmStatus, alarmType },
                alarmTimeLimit,
                alarmLimit1,
                alarmLimit2,
                new byte[]{speed},
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
    public static SuIntenseDrivingMonitorAlarm fromBytes(byte[] data) {
        if (data.length  != 47) {
            log.warn("SuIntenseDrivingMonitorAlarm fromBytes data is too long!");
            return null;
        }

        SuIntenseDrivingMonitorAlarm ins = new SuIntenseDrivingMonitorAlarm();
        ins.setAlarmId(ByteArrHelper.subByte(data, 0, 4));
        ins.setAlarmStatus(data[4]);
        ins.setAlarmType(data[5]);
        ins.setAlarmTimeLimit(ByteArrHelper.subByte(data, 6, 8));
        ins.setAlarmLimit1(ByteArrHelper.subByte(data, 8, 10));
        ins.setAlarmLimit2(ByteArrHelper.subByte(data, 10, 12));
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

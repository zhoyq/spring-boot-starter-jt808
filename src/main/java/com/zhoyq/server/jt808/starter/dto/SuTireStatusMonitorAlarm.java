package com.zhoyq.server.jt808.starter.dto;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 轮胎状态监测报警信息
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2021-04-20
 */
@Slf4j
@Setter
@Getter
public class SuTireStatusMonitorAlarm implements SuAlarm {

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

    /**
     * 报警/事件列表总数 以及 数据
     */
    private byte[] tireAlarm;

    private int tireAlarmNumber() {
        return tireAlarm[0];
    }

    private List<SuTireAlarm> tireAlarmList() {
        int num = this.tireAlarmNumber();
        if (num * 9 != this.tireAlarm.length - 1) {
            log.warn("SuTireStatusMonitorAlarm.tireAlarmList fromBytes data is bad!");
            return null;
        }

        List<SuTireAlarm> ret = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            byte[] data = ByteArrHelper.subByte(this.tireAlarm, 1 + i * 9, 1 + (i + 1) * 9);
            ret.add(SuTireAlarm.fromBytes(data));
        }
        return ret;
    }

    public byte[] toBytes() {
        return ByteArrHelper.union(
                alarmId,
                new byte[]{ alarmStatus, speed },
                elevation,
                latitude,
                longitude,
                datetime,
                vehicleStatus.toBytes(),
                alarmIdentificationNumber.toBytes(),
                tireAlarm
        );
    }

    /**
     * 从字节数组中获取 对象数据
     */
    public static SuTireStatusMonitorAlarm fromBytes(byte[] data) {
        if (data.length < 41) {
            log.warn("SuTireStatusMonitorAlarm fromBytes data is too long!");
            return null;
        }

        SuTireStatusMonitorAlarm ins = new SuTireStatusMonitorAlarm();
        ins.setAlarmId(ByteArrHelper.subByte(data, 0, 4));
        ins.setAlarmStatus(data[4]);
        ins.setSpeed(data[5]);
        ins.setElevation(ByteArrHelper.subByte(data, 6, 8));
        ins.setLatitude(ByteArrHelper.subByte(data, 8, 12));
        ins.setLongitude(ByteArrHelper.subByte(data, 12, 16));
        ins.setDatetime(ByteArrHelper.subByte(data, 16, 22));
        ins.setVehicleStatus(SuStatusInfo.fromBytes(ByteArrHelper.subByte(data, 22, 24)));
        ins.setAlarmIdentificationNumber(SuAlarmIdentificationNumber.fromBytes(ByteArrHelper.subByte(data, 24, 40)));
        ins.setTireAlarm(ByteArrHelper.subByte(data, 40));
        return ins;
    }

    @Slf4j
    @Getter
    @Setter
    public static class SuTireAlarm {
        /**
         * 胎压报警位置
         * 报警轮胎位置编号
         * （从左前轮开始以Z字形从 00 依次编号，编号与是否安装TPMS无关）
         */
        private byte position;

        /**
         * 报警事件类型
         * 0 表示无报警 1 表示有报警
         * bit0 胎压 （ 定时上报 ）
         * bit1 胎压过高报警
         * bit2 胎压过低报警
         * bit3 胎温过高报警
         * bit4 传感器异常报警
         * bit5 胎压不平衡报警
         * bit6 慢漏气报警
         * bit7 电池电量低报警
         * bit8 - bit15 自定义
         */
        private byte[] alarmType;

        /**
         * 轮胎压强
         * 单位 kPa
         */
        private byte[] tirePress;

        /**
         * 轮胎温度
         * 单位 摄氏度
         */
        private byte[] tireTemp;

        /**
         * 电池电量
         * 单位 百分比
         */
        private byte[] power;

        public static SuTireAlarm fromBytes(byte[] data) {
            if (data.length != 9) {
                log.warn("SuTireStatusMonitorAlarm fromBytes data is too long!");
                return null;
            }

            SuTireAlarm ins = new SuTireAlarm();
            ins.setPosition(data[0]);
            ins.setAlarmType(new byte[]{data[1], data[2]});
            ins.setTirePress(new byte[]{data[3], data[4]});
            ins.setTireTemp(new byte[]{data[5], data[6]});
            ins.setPower(new byte[]{data[7], data[8]});
            return ins;
        }
    }
}

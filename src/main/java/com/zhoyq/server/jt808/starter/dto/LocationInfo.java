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
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 定位信息
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/18
 */
@Setter
@Getter
public class LocationInfo {
    /**
     * 报警信息
     */
    private AlarmInfo alarmInfo;

    /**
     * 状态信息
     */
    private StatusInfo statusInfo;

    /**
     * 纬度
     */
    private double longitude;

    /**
     * 经度
     */
    private double latitude;

    /**
     * 高程
     */
    private int height;

    /**
     * 速度
     */
    private double speed;

    /**
     * 方向
     */
    private int direction;

    /**
     * 时间 YY-MM-DD-hh-mm-ss GMT+8 时间
     */
    private String datetime;

    /**
     * 附加信息
     */
    private List<LocationAttachInfo> attachInfo;

    /**
     * 获取苏标报警标识号 用于下发上传附件命令
     */
    public List<SuAlarmIdentificationNumber> getSuAlarmIdentificationNumberList() {
        List<SuAlarmIdentificationNumber> ret = new ArrayList<>();
        List<LocationAttachInfo> attachInfoList = this.getAttachInfo();

        if (attachInfoList != null && attachInfoList.size() > 0) {
            for (LocationAttachInfo locationAttachInfo : attachInfoList) {
                SuAlarm alarm = null;
                switch (locationAttachInfo.getId()){
                    case 0x64:
                        alarm = locationAttachInfo.getSuDriveAssistanceAlarm();
                        break;
                    case 0x65:
                        alarm = locationAttachInfo.getSuDriverMotionMonitorAlarm();
                        break;
                    case 0x66:
                        alarm = locationAttachInfo.getSuTireStatusMonitorAlarm();
                        break;
                    case 0x67:
                        alarm = locationAttachInfo.getSuChangeRoadAssistanceAlarm();
                        break;
                    case 0x70:
                        alarm = locationAttachInfo.getSuIntenseDrivingMonitorAlarm();
                        break;
                }

                if (alarm != null) {
                    ret.add(alarm.alarmIdentificationNumber());
                }
            }
        }

        return ret;
    }


    public static LocationInfo fromBytes(byte[] data) {
        // 报警标识
        byte[] alarms = ByteArrHelper.subByte(data, 0, 4);
        // 状态
        byte[] status = ByteArrHelper.subByte(data, 4, 8);
        // 纬度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度]
        byte[] latitude = ByteArrHelper.subByte(data, 8, 12);
        // 经度[以度为单位的值乘以 10 的 6 次方，精确到百万分之一度]
        byte[] longitude = ByteArrHelper.subByte(data, 12, 16);
        // 高程 [单位 m]
        byte[] height = ByteArrHelper.subByte(data, 16, 18);
        // 速度 [单位 0.1 km/h]
        byte[] speed = ByteArrHelper.subByte(data, 18, 20);
        // 方向 [0~359 正北为0 顺时针]
        byte[] direction = ByteArrHelper.subByte(data, 20, 22);
        // 时间 [yy-mm-dd-hh-mm-ss]
        byte[] datetime = ByteArrHelper.subByte(data, 22, 28);
        // 附加
        byte[] attache = ByteArrHelper.subByte(data, 28);

        AlarmInfo alarmInfo = AlarmInfo.fromBytes(alarms);
        StatusInfo statusInfo = StatusInfo.fromBytes(status);
        double longitudeDouble = (double)ByteArrHelper.fourbyte2int(longitude) / (double)1000000;
        double latitudeDouble = (double)ByteArrHelper.fourbyte2int(latitude) / (double)1000000;
        int heightInt = ByteArrHelper.twobyte2int(height);
        double speedDouble = (double) (ByteArrHelper.twobyte2int(speed)) / (double) 10;
        int directionInt = ByteArrHelper.twobyte2int(direction);
        String datetimeString = Jt808Helper.getDataTime(datetime);
        List<LocationAttachInfo> attachInfoList = null;
        if (attache.length > 0) {
            int pos = 0;
            attachInfoList = new ArrayList<>();
            while(pos < attache.length){
                int length = attache[pos + 1];
                int totalLength = length + 2;
                byte[] buf = ByteArrHelper.subByte(attache, pos, pos + totalLength);
                pos += totalLength;
                attachInfoList.add(LocationAttachInfo.fromBytes(buf));
            }
        }

        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setAlarmInfo(alarmInfo);
        locationInfo.setStatusInfo(statusInfo);
        locationInfo.setLongitude(longitudeDouble);
        locationInfo.setLatitude(latitudeDouble);
        locationInfo.setHeight(heightInt);
        locationInfo.setSpeed(speedDouble);
        locationInfo.setDirection(directionInt);
        locationInfo.setDatetime(datetimeString);
        locationInfo.setAttachInfo(attachInfoList);
        return locationInfo;
    }
}

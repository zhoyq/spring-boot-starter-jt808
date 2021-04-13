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

import com.zhoyq.server.jt808.starter.config.Const;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 车辆定位中的状态信息
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/18
 */
@Setter
@Getter
public class StatusInfo {
    /**
     * 0：ACC 关；1： ACC 开
     */
    private boolean acc;

    /**
     * 0：未定位；1：定位
     */
    private boolean positioning;

    /**
     * 0：北纬；1：南纬
     */
    private boolean south;

    /**
     * 0：东经；1：西经
     */
    private boolean west;

    /**
     * 0：运营状态；1：停运状态
     */
    private boolean suspended;

    /**
     * 0：经纬度未经保密插件加密；1：经纬度已经保密插件加密
     */
    private boolean encryption;

    /**
     * 1 紧急刹车系统采集的前撞预警
     */
    private boolean brakeSystemWarning;

    /**
     * 1 车道偏移预警
     */
    private boolean laneDepartureWarning;

    /**
     * 0：空车；1：半载；2：保留；3：满载
     */
    private int cargo;

    /**
     * 0：车辆油路正常；1：车辆油路断开
     */
    private boolean oilBreak;

    /**
     * 0：车辆电路正常；1：车辆电路断开
     */
    private boolean circuitBreak;

    /**
     * 0：车门解锁；1：车门加锁
     */
    private boolean locking;

    /**
     * 0：门 1 关；1：门 1 开（前门）
     */
    private boolean opening1;

    /**
     * 0：门 2 关；1：门 2 开（中门）
     */
    private boolean opening2;

    /**
     * 0：门 3 关；1：门 3 开（后门）
     */
    private boolean opening3;

    /**
     * 0：门 4 关；1：门 4 开（驾驶席门）
     */
    private boolean opening4;

    /**
     * 0：门 5 关；1：门 5 开（自定义）
     */
    private boolean opening5;

    /**
     * 0：未使用 GPS 卫星进行定位；1：使用 GPS 卫星进行定位
     */
    private boolean gps;

    /**
     * 0：未使用北斗卫星进行定位；1：使用北斗卫星进行定位
     */
    private boolean beidou;

    /**
     * 0：未使用 GLONASS 卫星进行定位；1：使用 GLONASS 卫星进行定位
     */
    private boolean glonass;

    /**
     * 0：未使用 Galileo 卫星进行定位；1：使用 Galileo 卫星进行定位
     */
    private boolean galileo;

    /**
     * 0 车辆处于停止状态 1 车辆处于行驶状态
     */
    private boolean vehicleStatus;

    public static StatusInfo fromBytes(byte[] status) {
        StatusInfo statusInfo = new StatusInfo();
        statusInfo.setAcc((status[3] & Const.BIN_0X01) == Const.BIN_0X01);
        statusInfo.setPositioning((status[3] & Const.BIN_0X02) == Const.BIN_0X02);
        statusInfo.setSouth((status[3] & Const.BIN_0X04) == Const.BIN_0X04);
        statusInfo.setWest((status[3] & Const.BIN_0X08) == Const.BIN_0X08);
        statusInfo.setSuspended((status[3] & Const.BIN_0X10) == Const.BIN_0X10);
        statusInfo.setEncryption((status[3] & Const.BIN_0X20) == Const.BIN_0X20);
        statusInfo.setBrakeSystemWarning((status[3] & Const.BIN_0X40) == Const.BIN_0X40);
        statusInfo.setLaneDepartureWarning((status[3] & Const.BIN_0X80) == Const.BIN_0X80);
        statusInfo.setCargo(status[2] & 0x03);
        statusInfo.setOilBreak((status[2] & Const.BIN_0X04) == Const.BIN_0X04);
        statusInfo.setCircuitBreak((status[2] & Const.BIN_0X08) == Const.BIN_0X08);
        statusInfo.setLocking((status[2] & Const.BIN_0X10) == Const.BIN_0X10);
        statusInfo.setOpening1((status[2] & Const.BIN_0X20) == Const.BIN_0X20);
        statusInfo.setOpening2((status[2] & Const.BIN_0X40) == Const.BIN_0X40);
        statusInfo.setOpening3((status[2] & Const.BIN_0X80) == Const.BIN_0X80);
        statusInfo.setOpening4((status[1] & Const.BIN_0X01) == Const.BIN_0X01);
        statusInfo.setOpening5((status[1] & Const.BIN_0X02) == Const.BIN_0X02);
        statusInfo.setGps((status[1] & Const.BIN_0X04) == Const.BIN_0X04);
        statusInfo.setBeidou((status[1] & Const.BIN_0X08) == Const.BIN_0X08);
        statusInfo.setGlonass((status[1] & Const.BIN_0X10) == Const.BIN_0X10);
        statusInfo.setGalileo((status[1] & Const.BIN_0X20) == Const.BIN_0X20);
        statusInfo.setVehicleStatus((status[1] & Const.BIN_0X40) == Const.BIN_0X40);
        // 23 - 31 保留
        return statusInfo;
    }
}

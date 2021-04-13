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
 * 位置上报中的报警信息
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/18
 */
@Getter
@Setter
public class AlarmInfo {
    /**
     * 紧急报警
     */
    private boolean emergencyAlarm;

    /**
     * 超速报警 持续报警
     */
    private boolean overSpeedAlarm;

    /**
     * 疲劳驾驶报警 持续报警
     */
    private boolean fatigueDrivingAlarm;

    /**
     * 危险预警
     */
    private boolean dangerWarning;

    /**
     * GNSS 模块发生故障 持续报警
     */
    private boolean gnssModuleFault;

    /**
     * GNSS 天线未接或被剪断 持续报警
     */
    private boolean gnssConnectFault;

    /**
     * GNSS 天线短路 持续报警
     */
    private boolean gnssShortCircuit;

    /**
     * 终端主电源欠压 持续报警
     */
    private boolean powerUnderpressure;

    /**
     * 终端主电源掉电 持续报警
     */
    private boolean powerFault;

    /**
     * 终端LCD或显示器故障 持续报警
     */
    private boolean lcdFault;

    /**
     * TTS模块故障 持续报警
     */
    private boolean ttsFault;

    /**
     * 摄像头故障 持续报警
     */
    private boolean CameraFault;

    /**
     * 道路运输证 IC 卡模块故障 持续报警
     */
    private boolean icModuleFault;

    /**
     * 超速预警 持续报警
     */
    private boolean overSpeedWarn;

    /**
     * 疲劳驾驶预警 持续报警
     */
    private boolean fatigueDrivingWarn;

    /**
     * 违规行驶报警 持续报警
     */
    private boolean driverAgainstRules;

    /**
     * 胎压预警 持续报警
     */
    private boolean tirePressureWarning;

    /**
     * 右转盲区异常报警 持续报警
     */
    private boolean rightTurnBlindArea;

    /**
     * 当天累计驾驶超时 持续报警
     */
    private boolean cumulativeDrivingTimeout;

    /**
     * 超时停车 持续报警
     */
    private boolean stopTimeout;

    /**
     * 进出区域
     */
    private boolean inArea;

    /**
     * 进出路线
     */
    private boolean outLine;

    /**
     * 路段行驶时间不足/过长
     */
    private boolean drivingTimeIncorrect;

    /**
     * 路线偏离报警 持续报警
     */
    private boolean routeDeviation;

    /**
     * 车辆 VSS 故障 持续报警
     */
    private boolean vssFault;

    /**
     * 车辆油量异常 持续报警
     */
    private boolean oilFault;

    /**
     * 车辆被盗(通过车辆防盗器) 持续报警
     */
    private boolean stolenVehicle;

    /**
     * 车辆非法点火
     */
    private boolean illegalIgnition;

    /**
     * 车辆非法位移
     */
    private boolean illegalDisplacement;

    /**
     * 碰撞预警 持续报警
     */
    private boolean collisionWarn;

    /**
     * 侧翻预警 持续报警
     */
    private boolean rollOverWarn;

    /**
     * 非法开门报警（终端未设置区域时，不判断非法开门）
     */
    private boolean illegalOpeningTheDoor;

    public static AlarmInfo fromBytes(byte[] data) {
        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setEmergencyAlarm((data[3] & Const.BIN_0X01) == Const.BIN_0X01);
        alarmInfo.setOverSpeedAlarm((data[3] & Const.BIN_0X02) == Const.BIN_0X02);
        alarmInfo.setFatigueDrivingAlarm((data[3] & Const.BIN_0X04) == Const.BIN_0X04);
        alarmInfo.setDangerWarning((data[3] & Const.BIN_0X08) == Const.BIN_0X08);
        alarmInfo.setGnssModuleFault((data[3] & Const.BIN_0X10) == Const.BIN_0X10);
        alarmInfo.setGnssConnectFault((data[3] & Const.BIN_0X20) == Const.BIN_0X20);
        alarmInfo.setGnssShortCircuit((data[3] & Const.BIN_0X40) == Const.BIN_0X40);
        alarmInfo.setPowerUnderpressure((data[3] & Const.BIN_0X80) == Const.BIN_0X80);
        alarmInfo.setPowerFault((data[2] & Const.BIN_0X01) == Const.BIN_0X01);
        alarmInfo.setLcdFault((data[2] & Const.BIN_0X02) == Const.BIN_0X02);
        alarmInfo.setTtsFault((data[2] & Const.BIN_0X04) == Const.BIN_0X04);
        alarmInfo.setCameraFault((data[2] & Const.BIN_0X08) == Const.BIN_0X08);
        alarmInfo.setIcModuleFault((data[2] & Const.BIN_0X10) == Const.BIN_0X10);
        alarmInfo.setOverSpeedWarn((data[2] & Const.BIN_0X20) == Const.BIN_0X20);
        alarmInfo.setFatigueDrivingWarn((data[2] & Const.BIN_0X40) == Const.BIN_0X40);
        alarmInfo.setDriverAgainstRules((data[2] & Const.BIN_0X80) == Const.BIN_0X80);
        alarmInfo.setTirePressureWarning((data[1] & Const.BIN_0X01) == Const.BIN_0X01);
        alarmInfo.setRightTurnBlindArea((data[1] & Const.BIN_0X02) == Const.BIN_0X02);
        alarmInfo.setCumulativeDrivingTimeout((data[1] & Const.BIN_0X04) == Const.BIN_0X04);
        alarmInfo.setStopTimeout((data[1] & Const.BIN_0X08) == Const.BIN_0X08);
        alarmInfo.setInArea((data[1] & Const.BIN_0X10) == Const.BIN_0X10);
        alarmInfo.setOutLine((data[1] & Const.BIN_0X20) == Const.BIN_0X20);
        alarmInfo.setDrivingTimeIncorrect((data[1] & Const.BIN_0X40) == Const.BIN_0X40);
        alarmInfo.setRouteDeviation((data[1] & Const.BIN_0X80) == Const.BIN_0X80);
        alarmInfo.setVssFault((data[0] & Const.BIN_0X01) == Const.BIN_0X01);
        alarmInfo.setOilFault((data[0] & Const.BIN_0X02) == Const.BIN_0X02);
        alarmInfo.setStolenVehicle((data[0] & Const.BIN_0X04) == Const.BIN_0X04);
        alarmInfo.setIllegalIgnition((data[0] & Const.BIN_0X08) == Const.BIN_0X08);
        alarmInfo.setIllegalDisplacement((data[0] & Const.BIN_0X10) == Const.BIN_0X10);
        alarmInfo.setCollisionWarn((data[0] & Const.BIN_0X20) == Const.BIN_0X20);
        alarmInfo.setRollOverWarn((data[0] & Const.BIN_0X40) == Const.BIN_0X40);
        alarmInfo.setIllegalOpeningTheDoor((data[0] & Const.BIN_0X80) == Const.BIN_0X80);
        return alarmInfo;
    }
}

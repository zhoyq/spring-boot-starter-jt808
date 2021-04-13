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
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/18
 */
@Slf4j
@Getter
@Setter
public class TerminalProperty {

    /**
     * 是否适用于客车车辆
     */
    private boolean supportBus;

    /**
     * 是否适用于危险品车辆
     */
    private boolean supportDangerVehicle;

    /**
     * 是否适用于普通货运车辆
     */
    private boolean supportFreightVehicle;

    /**
     * 是否适用于出租车辆
     */
    private boolean supportTaxi;

    /**
     * 是否支持硬盘录像
     */
    private boolean supportRecording;

    /**
     * 是否分体机 还是 一体机
     */
    private boolean supportExtension;

    /**
     * 终端制造商ID 16进制字符串
     */
    private String manufacturer;

    /**
     * 终端型号 16进制字符串
     */
    private String terminalModel;

    /**
     * 终端ID 按照GB18030字符编码 解析字符串
     */
    private String terminalId;

    /**
     * 终端SIM卡 ICCID
     */
    private String iccid;

    /**
     * 终端硬件版本号
     */
    private String terminalHardVersion;

    /**
     * 终端固件版本号
     */
    private String terminalSoftVersion;

    /**
     * 是否支持GPS定位
     */
    private boolean supportGps;
    /**
     * 是否支持北斗定位
     */
    private boolean supportBeidou;
    /**
     * 是否支持GLONASS定位
     */
    private boolean supportGlonass;
    /**
     * 是否支持Galileo定位
     */
    private boolean supportGalileo;

    /**
     * 是否支持GPRS通信
     */
    private boolean supportGprs;
    /**
     * 是否支持CDMA通信
     */
    private boolean supportCdma;
    /**
     * 是否支持TD-SCDMA通信
     */
    private boolean supportTdscdma;
    /**
     * 是否支持WCDMA通信
     */
    private boolean supportWcdma;
    /**
     * 是否支持CDMA2000通信
     */
    private boolean supportCdma2000;
    /**
     * 是否支持TD-LTE通信
     */
    private boolean supportTdlte;
    /**
     * 是否支持其他通信方式
     */
    private boolean supportOther;

    public static TerminalProperty fromBytes(byte[] data) {
        TerminalProperty terminalProperty = new TerminalProperty();
        // 终端类型
        byte[] terminalType = ByteArrHelper.subByte(data, 0, 2);
        terminalProperty.setSupportBus((terminalType[1] & Const.BIN_0X01) == Const.BIN_0X01);
        terminalProperty.setSupportDangerVehicle((terminalType[1] & Const.BIN_0X02) == Const.BIN_0X02);
        terminalProperty.setSupportFreightVehicle((terminalType[1] & Const.BIN_0X04) == Const.BIN_0X04);
        terminalProperty.setSupportTaxi((terminalType[1] & Const.BIN_0X08) == Const.BIN_0X08);
        terminalProperty.setSupportRecording((terminalType[1] & Const.BIN_0X40) == Const.BIN_0X40);
        terminalProperty.setSupportExtension((terminalType[1] & Const.BIN_0X80) == Const.BIN_0X80);
        // 制造商ID
        byte[] manufacturer = ByteArrHelper.subByte(data, 2, 7);
        terminalProperty.setManufacturer(ByteArrHelper.toHexString(manufacturer));
        // 终端型号
        byte[] terminalModel = ByteArrHelper.subByte(data, 7, 27);
        terminalProperty.setTerminalModel(ByteArrHelper.toHexString(terminalModel));
        // 终端ID
        byte[] terminalId = ByteArrHelper.subByte(data, 27, 34);
        try {
            terminalProperty.setTerminalId(Jt808Helper.toGBKString(terminalId));
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
        }
        // 终端SIM卡ICCID
        byte[] terminalIccid = ByteArrHelper.subByte(data, 34, 44);
        terminalProperty.setIccid(ByteArrHelper.getBCDStrByArr(terminalIccid));
        // 终端硬件版本号长度
        int terminalHardwareVersionLength = data[44];
        // 终端已经按版本号
        byte[] terminalHardwareVersion = ByteArrHelper.subByte(data, 45, 45 + terminalHardwareVersionLength);
        try {
            terminalProperty.setTerminalHardVersion(Jt808Helper.toGBKString(terminalHardwareVersion));
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
        }
        // 终端软件版本号长度
        int terminalSoftwareVersionLength = data[45 + terminalHardwareVersionLength];
        // 终端固件版本号
        byte[] terminalSoftwareVersion = ByteArrHelper.subByte(data, 46 + terminalHardwareVersionLength,
                46 + terminalHardwareVersionLength + terminalSoftwareVersionLength);
        try {
            terminalProperty.setTerminalSoftVersion(Jt808Helper.toGBKString(terminalSoftwareVersion));
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
        }
        // GNSS模块属性
        byte gnssModuleProp = data[46 + terminalHardwareVersionLength + terminalSoftwareVersionLength];
        terminalProperty.setSupportGps((gnssModuleProp & Const.BIN_0X01) == Const.BIN_0X01);
        terminalProperty.setSupportBeidou((gnssModuleProp & Const.BIN_0X02) == Const.BIN_0X02);
        terminalProperty.setSupportGlonass((gnssModuleProp & Const.BIN_0X04) == Const.BIN_0X04);
        terminalProperty.setSupportGalileo((gnssModuleProp & Const.BIN_0X08) == Const.BIN_0X08);
        // 通信模块属性
        byte connectModuleProp = data[47 + terminalHardwareVersionLength + terminalSoftwareVersionLength];
        terminalProperty.setSupportGprs((connectModuleProp & Const.BIN_0X01) == Const.BIN_0X01);
        terminalProperty.setSupportCdma((connectModuleProp & Const.BIN_0X02) == Const.BIN_0X02);
        terminalProperty.setSupportTdscdma((connectModuleProp & Const.BIN_0X04) == Const.BIN_0X04);
        terminalProperty.setSupportWcdma((connectModuleProp & Const.BIN_0X08) == Const.BIN_0X08);
        terminalProperty.setSupportCdma2000((connectModuleProp & Const.BIN_0X10) == Const.BIN_0X10);
        terminalProperty.setSupportTdlte((connectModuleProp & Const.BIN_0X20) == Const.BIN_0X20);
        terminalProperty.setSupportOther((connectModuleProp & Const.BIN_0X80) == Const.BIN_0X80);
        return terminalProperty;
    }
}

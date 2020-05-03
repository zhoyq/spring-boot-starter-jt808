/*
 *  Copyright (c) 2020. 衷于栖 All rights reserved.
 *
 *  版权所有 衷于栖 并保留所有权利 2020。
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  作者：衷于栖（feedback@zhoyq.com）
 *  博客：https://www.zhoyq.com
 *  创建时间：2020
 *
 */

package com.zhoyq.server.jt808.starter.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
}

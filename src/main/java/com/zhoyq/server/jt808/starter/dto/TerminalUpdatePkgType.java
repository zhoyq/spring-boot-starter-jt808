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

package com.zhoyq.server.jt808.starter.dto;

import lombok.Getter;

/**
 * 终端升级包类型
 * @author zhoyq
 * @date 2018-06-27
 */
@Getter
public enum TerminalUpdatePkgType {

    /**
     * 终端
     */
    TERMINAL(0x00),
    /**
     * 道路运输证IC卡读卡器
     */
    IC_READER(0x0C),
    /**
     * 卫星定位模块
     */
    GPS_POSITIONING(0x34),
    /**
     * 苏标：驾驶辅助功能模块
     */
    DRIVE_ASSISTANCE_MODULE(0x64),
    /**
     * 苏标：驾驶员行为检测模块
     */
    DRIVER_MOTION_MONITOR(0x65),
    /**
     * 苏标：轮胎状态检测模块
     */
    TIRE_STATUS_MONITOR(0x66),
    /**
     * 苏标：变道决策辅助模块
     */
    CHANGE_ROAD_ASSISTANCE(0x67),
    ;


    private byte value;
    TerminalUpdatePkgType(int value) {
        this.value = (byte)value;
    }

    public static TerminalUpdatePkgType VALUE_OF(byte b) {
        for (TerminalUpdatePkgType value : TerminalUpdatePkgType.values()) {
            if (value.getValue() == b) {
                return value;
            }
        }
        return null;
    }
}

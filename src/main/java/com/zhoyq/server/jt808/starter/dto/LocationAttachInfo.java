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
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 定位附加信息项目
 * TODO 值获取 遍历性 需要提升
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/23
 */
@Setter
@Getter
public class LocationAttachInfo {
    private int id;
    private byte[] data;

    // TODO 苏标：获取驾驶辅助功能报警信息
    // TODO 苏标：获取驾驶员行为检测功能报警信息
    // TODO 苏标：获取轮胎状态监测报警信息
    // TODO 苏标：获取变道决策辅助报警信息
    // TODO 苏标：获取激烈驾驶报警信息

    public static LocationAttachInfo fromBytes(byte[] data) {
        LocationAttachInfo locationAttachInfo = new LocationAttachInfo();
        locationAttachInfo.setId(data[0]);
        locationAttachInfo.setData(ByteArrHelper.subByte(data, 2));
        return locationAttachInfo;
    }
}

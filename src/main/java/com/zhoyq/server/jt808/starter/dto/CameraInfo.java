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


import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO: 便利性更新
 * @author zhoyq
 * @date 2018-06-27
 */
@Setter
@Getter
public class CameraInfo {
    /**
     * 通道ID
     * 值大于零
     * 苏标：0x00 - 0x25
     * 苏标：0x64 控制驾驶辅助模块摄像头拍照
     * 苏标：0x65 控制驾驶员行为检测模块拍照
     */
    private byte id;

    /**
     * 拍摄命令
     * 0：停止拍摄
     * 0xffff：录像
     * 其他：拍照数量
     * 苏标：仅主机拍照有效
     */
    private byte[] comm;

    /**
     * 拍照间隔（录像时间）单位秒
     * 0：最小间隔拍照或一直录像
     * 苏标：仅主机拍照有效
     */
    private byte[] spaceTime;

    /**
     * 保存标志
     * 0：实时上传
     * 1：保存
     * 苏标：仅主机拍照有效
     */
    private byte saveSign;

    /**
     * 分辨率
     * 0x00：最低分辨率
     * 0x01：320 x 240
     * 0x02：640 x 480
     * 0x03：800 x 600
     * 0x04：1024 x 768
     * 0x05：176 x 144 [Qcif]
     * 0x06：352 x 288 [Cif]
     * 0x07：704 x 288 [HALF D1]
     * 0x08：704 x 576 [D1]
     * 0xff：最高分辨率
     * 苏标：仅主机拍照有效
     */
    private byte resolution;

    /**
     * 图像（录像）质量
     * 1 - 10
     * 1 代表质量损失最小
     * 10 代表压缩比最大
     * 苏标：仅主机拍照有效
     */
    private byte quality;

    /**
     * 亮度
     * 0 - 255
     * 苏标：仅主机拍照有效
     */
    private byte luminance;

    /**
     * 对比度
     * 0 - 127
     * 苏标：仅主机拍照有效
     */
    private byte contrast;

    /**
     * 饱和度
     * 0 - 127
     * 苏标：仅主机拍照有效
     */
    private byte saturation;

    /**
     * 色度
     * 0 - 255
     * 苏标：仅主机拍照有效
     */
    private byte tone;

    public byte[] toBytes(){
        return ByteArrHelper.union(
                new byte[]{this.getId()},
                this.getComm(),
                this.getSpaceTime(),
                new byte[]{this.getSaveSign(),this.getResolution(),
                        this.getQuality(),this.getLuminance(),
                        this.getContrast(),this.getSaturation(),this.getTone()});
    }
}

/*
 *  Copyright (c) 2020. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2020.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2020. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.dto;


import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;

/**
 * @author 刘路
 * @date 2018-06-27
 */
public class CameraInfo {
    private byte id;
    private byte[] comm;
    private byte[] spaceTime;
    private byte saveSign;
    private byte resolution;
    private byte quality;
    private byte luminance;
    private byte contrast;
    private byte saturation;
    private byte tone;

    public CameraInfo(byte[] b) {
        this.id = b[0];
        this.comm = ByteArrHelper.subByte(b, 1, 3);
        this.spaceTime = ByteArrHelper.subByte(b, 3, 5);
        this.saveSign = b[5];
        this.resolution = b[6];
        this.quality = b[7];
        this.luminance = b[8];
        this.contrast = b[9];
        this.saturation = b[10];
        this.tone = b[11];
    }

    public byte getId() {
        return id;
    }
    public void setId(byte id) {
        this.id = id;
    }
    public byte[] getComm() {
        return comm;
    }
    public void setComm(byte[] comm) {
        this.comm = comm;
    }
    public byte[] getSpaceTime() {
        return spaceTime;
    }
    public void setSpaceTime(byte[] spaceTime) {
        this.spaceTime = spaceTime;
    }
    public byte getSaveSign() {
        return saveSign;
    }
    public void setSaveSign(byte saveSign) {
        this.saveSign = saveSign;
    }
    public byte getResolution() {
        return resolution;
    }
    public void setResolution(byte resolution) {
        this.resolution = resolution;
    }
    public byte getQuality() {
        return quality;
    }
    public void setQuality(byte quality) {
        this.quality = quality;
    }
    public byte getLuminance() {
        return luminance;
    }
    public void setLuminance(byte luminance) {
        this.luminance = luminance;
    }
    public byte getContrast() {
        return contrast;
    }
    public void setContrast(byte contrast) {
        this.contrast = contrast;
    }
    public byte getSaturation() {
        return saturation;
    }
    public void setSaturation(byte saturation) {
        this.saturation = saturation;
    }
    public byte getTone() {
        return tone;
    }
    public void setTone(byte tone) {
        this.tone = tone;
    }

}

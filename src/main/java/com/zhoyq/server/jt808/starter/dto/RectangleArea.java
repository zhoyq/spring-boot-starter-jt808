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

/**
 * 矩形区域
 * @author zhoyq
 * @date 2018-06-27
 */
public class RectangleArea {
    private byte[] id;
    private byte[] prop;
    private byte[] leftUpLat;
    private byte[] leftUpLon;
    private byte[] rightDownLat;
    private byte[] rightDownLon;
    private byte[] beginTime;
    private byte[] endTime;
    private byte[] highestSpeed;
    private byte overSpeedTime;
    public byte[] getId() {
        return id;
    }
    public void setId(byte[] id) {
        this.id = id;
    }
    public byte[] getProp() {
        return prop;
    }
    public void setProp(byte[] prop) {
        this.prop = prop;
    }
    public byte[] getLeftUpLat() {
        return leftUpLat;
    }
    public void setLeftUpLat(byte[] leftUpLat) {
        this.leftUpLat = leftUpLat;
    }
    public byte[] getLeftUpLon() {
        return leftUpLon;
    }
    public void setLeftUpLon(byte[] leftUpLon) {
        this.leftUpLon = leftUpLon;
    }
    public byte[] getRightDownLat() {
        return rightDownLat;
    }
    public void setRightDownLat(byte[] rightDownLat) {
        this.rightDownLat = rightDownLat;
    }
    public byte[] getRightDownLon() {
        return rightDownLon;
    }
    public void setRightDownLon(byte[] rightDownLon) {
        this.rightDownLon = rightDownLon;
    }
    public byte[] getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(byte[] beginTime) {
        this.beginTime = beginTime;
    }
    public byte[] getEndTime() {
        return endTime;
    }
    public void setEndTime(byte[] endTime) {
        this.endTime = endTime;
    }
    public byte[] getHighestSpeed() {
        return highestSpeed;
    }
    public void setHighestSpeed(byte[] highestSpeed) {
        this.highestSpeed = highestSpeed;
    }
    public byte getOverSpeedTime() {
        return overSpeedTime;
    }
    public void setOverSpeedTime(byte overSpeedTime) {
        this.overSpeedTime = overSpeedTime;
    }

}

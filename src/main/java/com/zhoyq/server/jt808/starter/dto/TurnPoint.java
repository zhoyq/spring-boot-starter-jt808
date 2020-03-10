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

/**
 * 拐点
 * @author 刘路
 * @date 2018-06-27
 */
public class TurnPoint {
    private byte[] id;
    private byte[] routeId;
    private byte[] lat;
    private byte[] lon;
    private byte width;
    private byte prop;
    private byte[] driveOverValue;
    private byte[] driveLowerValue;
    private byte[] highestSpeed;
    private byte overSpeedTime;
    public byte[] getId() {
        return id;
    }
    public void setId(byte[] id) {
        this.id = id;
    }
    public byte[] getRouteId() {
        return routeId;
    }
    public void setRouteId(byte[] routeId) {
        this.routeId = routeId;
    }
    public byte[] getLat() {
        return lat;
    }
    public void setLat(byte[] lat) {
        this.lat = lat;
    }
    public byte[] getLon() {
        return lon;
    }
    public void setLon(byte[] lon) {
        this.lon = lon;
    }
    public byte getWidth() {
        return width;
    }
    public void setWidth(byte width) {
        this.width = width;
    }
    public byte getProp() {
        return prop;
    }
    public void setProp(byte prop) {
        this.prop = prop;
    }
    public byte[] getDriveOverValue() {
        return driveOverValue;
    }
    public void setDriveOverValue(byte[] driveOverValue) {
        this.driveOverValue = driveOverValue;
    }
    public byte[] getDriveLowerValue() {
        return driveLowerValue;
    }
    public void setDriveLowerValue(byte[] driveLowerValue) {
        this.driveLowerValue = driveLowerValue;
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

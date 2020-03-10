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
 * 终端升级包
 * @author 刘路
 * @date 2018-06-27
 */
public class TerminalUpdatePkg {
    private byte updateType;
    private byte[] producerId;
    private byte versionLength;
    private byte[] version;
    private byte[] dataLength;
    private byte[] data;
    public byte getUpdateType() {
        return updateType;
    }
    public void setUpdateType(byte updateType) {
        this.updateType = updateType;
    }
    public byte[] getProducerId() {
        return producerId;
    }
    public void setProducerId(byte[] producerId) {
        this.producerId = producerId;
    }
    public byte getVersionLength() {
        return versionLength;
    }
    public void setVersionLength(byte versionLength) {
        this.versionLength = versionLength;
    }
    public byte[] getVersion() {
        return version;
    }
    public void setVersion(byte[] version) {
        this.version = version;
    }
    public byte[] getDataLength() {
        return dataLength;
    }
    public void setDataLength(byte[] dataLength) {
        this.dataLength = dataLength;
    }
    public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }

}

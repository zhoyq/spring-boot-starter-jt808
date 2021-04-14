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
 * @date 2020/2/19
 */
@Slf4j
@Setter
@Getter
public class DriverInfo {
    /**
     * 司机名称
     */
    private String driverName;
    /**
     * 司机身份证号 仅2011 2019
     */
    private String idCardNumber;
    /**
     * 司机从业资格证号码
     */
    private String certificateNumber;
    /**
     * 发证机构名称
     */
    private String certificatePublishAgentName;
    /**
     * 证件有效期 仅2013
     */
    private String certificateLimitDate;

    /**
     * 驾驶员身份采集问题
     */
    private DriverAlarmInfo driverAlarmInfo;

    /**
     * 插拔卡时间 YY-MM-DD-hh-mm-ss
     */
    private String datetime;

    /**
     * 是否采集成功 因为需要采集报警所以不能只传回空值
     */
    private boolean success = false;

    public static DriverInfo fromBytes2011(byte[] data) {
        // 姓名长度
        int nameLength = data[0];
        // 驾驶员姓名
        byte[] name = ByteArrHelper.subByte(data,1,nameLength + 1);
        // 身份证编码
        byte[] idCard = ByteArrHelper.subByte(data,nameLength + 1,nameLength + 21);
        // 从业资格证编码
        byte[] certificate = ByteArrHelper.subByte(data,nameLength + 21,nameLength + 61);
        // 从业资格证发证机构名称长度 最后全是 所以不需要
//        int certificatePublishAgentNameLength = data[nameLength + 61];
        // 从业资格证发证机构名称
        byte[] certificatePublishAgentName = ByteArrHelper.subByte(data,nameLength + 62);

        DriverInfo driverInfo = new DriverInfo();

        try {
            driverInfo.setDriverName(Jt808Helper.toGBKString(name));
            driverInfo.setIdCardNumber(Jt808Helper.toGBKString(idCard));
            driverInfo.setCertificateNumber(Jt808Helper.toGBKString(certificate));
            driverInfo.setCertificatePublishAgentName(Jt808Helper.toGBKString(certificatePublishAgentName));
        } catch (UnsupportedEncodingException e) {
            log.warn(e.getMessage());
        }

        driverInfo.setSuccess(true);

        return driverInfo;
    }
    public static DriverInfo fromBytes2013(byte[] data) {
        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setDriverAlarmInfo(new DriverAlarmInfo());

        if(data[0] == Const.NUMBER_1){
            // 驾驶员上班 卡插入
            driverInfo.getDriverAlarmInfo().setPullOutCard(false);
        }else if(data[0] == Const.NUMBER_2){
            // 驾驶员下班 卡拔出
            driverInfo.getDriverAlarmInfo().setPullOutCard(true);
        }else{
            // 2013 第一个字节 不支持其他形式
            return null;
        }
        String gTime = Jt808Helper.getDataTime(ByteArrHelper.subByte(data,1,7));

        driverInfo.setDatetime(gTime);

        // 拔卡则不再继续解析
        if(!driverInfo.getDriverAlarmInfo().isPullOutCard()){
            switch (data[7]){
                case 0x00:
                    // IC 卡读卡成功 读取驾驶员信息
                    try {
                        int nameLength = data[8];
                        String driverName = Jt808Helper.toGBKString(ByteArrHelper.subByte(data,9,
                                nameLength + 9));
                        String certificate = Jt808Helper.toGBKString(ByteArrHelper.subByte(data,nameLength + 9,
                                nameLength + 29));
                        int certificatePublishAgentNameLength = data[nameLength + 29];
                        String certificatePublishAgentName =
                                Jt808Helper.toGBKString(ByteArrHelper.subByte(data,nameLength + 30,
                                        nameLength + 30 + certificatePublishAgentNameLength));
                        String expiryTime = ByteArrHelper.getBCDStrByArr(ByteArrHelper.subByte(data,
                                nameLength + 30 + certificatePublishAgentNameLength));

                        driverInfo.setDriverName(driverName);
                        driverInfo.setCertificateNumber(certificate);
                        driverInfo.setCertificatePublishAgentName(certificatePublishAgentName);
                        driverInfo.setCertificateLimitDate(expiryTime);

                    } catch (UnsupportedEncodingException e) {
                        log.warn(e.getMessage());
                        return null;
                    }
                    break;
                case 0x01:
                    // 读卡失败，原因为卡片密钥认证未通过
                    driverInfo.getDriverAlarmInfo().setUnAuthentication(true);
                    break;
                case 0x02:
                    // 读卡失败，原因为卡片已被锁定
                    driverInfo.getDriverAlarmInfo().setLocked(true);
                    break;
                case 0x03:
                    // 读卡失败，原因为卡片被拔出
                    driverInfo.getDriverAlarmInfo().setPullOut(true);
                    break;
                case 0x04:
                    // 读卡失败，原因为数据校验错误
                    driverInfo.getDriverAlarmInfo().setCheckFailed(true);
                    break;
                default:
                    return null;
            }
        }

        driverInfo.setSuccess(true);

        return driverInfo;
    }
    public static DriverInfo fromBytes2019(byte[] data) {
        DriverInfo driverInfo = new DriverInfo();
        driverInfo.setDriverAlarmInfo(new DriverAlarmInfo());

        if(data[0] == Const.NUMBER_1){
            // 驾驶员上班 卡插入
            driverInfo.getDriverAlarmInfo().setPullOutCard(false);
        }else if(data[0] == Const.NUMBER_2){
            // 驾驶员下班 卡拔出 没有其他信息
            driverInfo.getDriverAlarmInfo().setPullOutCard(true);
        }else{
            // 2019 第一个字节 不支持其他形式
            return null;
        }
        String gTime = Jt808Helper.getDataTime(ByteArrHelper.subByte(data,1,7));

        driverInfo.setDatetime(gTime);

        // 拔卡则不再继续解析
        if(!driverInfo.getDriverAlarmInfo().isPullOutCard()){
            switch (data[7]){
                case 0x00:
                    // IC 卡读卡成功 读取驾驶员信息
                    try {
                        int nameLength = data[8];
                        String driverName = Jt808Helper.toGBKString(ByteArrHelper.subByte(data,9,
                                nameLength + 9));
                        String certificate = Jt808Helper.toGBKString(ByteArrHelper.subByte(data,nameLength + 9,
                                nameLength + 29));
                        int certificatePublishAgentNameLength = data[nameLength + 29];
                        String certificatePublishAgentName =
                                Jt808Helper.toGBKString(ByteArrHelper.subByte(data,nameLength + 30,
                                        nameLength + 30 + certificatePublishAgentNameLength));
                        String expiryTime = ByteArrHelper.getBCDStrByArr(ByteArrHelper.subByte(data,
                                nameLength + certificatePublishAgentNameLength + 30,
                                nameLength + certificatePublishAgentNameLength + 34));
                        String idCardNum = Jt808Helper.toGBKString(ByteArrHelper.subByte(
                                data,
                                nameLength + certificatePublishAgentNameLength + 34,
                                nameLength + certificatePublishAgentNameLength + 54
                        ));

                        driverInfo.setDriverName(driverName);
                        driverInfo.setCertificateNumber(certificate);
                        driverInfo.setCertificatePublishAgentName(certificatePublishAgentName);
                        driverInfo.setCertificateLimitDate(expiryTime);
                        driverInfo.setIdCardNumber(idCardNum);

                    } catch (UnsupportedEncodingException e) {
                        log.warn(e.getMessage());
                        return null;
                    }
                    break;
                case 0x01:
                    // 读卡失败，原因为卡片密钥认证未通过
                    driverInfo.getDriverAlarmInfo().setUnAuthentication(true);
                    break;
                case 0x02:
                    // 读卡失败，原因为卡片已被锁定
                    driverInfo.getDriverAlarmInfo().setLocked(true);
                    break;
                case 0x03:
                    // 读卡失败，原因为卡片被拔出
                    driverInfo.getDriverAlarmInfo().setPullOut(true);
                    break;
                case 0x04:
                    // 读卡失败，原因为数据校验错误
                    driverInfo.getDriverAlarmInfo().setCheckFailed(true);
                    break;
                default:
                    return null;
            }
        }

        driverInfo.setSuccess(true);

        return driverInfo;
    }
}

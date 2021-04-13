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

package com.zhoyq.server.jt808.starter.service.impl;

import com.zhoyq.server.jt808.starter.dto.*;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.service.DataService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 仅进行log 使用 0000000 作为鉴权码访问鉴权即可 但是因为 缓存未持久化 运行之后 需要重新注册鉴权
 * 车辆和设备仍需要用户添加 才能使用
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/4
 */
@Slf4j
public class SimpleDataServiceAdapter implements DataService {

    /**
     *  设备必须存在 才能存储完成
     */
    @Override
    public void terminalRsa(String phone, byte[] e, byte[] n) {
        log.info("{}, rsa {} {}", phone, ByteArrHelper.toHexString(e), ByteArrHelper.toHexString(n));
    }

    @Override
    public byte[] terminalRsa(String sim) {
        return null;
    }

    /**
     * 有下发指令 才有应答 否则剔除
     */
    @Override
    public void terminalAnswer(String phone, int platformStreamNumber, String platformCommandId,
                               String msgId, byte[] msgBody) {
        log.info("{}, answer {}", phone, msgId);
    }

    @Override
    public void terminalParameters(String phone, TerminalParameters parameters) {
        log.info("{}, terminalParameters", phone);
    }

    @Override
    public void terminalProperty(String phone, TerminalProperty property) {
        log.info("{}, terminalProperty", phone);
    }

    /**
     * 心跳不存储
     */
    @Override
    public void terminalHeartbeat(String phone) {
        log.info("{}, heartbeat", phone);
    }

    /**
     * 终端注销 需要通过手机号 取消 车辆和设备之间的联系
     * 取消终端已经鉴权的缓存
     */
    @Override
    public void terminalCancel(String phone) {
        log.info("{}, cancel", phone);
    }

    /**
     * 车辆信息 和 设备信息 是注册信息之前注册到平台的
     * 注册需要查找到 平台的车辆和设备并通过手机号来连接
     *
     * 车 设备 和 卡号 是三个不同的对象
     * 车辆 Vechile （province city licenseColor license）
     * 设备 Device （manufacturer deviceType deviceId）
     * 卡号 Sim (phone)
     */
    @Override
    public String terminalRegister(String phone, int province, int city, String manufacturer, String deviceType, String deviceId, byte licenseColor, String registerLicense) {
        log.info("{}, register", phone);
        return null;
    }

    @Override
    public void terminalLocation(String phone, LocationInfo locationInfo, Integer mediaId) {
        log.info("{}, location", phone);
    }

    @Override
    public void eventReport(String phone, byte eventReportAnswerId) {
        log.info("{}, report", phone);
    }

    @Override
    public void orderInfo(String phone, byte type) {
        log.info("{}, order info", phone);
    }

    @Override
    public void cancelOrderInfo(String phone, byte type) {
        log.info("{}, cancel order info", phone);
    }

    @Override
    public void eBill(String phone, byte[] data) {
        log.info("{}, bill", phone);
    }

    @Override
    public void driverInfo(String phone, DriverInfo driverInfo) {
        log.info("{}, driver info", phone);
    }

    @Override
    public void canData(String phone, CanDataInfo canDataInfo) {
        log.info("{}, can", phone);
    }

    @Override
    public void mediaInfo(String phone, MediaInfo mediaInfo) {
        log.info("{}, media info", phone);
    }

    @Override
    public void mediaPackage(String phone, byte[] mediaData, Integer mediaId) {
        log.info("{}, media package", phone);
    }

    @Override
    public void dataTransport(String phone, DataTransportInfo dataTransportInfo) {
        log.info("{}, data transport", phone);
    }

    @Override
    public void compressData(String phone, byte[] data) {
        log.info("{}, compress data", phone);
    }

    @Override
    public void terminalAuth(String phone, String authId, String imei, String softVersion) {
        log.info("{}, terminal auth", phone);
    }

    @Override
    public List<SimAuthDto> simAuth() {
        return null;
    }
}

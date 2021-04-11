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

package com.zhoyq.server.jt808.starter.pack;

import com.zhoyq.server.jt808.starter.config.Const;
import com.zhoyq.server.jt808.starter.core.Jt808Pack;
import com.zhoyq.server.jt808.starter.core.PackHandler;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.CacheService;
import com.zhoyq.server.jt808.starter.service.DataService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 终端注册
 * 终端注册是为了建立终端和车辆的关系 注册获取鉴权码鉴权完成后才能传输
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2018/7/31
 */
@Slf4j
@Jt808Pack(msgId = 0x0100)
@AllArgsConstructor
public class Handler0x0100 implements PackHandler {
    private DataService dataService;
    private CacheService cacheService;
    private ThreadPoolExecutor tpe;

    @Override
    public byte[] handle( byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody) {
        log.info("0100 终端注册 TerminalRegister");
        // 判断版本
        int version;
        if (phoneNum.length == 10) {
            version = 2019;
        } else {
            if(msgBody.length > 37) {
                version = 2013;
            } else {
                version = 2011;
            }
        }

        String phone = ByteArrHelper.toHexString(phoneNum);
        // 省域ID, 市县域ID, 制造商ID, 终端型号, 终端ID, 车牌颜色 为0时 表示没有车牌, 车牌号码[车牌颜色为0 时 表示VIN-车辆大架号]
        byte[] provinceId, cityId, producerId, terminalType, terminalId, licenseColor, license;
        int offset = 0;
        provinceId = ByteArrHelper.subByte(msgBody, offset, offset += 2);
        cityId = ByteArrHelper.subByte(msgBody, offset, offset += 2);
        if (version == 2019) {
            producerId = ByteArrHelper.subByte(msgBody, offset, offset += 11);
        } else {
            producerId = ByteArrHelper.subByte(msgBody, offset, offset += 5);
        }
        if (version == 2019) {
            terminalType = ByteArrHelper.subByte(msgBody, offset, offset += 30);
        } else if(version == 2013){
            terminalType = ByteArrHelper.subByte(msgBody, offset, offset += 20);
        } else {
            terminalType = ByteArrHelper.subByte(msgBody, offset, offset += 8);
        }
        if(version == 2019){
            terminalId = ByteArrHelper.subByte(msgBody, offset, offset += 30);
        } else {
            terminalId = ByteArrHelper.subByte(msgBody, offset, offset += 7);
        }
        licenseColor = ByteArrHelper.subByte(msgBody, offset, offset += 1);
        license = ByteArrHelper.subByte(msgBody, offset);

        // 鉴权码需要在调用终端注册的时候自动生成 7 位数
        Future<String> authFuture = tpe.submit(()->{
            // 省域ID
            int province = ByteArrHelper.twobyte2int(provinceId);
            // 市县域ID
            int city = ByteArrHelper.twobyte2int(cityId);
            // 制造商ID
            String manufacturer = null;
            // 终端型号
            String deviceType = null;
            // 终端ID
            String deviceId = null;
            // 车牌颜色 直接用 byte 即可
            // 车牌号码[车牌颜色为0 时 表示VIN-车辆大架号]
            String registerLicense = null;

            try {
                manufacturer = Jt808Helper.toAsciiString(producerId);
                deviceType = Jt808Helper.toAsciiString(terminalType);
                deviceId = Jt808Helper.toAsciiString(terminalId);
                registerLicense = Jt808Helper.toGBKString(license);
            } catch (UnsupportedEncodingException e) {
                log.warn(e.getMessage());
            }

            // 需要现在平台添加终端以及车辆才能注册成功 屏蔽无关车辆注册登入
            // 需要在平台验证 终端ID 和 车辆标识 存在注册成功 返回鉴权码 否则注册失败

            // 建立终端与车辆之间的关系
            // 通过省ID、市ID、车牌颜色和车牌号唯一查询出车辆信息
            // 通过设备ID、制造商ID、以及设备类型唯一查询出设备信息
            // 然后通过两者信息链接终端和车辆信息
            return dataService.terminalRegister(phone, province, city, manufacturer,
                    deviceType, deviceId, licenseColor[0], registerLicense);
        });

        // 鉴权码
        String str;
        try {
            // 返回值有 0000001 车辆被注册 0000002 无车辆 0000003 终端被注册 0000004 或无终端 者 真正的鉴权码
            str = authFuture.get();
        } catch (Exception e) {
            log.warn(e.getMessage());
            log.warn("鉴权码获取失败！");
            str = null;
        }
        // 应答
        if(str == null){
            // 数据库查询或者之类的出现了异常 直接回复平台失败应答
            return ResHelper.getPlatAnswer(phoneNum, streamNum,msgId,(byte)1);
        }else if(Const.TERMINAL_REG_HAS_VEHICLE.equals(str)){
            // 车辆已经注册
            return ResHelper.getTerminalRegisterAnswer(phoneNum, streamNum, (byte)1, str);
        }else if(Const.TERMINAL_REG_NO_VEHICLE.equals(str)){
            // 不存在的车辆
            return ResHelper.getTerminalRegisterAnswer(phoneNum, streamNum, (byte)2, str);
        }else if(Const.TERMINAL_REG_HAS_TERMINAL.equals(str)){
            // 终端已经注册
            return ResHelper.getTerminalRegisterAnswer(phoneNum, streamNum, (byte)3, str);
        }else if(Const.TERMINAL_REG_NO_TERMINAL.equals(str)){
            // 不存在的终端
            return ResHelper.getTerminalRegisterAnswer(phoneNum, streamNum, (byte)4, str);
        }else{
            // 设置鉴权码
            cacheService.setAuth(phone, str);
            // 正常
            return ResHelper.getTerminalRegisterAnswer(phoneNum, streamNum, (byte)0, str);
        }
    }
}

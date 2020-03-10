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

package com.zhoyq.server.jt808.starter.pack;

import com.zhoyq.server.jt808.starter.config.Const;
import com.zhoyq.server.jt808.starter.core.Jt808Pack;
import com.zhoyq.server.jt808.starter.core.PackHandler;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.DataService;
import com.zhoyq.server.jt808.starter.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 终端注册
 * 终端注册是为了建立终端和车辆的关系 注册获取鉴权码鉴权完成后才能传输
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2018/7/31
 */
@Slf4j
@Jt808Pack(msgId = 0x0100)
public class Handler0x0100 implements PackHandler {

    @Autowired
    private DataService dataService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private ThreadPoolExecutor tpe;

    @Override
    public byte[] handle( byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody) {
        log.info("0100 终端注册 TerminalRegister");

        String phone = ByteArrHelper.toHexString(phoneNum);
        // 省域ID
        byte[] provinceId = ByteArrHelper.subByte(msgBody, 0, 2);
        // 市县域ID
        byte[] cityId = ByteArrHelper.subByte(msgBody, 2, 4);
        // 制造商ID
        byte[] producerId = ByteArrHelper.subByte(msgBody, 4, 9);
        // 终端型号
        byte[] terminalType = ByteArrHelper.subByte(msgBody, 9, 29);
        // 终端ID
        byte[] terminalId = ByteArrHelper.subByte(msgBody, 29, 36);
        // 车牌颜色 为0时 表示没有车牌
        byte licenseColor = ByteArrHelper.subByte(msgBody, 36, 37)[0];
        // 车牌号码[车牌颜色为0 时 表示VIN-车辆大架号]
        byte[] license = ByteArrHelper.subByte(msgBody, 37);

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
                manufacturer = Jt808Helper.toGB18030String(producerId);
                deviceType = Jt808Helper.toGB18030String(terminalType);
                deviceId = Jt808Helper.toGB18030String(terminalId);
                registerLicense = Jt808Helper.toGB18030String(license);
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
                    deviceType, deviceId, licenseColor, registerLicense);
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
            sessionService.setAuth(phone, str);
            try{
                sessionService.setDevice(phone, new String(terminalId,"GB18030"));
            }catch(UnsupportedEncodingException e){
                log.warn(e.getMessage());
            }
            // 正常
            return ResHelper.getTerminalRegisterAnswer(phoneNum, streamNum, (byte)0, str);
        }
    }
}

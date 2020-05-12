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

import com.zhoyq.server.jt808.starter.core.Jt808Pack;
import com.zhoyq.server.jt808.starter.core.PackHandler;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.CacheService;
import com.zhoyq.server.jt808.starter.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 终端鉴权 使用注册的鉴权码进行鉴权
 * 鉴权之前终端不能发送数据
 * 鉴权码最好是随机的 要求终端每次链接请求一次
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2018/7/31
 */
@Slf4j
@Jt808Pack(msgId = 0x0102)
public class Handler0x0102 implements PackHandler {

    @Autowired
    private CacheService cacheService;
    @Autowired
    private DataService dataService;
    @Autowired
    private ThreadPoolExecutor tpe;

    @Autowired
    private ByteArrHelper byteArrHelper;
    @Autowired
    private Jt808Helper jt808Helper;
    @Autowired
    private ResHelper resHelper;

    @Override
    public byte[] handle( byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody) {
        log.info("0102 终端鉴权  TerminalAuthentication");
        int version;
        if (phoneNum.length == 10) {
            version = 2019;
        } else {
            version = 20132011;
        }

        String phone = byteArrHelper.toHexString(phoneNum);

        // 获取鉴权码
        String authId;

        if (version == 2019) {
            int len = msgBody[0];
            try {
                authId = jt808Helper.toGBKString(byteArrHelper.subByte(msgBody, 1, 1 + len));
            } catch (UnsupportedEncodingException e) {
                log.warn(e.getMessage());
                authId = null;
            }
        } else {
            try {
                authId = jt808Helper.toGBKString(msgBody);
            } catch (UnsupportedEncodingException e) {
                log.warn(e.getMessage());
                authId = null;
            }
        }

        String oriAuthId = cacheService.getAuth(phone);

        byte result;
        if(oriAuthId != null && oriAuthId.equals(authId)){
            // 鉴权成功
            result = 0;
            // 成功后保存鉴权信息
            if(version == 2019){
                tpe.execute(() -> {
                    byte[] imei = byteArrHelper.subByte(msgBody, msgBody[0] + 1, msgBody[0] + 16);
                    byte[] softVersion = byteArrHelper.subByte(msgBody, msgBody[0] + 16, msgBody[0] + 36);
                    dataService.terminalAuth(
                            phone,
                            oriAuthId,
                            jt808Helper.toAsciiString(imei),
                            jt808Helper.toAsciiString(softVersion)
                    );
                });
            } else {
                tpe.execute(() -> {
                    dataService.terminalAuth(
                            phone,
                            oriAuthId,
                            null,
                            null
                    );
                });
            }
        }else{
            // 鉴权失败
            result = 1;
        }
        return resHelper.getPlatAnswer(phoneNum,streamNum, msgId, result);
    }
}

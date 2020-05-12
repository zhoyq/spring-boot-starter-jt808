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
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 2019 新增 查询区域或者线路数据应答
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/5
 */
@Slf4j
@Jt808Pack(msgId = 0x0608)
public class Handler0x0608 implements PackHandler {
    @Autowired
    private DataService dataService;
    @Autowired
    private ThreadPoolExecutor tpe;

    @Autowired
    private ByteArrHelper byteArrHelper;
    @Autowired
    private ResHelper resHelper;

    @Override
    public byte[] handle(byte[] phoneNum, byte[] streamNum, byte[] msgId, byte[] msgBody) {
        log.info("0608 查询区域或者线路数据应答 search area or route data answer");
        String phone = byteArrHelper.toHexString(phoneNum);
        // 消息体中没有终端对应平台下发指令的流水号 所以指定流水号为 -1
        int platformStreamNumber = -1;
        // 保存命令到相应的下发指令
        tpe.execute(() -> dataService.terminalAnswer(phone, platformStreamNumber, "8608", "0608", msgBody));
        return resHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x00);
    }
}

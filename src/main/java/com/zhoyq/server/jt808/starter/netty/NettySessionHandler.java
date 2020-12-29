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

package com.zhoyq.server.jt808.starter.netty;

import com.zhoyq.server.jt808.starter.config.Jt808Config;
import com.zhoyq.server.jt808.starter.core.PackHandler;
import com.zhoyq.server.jt808.starter.core.PackHandlerManagement;
import com.zhoyq.server.jt808.starter.core.SessionManagement;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.CacheService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/17
 */
@Slf4j
@Component
@AllArgsConstructor
@ChannelHandler.Sharable
public class NettySessionHandler extends ChannelInboundHandlerAdapter {
    private PackHandlerManagement packHandlerManagement;
    private SessionManagement sessionManagement;
    private CacheService cacheService;
    private Jt808Config jt808Config;
    private ByteArrHelper byteArrHelper;
    private ResHelper resHelper;
    private Jt808Helper jt808Helper;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("session exception with {}", ctx.name());
        log.warn(cause.getMessage());
        // 异常时 关闭session 等待重连
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        byte[] originData  = (byte[])msg;
        log.debug("session received msg with id {} and msg {}", ctx.name(), Arrays.toString(originData));

        // 按照协议解析获得的字节数组 pkgNum 从 1 开始
        byte[] msgId, msgBodyProp, protocolVersion, phoneNum, streamNum, pkgCount = null, pkgNum = null, res = null;
        int offset = 0;
        msgId = new byte[]{originData[offset++],originData[offset++]};
        msgBodyProp = new byte[]{originData[offset++],originData[offset++]};
        // 通过消息体属性中的版本标识位 判断是否是 2019版本协议 并增加相关解析
        boolean isVersion2019 =  jt808Helper.isVersion2019(msgBodyProp);
        if (isVersion2019) {
            protocolVersion = new byte[]{originData[offset++]};
            phoneNum = new byte[]{
                    originData[offset++],originData[offset++],originData[offset++],originData[offset++],originData[offset++],
                    originData[offset++],originData[offset++],originData[offset++],originData[offset++],originData[offset++]
            };
        } else {
            phoneNum = new byte[]{
                    originData[offset++],originData[offset++],originData[offset++],
                    originData[offset++],originData[offset++],originData[offset++]
            };
        }
        streamNum = new byte[]{originData[offset++],originData[offset++]};
        boolean hasPackage = jt808Helper.hasPackage(msgBodyProp);
        if( hasPackage ){
            pkgCount = new byte[]{originData[offset++],originData[offset++]};
            pkgNum = new byte[]{originData[offset++],originData[offset]};
        }

        String phone = byteArrHelper.toHexString(phoneNum);

        // 相同身份的终端建立链接 原链接需要断开 也就是加入之前需要判断是否存在终端 存在关闭后在加入
        if(sessionManagement.contains(phone)){
            ChannelHandlerContext preSession = (ChannelHandlerContext)sessionManagement.get(phone);
            if (!preSession.name().equals(ctx.name())) {
                preSession.close();
            }
        }

        // session 加入会话缓存
        sessionManagement.set(phone, ctx);
        if( hasPackage ){
            int totalPkgNum = byteArrHelper.twobyte2int(pkgCount);
            int currentPkgNum = byteArrHelper.twobyte2int(pkgNum);
            // 序号必须小于等于总包数 条件达成之后进行分包处理 否则不处理分包且不处理数据
            if(totalPkgNum >= currentPkgNum){
                if(!cacheService.containsPackages(phone)){
                    ConcurrentHashMap<Integer,byte[]> buf = new ConcurrentHashMap<>(totalPkgNum);
                    cacheService.setPackages(phone, buf);
                }
                Map<Integer,byte[]> pkgBuf = cacheService.getPackages(phone);
                pkgBuf.put(currentPkgNum,originData);
            }
            // 分包结束时需要对分包数据进行解析处理并返回应答 通过总包数和序号对比 判断是不是最后一包
            if( totalPkgNum == currentPkgNum ){
                // 如果是 这个电话的最后一包
                if(jt808Helper.pkgAllReceived(phone, totalPkgNum)){
                    // 合并所有包 并解析
                    res = data(jt808Helper.allPkg(phone,totalPkgNum));
                }else{
                    // 没有全部收到 需要补传 最初一包的流水号
                    byte[] originStreamNum = null;
                    // 补传id列表
                    byte[] idList = new byte[]{};
                    // 补传数量
                    byte num = 0;
                    Map<Integer,byte[]> map = cacheService.getPackages(phone);
                    for(int i = 1;i<=totalPkgNum;i++){
                        if(originStreamNum == null){
                            if (isVersion2019) {
                                originStreamNum = byteArrHelper.subByte(map.get(1), 15, 17);
                            } else {
                                originStreamNum = byteArrHelper.subByte(map.get(1), 10, 12);
                            }
                        }
                        if(!map.containsKey(i)){
                            num++;
                            if (isVersion2019) {
                                idList = byteArrHelper.union(idList, byteArrHelper.subByte(map.get(i), 19, 21));
                            } else {
                                idList = byteArrHelper.union(idList, byteArrHelper.subByte(map.get(i), 14, 16));
                            }
                        }
                    }
                    if(originStreamNum != null) {
                        res = resHelper.getPkgReq(phoneNum, originStreamNum, num, idList);
                    }
                }
            }
        }else{
            res =  data(originData);
        }
        if( res == null ){
            res = resHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x00);
        }
        // 分包消息总长度
        int msgLen = jt808Config.getPackageLength();
        if( res.length > msgLen ){
            // 分包发送
            jt808Helper.sentByPkg(res, ctx);
        }else{
            ctx.writeAndFlush(res);
        }
    }

    /**
     * 数据处理
     * @param originData 原始数据 有分包的情况已经处理完
     * @return 应答数据
     */
    private byte[] data(byte[] originData){

        byte[] msgBody;

        int offset = 0;

        final byte[] msgId = new byte[]{originData[offset++],originData[offset++]};
        final byte[] msgBodyProp = new byte[]{originData[offset++],originData[offset++]};
        // 通过消息体属性中的版本标识位 判断是否是 2019版本协议 并增加相关解析
        byte[] phoneNum;
        if (jt808Helper.isVersion2019(msgBodyProp)) {
            // 忽略 协议版本解析
            offset++;
            phoneNum = new byte[]{
                    originData[offset++],originData[offset++],originData[offset++],originData[offset++],originData[offset++],
                    originData[offset++],originData[offset++],originData[offset++],originData[offset++],originData[offset++]
            };
        } else {
            phoneNum = new byte[]{
                    originData[offset++],originData[offset++],originData[offset++],
                    originData[offset++],originData[offset++],originData[offset++]
            };
        }
        final byte[] streamNum = new byte[]{originData[offset++],originData[offset++]};
        int msgLen = jt808Config.getPackageLength();
        if(originData.length > msgLen){
            // 超长的数据一定是分包合并后的数据 直接获取后边的数据即可 因为已经处理了尾部的校验位
            msgBody = byteArrHelper.subByte(originData, offset);
        }else{
            int bodyLength = originData.length-1-offset;
            msgBody = new byte[bodyLength];
            for(int i=0;i<msgBody.length;i++){
                msgBody[i] = originData[offset++];
            }
        }

        String phone = byteArrHelper.toHexString(phoneNum);
        int msgIdInt = byteArrHelper.twobyte2int(msgId);

        // 检查鉴权记录 看是否链接后鉴权过 成功鉴权才能继续访问其他命令
        // 每次都判断终端鉴权 有些短连接如果每次鉴权 的话 很麻烦 所以推荐使用长链接Map
        boolean isAuth = cacheService.containsAuth(phone);

        if(jt808Config.getAuth()) {
            // 需要检查权限
            // 如果未鉴权 则可以使用 authMsgId 中定义的命令
            if (!isAuth) {
                String[] authMsgIds = jt808Config.getAuthMsgId().split(",");
                for (String authMsgId: authMsgIds){
                    if (authMsgId.length() == 4 && msgIdInt == byteArrHelper.twobyte2int(byteArrHelper.hexStr2bytes(authMsgId))) {
                        isAuth = true;
                        break;
                    }
                }
            }
        } else {
            // 不需要检查权限 直接设置为true 已授权
            isAuth = true;
        }

        if (!isAuth) {
            return resHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x01);
        }

        PackHandler handler = packHandlerManagement.getPackHandler(msgIdInt);
        return handler.handle(phoneNum, streamNum, msgId, msgBody);
    }
}

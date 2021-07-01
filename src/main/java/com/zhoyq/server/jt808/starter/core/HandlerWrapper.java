package com.zhoyq.server.jt808.starter.core;

import com.zhoyq.server.jt808.starter.config.Const;
import com.zhoyq.server.jt808.starter.config.Jt808Config;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.CacheService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2020-10-27
 */
@Slf4j
public class HandlerWrapper {
    SessionManagement sessionManagement;
    CacheService cacheService;
    PackHandlerManagement packHandlerManagement;
    Jt808Config jt808Config;

    public HandlerWrapper (
            SessionManagement sessionManagement,
            CacheService cacheService,
            PackHandlerManagement packHandlerManagement,
            Jt808Config jt808Config
    ) {
        this.sessionManagement = sessionManagement;
        this.cacheService = cacheService;
        this.packHandlerManagement = packHandlerManagement;
        this.jt808Config = jt808Config;
    }

    private byte[] originData;
    private byte[] msgId;
    private byte[] msgBodyProp;
    private byte[] protocolVersion;
    private byte[] phoneNum;
    private byte[] streamNum;
    private byte[] pkgCount;
    private byte[] pkgNum;
    private String phone;
    private boolean hasPackage;
    private boolean isVersion2019;

    public void init(byte[] originData) {
        this.originData = originData;
        int offset = 0;
        msgId = new byte[]{originData[offset++],originData[offset++]};
        msgBodyProp = new byte[]{originData[offset++],originData[offset++]};
        // 通过消息体属性中的版本标识位 判断是否是 2019版本协议 并增加相关解析
        isVersion2019 =  Jt808Helper.isVersion2019(msgBodyProp);
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
        hasPackage = Jt808Helper.hasPackage(msgBodyProp);
        if( hasPackage ){
            pkgCount = new byte[]{originData[offset++],originData[offset++]};
            pkgNum = new byte[]{originData[offset++],originData[offset]};
        }
        phone = ByteArrHelper.toHexString(phoneNum);
    }

    public void handleSession(IoSession session) {
        // 相同身份的终端建立链接 原链接需要断开 也就是加入之前需要判断是否存在终端 存在关闭后在加入
        // 平台流水号 绑定到 session 一直增加即可
        int platStreamNumber = 0;
        if(sessionManagement.contains(phone)){
            IoSession preSession = (IoSession) sessionManagement.get(phone);
            platStreamNumber = (int)preSession.getAttribute(Const.PLATFORM_STREAM_NUMBER);
            if (preSession.getId() != session.getId()) {
                preSession.closeNow();
            }
        }

        // session 加入会话缓存
        sessionManagement.set(phone, session);
        session.setAttribute(phone, platStreamNumber);
    }

    public void handleSession(ChannelHandlerContext ctx) {
        // 相同身份的终端建立链接 原链接需要断开 也就是加入之前需要判断是否存在终端 存在关闭后在加入
        // 平台流水号 绑定到 session 一直增加即可
        int platStreamNumber = 0;
        if(sessionManagement.contains(phone)){
            ChannelHandlerContext preSession = (ChannelHandlerContext)sessionManagement.get(phone);
            platStreamNumber = (int)preSession.channel().attr(AttributeKey.valueOf(Const.PLATFORM_STREAM_NUMBER)).get();
            if (!preSession.name().equals(ctx.name())) {
                preSession.close();
            }
        }

        // session 加入会话缓存
        sessionManagement.set(phone, ctx);
        ctx.channel().attr(AttributeKey.valueOf(Const.PLATFORM_STREAM_NUMBER)).set(platStreamNumber);
    }

    /**
     * 处理消息
     */
    public void handleMessage() {
        byte[] res = null;
        if( hasPackage ){
            int totalPkgNum = ByteArrHelper.twobyte2int(pkgCount);
            int currentPkgNum = ByteArrHelper.twobyte2int(pkgNum);
            // 序号必须小于等于总包数 条件达成之后进行分包处理 否则不处理分包且不处理数据
            if(totalPkgNum >= currentPkgNum){
                if(!cacheService.containsPackages(phone)){
                    ConcurrentHashMap<Integer,byte[]> buf = new ConcurrentHashMap<>(totalPkgNum);
                    cacheService.setPackages(phone, buf);
                }
                Map<Integer,byte[]> pkgBuf = cacheService.getPackages(phone);
                if (pkgBuf != null) {
                    pkgBuf.put(currentPkgNum, originData);
                }
            }
            // 分包结束时需要对分包数据进行解析处理并返回应答 通过总包数和序号对比 判断是不是最后一包
            if( totalPkgNum == currentPkgNum ){
                // 如果是 这个电话的最后一包
                if(Jt808Helper.pkgAllReceived(phone, totalPkgNum)){
                    // 合并所有包 并解析
                    byte[] allPkgData = Jt808Helper.allPkg(phone, totalPkgNum);
                    if (allPkgData != null) {
                        res = handlePackage(allPkgData);
                    }
                }else{
                    // 没有全部收到 需要补传 最初一包的流水号
                    byte[] originStreamNum = null;
                    // 补传id列表
                    byte[] idList = new byte[]{};
                    // 补传数量
                    byte num = 0;
                    Map<Integer,byte[]> map = cacheService.getPackages(phone);
                    if (map != null) {
                        for(int i = 1;i<=totalPkgNum;i++){
                            if(originStreamNum == null){
                                if (isVersion2019) {
                                    originStreamNum = ByteArrHelper.subByte(map.get(1), 15, 17);
                                } else {
                                    originStreamNum = ByteArrHelper.subByte(map.get(1), 10, 12);
                                }
                            }
                            if(!map.containsKey(i)){
                                num++;
                                if (isVersion2019) {
                                    idList = ByteArrHelper.union(idList, ByteArrHelper.subByte(map.get(i), 19, 21));
                                } else {
                                    idList = ByteArrHelper.union(idList, ByteArrHelper.subByte(map.get(i), 14, 16));
                                }
                            }
                        }
                    }
                    if(originStreamNum != null) {
                        res = ResHelper.getPkgReq(phoneNum, originStreamNum, num, idList);
                    }
                }
            }
        }else{
            res =  handlePackage(originData);
        }
        if( res == null ){
            res = ResHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x00);
        }
        // 分包消息总长度
        int msgLen = jt808Config.getPackageLength();

        if( res.length > msgLen ){
            // 分包发送
            if (Const.USE_MINA.equals(jt808Config.getUse())) {
                Jt808Helper.sentByPkg(res, (IoSession) sessionManagement.get(phone));
            } else {
                Jt808Helper.sentByPkg(res, (ChannelHandlerContext) sessionManagement.get(phone));
            }
        }else{
            if (Const.USE_MINA.equals(jt808Config.getUse())) {
                ((IoSession) sessionManagement.get(phone)).write(res);
            } else {
                ((ChannelHandlerContext) sessionManagement.get(phone)).writeAndFlush(res);
            }
        }
    }

    private byte[] handlePackage(byte[] data) {
        byte[] msgBody;

        int offset = 0;

        final byte[] msgId = new byte[]{data[offset++],data[offset++]};
        final byte[] msgBodyProp = new byte[]{data[offset++],data[offset++]};
        // 通过消息体属性中的版本标识位 判断是否是 2019版本协议 并增加相关解析
        byte[] phoneNum;
        if (Jt808Helper.isVersion2019(msgBodyProp)) {
            // 忽略 协议版本解析
            offset++;
            phoneNum = new byte[]{
                    data[offset++],data[offset++],data[offset++],data[offset++],data[offset++],
                    data[offset++],data[offset++],data[offset++],data[offset++],data[offset++]
            };
        } else {
            phoneNum = new byte[]{
                    data[offset++],data[offset++],data[offset++],
                    data[offset++],data[offset++],data[offset++]
            };
        }
        final byte[] streamNum = new byte[]{data[offset++],data[offset++]};
        if (this.hasPackage) {
            // 超长的数据一定是分包合并后的数据 直接获取后边的数据即可 因为已经处理了尾部的校验位

            // 过滤掉消息包封装项
            // 感谢 https://github.com/bigbeef 提交的建议

            offset += 4;
            msgBody = ByteArrHelper.subByte(data, offset);
        } else {
            int bodyLength = data.length-1-offset;
            msgBody = new byte[bodyLength];
            for(int i=0;i<msgBody.length;i++){
                msgBody[i] = data[offset++];
            }
            // 无分包需要 单独处理 RSA 加密
            // 解密失败 直接返回 失败应答
            boolean hasRsa = Jt808Helper.checkRsa(msgBodyProp);
            if (hasRsa) {
                try {
                    msgBody = Jt808Helper.rsa(msgBody);
                } catch (Exception e) {
                    log.warn(e.getMessage());
                    log.warn("{} rsa 解密失败", phone);
                    return ResHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x01);
                }
            }

        }

        String phone = ByteArrHelper.toHexString(phoneNum);
        int msgIdInt = ByteArrHelper.twobyte2int(msgId);

        // 检查鉴权记录 看是否链接后鉴权过 成功鉴权才能继续访问其他命令
        // 每次都判断终端鉴权 有些短连接如果每次鉴权 的话 很麻烦 所以推荐使用长链接Map
        boolean isAuth = cacheService.containsAuth(phone);

        if(jt808Config.getAuth()) {
            // 需要检查权限
            // 如果未鉴权 则可以使用 authMsgId 中定义的命令
            if (!isAuth) {
                String[] authMsgIds = jt808Config.getAuthMsgId().split(",");
                for (String authMsgId: authMsgIds){
                    if (authMsgId.length() == 4 && msgIdInt == ByteArrHelper.twobyte2int(ByteArrHelper.hexStr2bytes(authMsgId))) {
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
            return ResHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x01);
        }

        PackHandler handler = packHandlerManagement.getPackHandler(msgIdInt);
        return handler.handle(phoneNum, streamNum, msgId, msgBody);
    }
}

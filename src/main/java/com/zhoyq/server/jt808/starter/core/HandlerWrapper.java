package com.zhoyq.server.jt808.starter.core;

import com.zhoyq.server.jt808.starter.config.Const;
import com.zhoyq.server.jt808.starter.config.Jt808Config;
import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.helper.Jt808Helper;
import com.zhoyq.server.jt808.starter.helper.ResHelper;
import com.zhoyq.server.jt808.starter.service.CacheService;
import io.netty.channel.ChannelHandlerContext;
import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhoyq &lt;feedback@zhoyq.com&gt;
 * @date 2020-10-27
 */
@Component
public class HandlerWrapper {
    private Jt808Helper jt808Helper;
    private ByteArrHelper byteArrHelper;
    private ResHelper resHelper;
    private SessionManagement sessionManagement;
    private CacheService cacheService;
    private PackHandlerManagement packHandlerManagement;
    private Jt808Config jt808Config;

    public HandlerWrapper (
            Jt808Helper jt808Helper,
            ByteArrHelper byteArrHelper,
            ResHelper resHelper,
            SessionManagement sessionManagement,
            CacheService cacheService,
            PackHandlerManagement packHandlerManagement,
            Jt808Config jt808Config
    ) {
        this.jt808Helper = jt808Helper;
        this.byteArrHelper = byteArrHelper;
        this.resHelper = resHelper;
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
        isVersion2019 =  jt808Helper.isVersion2019(msgBodyProp);
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
        hasPackage = jt808Helper.hasPackage(msgBodyProp);
        if( hasPackage ){
            pkgCount = new byte[]{originData[offset++],originData[offset++]};
            pkgNum = new byte[]{originData[offset++],originData[offset]};
        }
        phone = byteArrHelper.toHexString(phoneNum);
    }

    public void handleSession(IoSession session) {
        // 相同身份的终端建立链接 原链接需要断开 也就是加入之前需要判断是否存在终端 存在关闭后在加入
        if(sessionManagement.contains(phone)){
            IoSession preSession = (IoSession) sessionManagement.get(phone);
            if (preSession.getId() != session.getId()) {
                preSession.closeNow();
            }
        }

        // session 加入会话缓存
        sessionManagement.set(phone, session);
    }

    public void handleSession(ChannelHandlerContext ctx) {
        // 相同身份的终端建立链接 原链接需要断开 也就是加入之前需要判断是否存在终端 存在关闭后在加入
        if(sessionManagement.contains(phone)){
            ChannelHandlerContext preSession = (ChannelHandlerContext)sessionManagement.get(phone);
            if (!preSession.name().equals(ctx.name())) {
                preSession.close();
            }
        }

        // session 加入会话缓存
        sessionManagement.set(phone, ctx);
    }

    /**
     * TODO 处理 rsa
     * boolean useRsa = jt808Helper.checkRsa(msgBodyProp);
     * if (useRsa) {
     *     byte[] en = dataService.terminalRsa(phone);
     * }
     */
    public void handleMessage() {
        byte[] res = null;
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
                pkgBuf.put(currentPkgNum, originData);
            }
            // 分包结束时需要对分包数据进行解析处理并返回应答 通过总包数和序号对比 判断是不是最后一包
            if( totalPkgNum == currentPkgNum ){
                // 如果是 这个电话的最后一包
                if(jt808Helper.pkgAllReceived(phone, totalPkgNum)){
                    // 合并所有包 并解析
                    res = handlePackage(jt808Helper.allPkg(phone, totalPkgNum));
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
            res =  handlePackage(originData);
        }
        if( res == null ){
            res = resHelper.getPlatAnswer(phoneNum, streamNum, msgId, (byte) 0x00);
        }
        // 分包消息总长度
        int msgLen = jt808Config.getPackageLength();

        if( res.length > msgLen ){
            // 分包发送
            if (Const.USE_MINA.equals(jt808Config.getUse())) {
                jt808Helper.sentByPkg(res, (IoSession) sessionManagement.get(phone));
            } else {
                jt808Helper.sentByPkg(res, (ChannelHandlerContext) sessionManagement.get(phone));
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

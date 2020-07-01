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

package com.zhoyq.server.jt808.starter.core;

import com.zhoyq.server.jt808.starter.helper.ByteArrHelper;
import com.zhoyq.server.jt808.starter.pack.NoSupportHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/4
 */
@Slf4j
@Component
@AllArgsConstructor
public class PackHandlerManagement implements ApplicationContextAware {

    /**
     * 所有实现的包处理器
     */
    private static Map<Integer, PackHandler> packHandlerMap;

    /**
     * 不支持的协议消息
     */
    private NoSupportHandler noSupportHandler;
    private ByteArrHelper byteArrHelper;

    /**
     * 唤醒时 初始化 packHandlerMap
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 仅一次性初始化完成
        if(packHandlerMap == null){
            packHandlerMap = new ConcurrentHashMap<>();

            Map<String, Object> handlers =  applicationContext.getBeansWithAnnotation(Jt808Pack.class);
            for (Map.Entry<String, Object> entry : handlers.entrySet()) {
                Object handler = entry.getValue();
                if (handler instanceof PackHandler) {
                    Jt808Pack packConfig = handler.getClass().getAnnotation(Jt808Pack.class);
                    if(packConfig != null && !packHandlerMap.containsKey(packConfig.msgId())){
                        // 不存在定义才加入
                        log.trace("add pack handler {} for {}", handler.getClass().getName(),
                                byteArrHelper.toHexString(byteArrHelper.int2twobytes(packConfig.msgId())));
                        packHandlerMap.put(packConfig.msgId() ,(PackHandler)handler);
                    }
                }
            }
        }
    }

    public PackHandler getPackHandler(int msgId) {
        return packHandlerMap == null? noSupportHandler : packHandlerMap.get(msgId);
    }
}

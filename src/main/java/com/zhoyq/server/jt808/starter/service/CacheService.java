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

package com.zhoyq.server.jt808.starter.service;

import java.util.Map;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/16
 */
public interface CacheService {
    /**
     * 是否含此电话号码对应的包
     * @param phone 终端对应 12 位电话号码
     * @return 是否含有
     */
    boolean containsPackages(String phone);

    /**
     * 设置电话号码对应的包组
     * @param phone 终端对应 12 位电话号码
     * @param packages 包列表
     */
    void setPackages(String phone, Map<Integer,byte[]> packages);

    /**
     * 获取电话号码对应的包组
     * @param phone 终端对应 12 位电话号码
     * @return 包列表
     */
    Map<Integer,byte[]> getPackages(String phone);

    /**
     * 电话号码对应的会话是否已经鉴权
     * @param phone 终端对应 12 位电话号码
     * @return 鉴权与否
     */
    boolean containsAuth(String phone);

    /**
     * 去掉电话号码对应的鉴权信息
     * @param phone 终端对应 12 位电话号码
     */
    void removeAuth(String phone);

    /**
     * 通过电话号码获取鉴权码
     * @param phone 终端对应 12 位电话号码
     * @return 鉴权码
     */
    String getAuth(String phone);

    /**
     * 设置电话号码对应的鉴权码
     * @param phone 终端对应 12 位电话号码
     * @param str 鉴权码
     */
    void setAuth(String phone, String str);

    /**
     * 是否含此电话号码对应的包
     * @param phone 终端对应 12 位电话号码
     * @return 是否含有
     */
    boolean containsSentPackages(String phone);

    /**
     * 设置电话号码对应的包组
     * @param phone 终端对应 12 位电话号码
     * @param packages 包列表
     */
    void setSentPackages(String phone, Map<Integer,byte[]> packages);

    /**
     * 获取电话号码对应的包组
     * @param phone 终端对应 12 位电话号码
     * @return 包列表
     */
    Map<Integer,byte[]> getSentPackages(String phone);
}

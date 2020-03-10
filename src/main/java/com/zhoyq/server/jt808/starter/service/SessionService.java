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

package com.zhoyq.server.jt808.starter.service;

import java.util.Map;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/2/16
 */
public interface SessionService {
    /**
     * 是否含此电话号码的会话
     * @param phone 终端对应 12 位电话号码
     * @return 是否含有
     */
    boolean contains(String phone);

    /**
     * 获取电话号码对应的会话
     * @param phone 终端对应 12 位电话号码
     * @return 会话
     */
    Object get(String phone);

    /**
     * 设置电话号码对应会话 存在替换
     * @param phone 终端对应 12 位电话号码
     * @param session 会话
     */
    void set(String phone, Object session);

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
     * 设置电话号码对应的设备号
     * @param phone 终端对应 12 位电话号码
     * @param device 设备识别号
     */
    void setDevice(String phone, String device);

    /**
     * 获取设备ID
     * @param phone 终端对应 12 位电话号码
     * @return 设备ID
     */
    String getDevice(String phone);
}

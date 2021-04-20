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

package com.zhoyq.server.jt808.starter.service.impl;

import com.zhoyq.server.jt808.starter.dto.FileUploadAnswerPkg;
import com.zhoyq.server.jt808.starter.service.CacheService;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhoyq <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2020/5/7
 */
public class HashMapCacheService implements CacheService {

    // ==== \/ package

    private static SoftReference<Map<String, Map<Integer, byte[]>>> packageMapRef = new SoftReference<>(new HashMap<>());

    @Override
    public boolean containsPackages(String phone) {
        Map<String, Map<Integer, byte[]>> stringMapMap = packageMapRef.get();
        if (stringMapMap != null) {
            return stringMapMap.containsKey(phone);
        }
        return false;
    }

    @Override
    public void setPackages(String phone, Map<Integer, byte[]> packages) {
        Map<String, Map<Integer, byte[]>> stringMapMap = packageMapRef.get();
        if (stringMapMap != null) {
            stringMapMap.put(phone, packages);
        } else {
            packageMapRef = new SoftReference<>(Collections.singletonMap(phone, packages));
        }
    }

    @Override
    public Map<Integer, byte[]> getPackages(String phone) {
        Map<String, Map<Integer, byte[]>> stringMapMap = packageMapRef.get();
        if (stringMapMap != null) {
            return stringMapMap.get(phone);
        }
        return null;
    }


    // ==== \/ auth
    private static Map<String, String> authMap = new ConcurrentHashMap<>();

    @Override
    public boolean containsAuth(String phone) {
        return authMap.containsKey(phone);
    }

    @Override
    public void removeAuth(String phone) {
        authMap.remove(phone);
    }

    @Override
    public String getAuth(String phone) {
        return authMap.get(phone);
    }

    @Override
    public void setAuth(String phone, String str) {
        authMap.put(phone, str);
    }

    // ==== \/ sent package - 目前进保存前一次下发的数据的缓存

    private static Map<String, Map<Integer, byte[]>> sentPackageMap = new ConcurrentHashMap<>();

    @Override
    public boolean containsSentPackages(String phone) {
        return sentPackageMap.containsKey(phone);
    }

    @Override
    public void setSentPackages(String phone, Map<Integer, byte[]> packages) {
        sentPackageMap.put(phone, packages);
    }

    @Override
    public Map<Integer, byte[]> getSentPackages(String phone) {
        return sentPackageMap.get(phone);
    }


    // ==== \/ 苏标：stream upload

    @Override
    public void startSuStreamUpload(String fileName, long fileLength) {

    }

    @Override
    public byte[] stopSuStreamUpload(String fileName) {
        return new byte[0];
    }

    @Override
    public void addSuStreamUpload(String fileName, byte[] data) {

    }

    @Override
    public boolean checkSuStreamUploadOver(String fileName) {
        return false;
    }

    @Override
    public boolean checkSuStreamUpload(String fileName) {
        return false;
    }

    @Override
    public List<FileUploadAnswerPkg> getSuStreamReUpload(String fileName) {
        return null;
    }
}

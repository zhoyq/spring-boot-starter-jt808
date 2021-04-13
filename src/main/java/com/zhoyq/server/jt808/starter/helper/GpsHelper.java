/*
 *  Copyright (c) 2021. 刘路 All rights reserved
 *  版权所有 刘路 并保留所有权利 2021.
 *  ============================================================================
 *  这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
 *  使用。不允许对程序代码以任何形式任何目的的再发布。如果项目发布携带作者
 *  认可的特殊 LICENSE 则按照 LICENSE 执行，废除上面内容。请保留原作者信息。
 *  ============================================================================
 *  刘路（feedback@zhoyq.com）于 2021. 创建
 *  http://zhoyq.com
 */

package com.zhoyq.server.jt808.starter.helper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 坐标转换工具类
 * WGS84: Google Earth采用，Google Map中国范围外使用
 * GCJ02: 火星坐标系，中国国家测绘局制定的坐标系统，由WGS84机密后的坐标。Google Map中国和搜搜地图使用，高德
 * BD09:百度坐标，GCJ02机密后的坐标系
 * 搜狗坐标系，图吧坐标等，估计也是在GCJ02基础上加密而成的
 * @author 刘路 <a href="mailto:feedback@zhoyq.com">feedback@zhoyq.com</a>
 * @date 2021/4/13
 */
public class GpsHelper {
    public static final String BAIDU_LBS_TYPE = "bd09ll";
    public static double pi = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Lonlat{
        private double lon;
        private double lat;
    }

    /**
     * WGS-84 to GCJ-02(火星坐标系)
     */
    public static Lonlat gps84ToGcj02(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Lonlat(mgLon, mgLat);
    }

    /**
     * GCJ-02（火星坐标系）to WGS-84
     */
    public static Lonlat gcj02ToGps84(double lat, double lon) {
        Lonlat gps = transform(lat, lon);
        double lontitude = lon * 2 - gps.getLon();
        double latitude = lat * 2 - gps.getLat();
        return new Lonlat(lontitude, latitude);
    }

    /**
     * GCJ-02（火星坐标系）to BD-09（百度坐标系）
     */
    public static Lonlat gcj02ToBd09(double gg_lat, double gg_lon) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new Lonlat(bd_lon, bd_lat);
    }

    /**
     * BD-09（百度坐标系） to GCJ-02（火星坐标系）
     */
    public static Lonlat bd09ToGcj02(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Lonlat(gg_lon, gg_lat);
    }

    /**
     * BD-09（百度坐标系） to WGS-84
     */
    public static Lonlat bd09ToGps84(double bd_lat, double bd_lon) {
        Lonlat gcj02 = bd09ToGcj02(bd_lat, bd_lon);
        return gcj02ToGps84(gcj02.getLat(), gcj02.getLon());
    }

    /**
     * BD-09（百度坐标系） to WGS-84
     */
    public static Lonlat gps84ToBd09(double lat, double lon) {
        Lonlat gcj02 = gps84ToGcj02(lat, lon);
        if (gcj02 == null) {
            return null;
        }
        double lonBuf = gcj02.getLon();
        double latBuf = gcj02.getLat();
        return gcj02ToBd09(latBuf, lonBuf);
    }

    /**
     * 中国境内
     */
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static Lonlat transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new Lonlat(lon, lat);
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Lonlat(mgLon, mgLat);
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }


    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }
}

package com.alinaxi.location.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @desc:TODO
 * @author: xiefapan
 * @date: 2020/2/15  15:20
 */

public class AreaUtils
{
    /**
     * 是否是省级单位，省级单位以0000结尾
     *
     * @param code
     * @return
     */
    public static boolean isProvince(String code)
    {
        return StringUtils.endsWith(code, "0000");
    }

    /**
     * 是否是直辖市
     *
     * @param provinceName
     * @return
     */
    public static boolean isDirectlyCity(String provinceName)
    {
        return provinceName.contains("北京") || provinceName.contains("上海") || provinceName.contains("天津") ||
               provinceName.contains("重庆");
    }

    /**
     * 是否是省下面的市
     *
     * @param provinceCode
     * @param areaCode
     * @return
     */
    public static boolean isProvinceCity(String provinceCode, String areaCode)
    {
        //不能和省级单位编码相同
        if (StringUtils.equals(provinceCode, areaCode))
        {
            return false;
        }

        //前2位相同，后两位为0
        if (equalStart(areaCode, provinceCode, 2) && StringUtils.endsWith(areaCode, "00"))
        {
            return true;
        }
        return false;
    }

    /**
     * 计算区县上级市的编码，前4位+00，只针对市下辖的县区，省直辖县区不生效
     * @param countyCode
     * @return
     */
    public static String calculateCityCode(String countyCode)
    {
       return StringUtils.substring(countyCode, 0, 4) + "00";
    }

    /**
     * 计算区县所在省的编码，前2位+0000
     * @param countyCode
     * @return
     */
    public static String calculateProvinceCode(String countyCode)
    {
        return StringUtils.substring(countyCode, 0, 2) + "0000";
    }


    /**
     * 是否是市下面的县区
     *
     * @param cityCode
     * @param areaCode
     * @return
     */
    public static boolean isCityCounty(String cityName, String cityCode, String areaCode)
    {
        //不能和市级单位编码相同
        if (StringUtils.equals(cityCode, areaCode))
        {
            return false;
        }

        //判断直辖市情况，前两位相同
        if (AreaUtils.isDirectlyCity(cityName))
        {
            if (equalStart(areaCode, cityCode, 2))
            {
                return true;
            }
        }

        //前4位相同
        if (equalStart(areaCode, cityCode, 3))
        {
            return true;
        }
        return false;
    }

    /**
     * 是否是省直辖县区
     *
     * @param provinceCode
     * @param areaCode
     * @return
     */
    public static boolean isDirectlyCounty(String provinceCode, String areaCode)
    {
        //不能和省级单位编码相同
        if (StringUtils.equals(provinceCode, areaCode))
        {
            return false;
        }

        //前2位相同，后两位不为0
        if (equalStart(areaCode, provinceCode, 2) && !StringUtils.endsWith(areaCode, "00"))
        {
            return true;
        }
        return false;
    }

    /**
     * 判断前几位是否相同
     *
     * @param str1
     * @param str2
     * @param end
     * @return
     */
    private static boolean equalStart(String str1, String str2, int end)
    {
        return StringUtils.startsWith(str1, StringUtils.substring(str2, 0, end));
    }
}
package com.alinaxi.location.model;

/**
 * @desc:区域类型
 * @author: xiefapan
 * @date: 2020/2/14  14:31
 */
public enum AreaType{

    /**
     * 省，直辖市
     */
    PROVINCE,

    /**
     * 省下辖的市
     */
    CITY,

    /**
     * 县、区
     */
    COUNTY,

    /**
     * 省直辖的县，如湖北仙桃
     */
    DIRECTCOUNTY,

    /**
     * 直辖市中间的一层虚拟节点，如北京市-市辖区
     */
    VIRTUALCITY;
}
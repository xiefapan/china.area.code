package com.alinaxi.location;

import com.alinaxi.location.model.Area;
import com.alinaxi.location.model.AreaType;

import java.util.Map;
import java.util.TreeMap;

/**
 * @desc:TODO
 * @author: xiefapan
 * @date: 2020/2/15  15:16
 */

public class AreaModel
{
    private Map<String, Area> provinceMap = new TreeMap<>();

    private Map<String, Area> cityMap = new TreeMap<>();

    private Map<String, Area> countyMap = new TreeMap<>();

    /**
     * 省直辖县集合
     */
    private Map<String, Area> directlyCountyMap = new TreeMap<>();


    private Map<String, Area> areaMap = new TreeMap<>();

    public Map<String, Area> getProvinceMap()
    {
        return provinceMap;
    }

    public Map<String, Area> getCityMap()
    {
        return cityMap;
    }

    public Map<String, Area> getCountyMap()
    {
        return countyMap;
    }

    public Map<String, Area> getAreaMap()
    {
        return areaMap;
    }


    public void addProvince(Area area){
        area.setAreaType(AreaType.PROVINCE);

        this.provinceMap.put(area.getCode(),area);
        this.areaMap.put(area.getCode(),area);
    }

    public void addCity(Area area){
        area.setAreaType(AreaType.CITY);

        this.cityMap.put(area.getCode(),area);
        this.areaMap.put(area.getCode(),area);
    }

    public void addCounty(Area area){
        area.setAreaType(AreaType.COUNTY);

        this.countyMap.put(area.getCode(),area);
        this.areaMap.put(area.getCode(),area);
    }

    public Map<String, Area> getDirectlyCountyMap()
    {
        return directlyCountyMap;
    }
}
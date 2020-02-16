package com.alinaxi.location.generate;

import com.alinaxi.location.AreaModel;
import com.alinaxi.location.model.Area;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @desc:生成有赞Vant框架area.json文件 https://github.com/youzan/vant/blob/dev/src/area/demo/area.js
 * @author: xiefapan
 * @date: 2020/2/14  14:42
 */

public class VantAreaJsGenerator extends BaseVantGenerator implements IGenerator
{
    private Map<String, Area> provinceMap;
    private Map<String, Area> cityMap;
    private Map<String, Area> countyMap;

    private String basePath;

    public VantAreaJsGenerator(AreaModel areaModel, String basePath)
    {
        this.basePath = basePath;

        processData(areaModel);
    }

    /**
     * 处理特殊数据
     */
    protected void processData(AreaModel areaModel)
    {
        //有赞的vant框架对省直辖县是采用中间加一层省直辖县，需要对数据做特殊处理

        //目前拥有省直辖县的有//
        // 河南省-济源市(419001),
        // 湖北省-仙桃市等(429004,429005,429006,429021),
        // 海南省-五指山市等（469001-469030）
        // 新疆-石河子等（659001-659009）

        this.provinceMap = areaModel.getProvinceMap();

        this.cityMap = new TreeMap<>();
        cityMap.putAll(areaModel.getCityMap());

        this.countyMap = new TreeMap<>();
        countyMap.putAll(areaModel.getCountyMap());

        //移除原来放到city列表内的省直辖县
        final Map<String, Area> directlyCountyMap = areaModel.getDirectlyCountyMap();
        for (String code : directlyCountyMap.keySet())
        {
            cityMap.remove(code);
        }

        //将省直辖县加到county列表中
        this.countyMap.putAll(directlyCountyMap);


        //市级单位列表增加几个虚拟节点
        putNewArea(cityMap, "419000", "省直辖县");
        putNewArea(cityMap, "429000", "省直辖县");
        putNewArea(cityMap, "469000", "省直辖县");
        putNewArea(cityMap, "659000", "自治区直辖县");

    }

    private void putNewArea(Map<String, Area> newCityMap, String code, String name)
    {
        final Area area = new Area();
        area.setName(name);
        area.setCode(code);

        newCityMap.put(code, area);
    }

    @Override
    public void generate()
    {
        generateAreaJs();
    }


    private void generateAreaJs()
    {

        final String content = generateAreaJsContent(provinceMap.values(), cityMap.values(), countyMap.values());


        final String filePath = basePath + "/vant/area.js";

        try
        {
            FileUtils.writeStringToFile(new File(filePath), content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
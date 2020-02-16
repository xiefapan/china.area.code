package com.alinaxi.location.generate;

import com.alinaxi.location.AreaModel;
import com.alinaxi.location.model.Area;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 对有赞Vant框架area.json文件做了特殊处理
 * 省直辖县做了一层特殊的编码对应，生成area-decode.js文件
 * 实现省直辖县放在第二层展示
 *
 * @author: xiefapan
 * @date: 2020/2/14  14:42
 */

public class EncodeVantAreaJsGenerator extends BaseVantGenerator implements IGenerator
{
    private Map<String, Area> provinceMap;
    private Map<String, Area> cityMap;
    private Map<String, Area> countyMap;

    private Map<String, List<Integer>> provinceCityCodeMap = new HashMap<>();

    private Map<String, Area> encodeDirectCountyMap = new TreeMap<>();

    private String basePath;

    public EncodeVantAreaJsGenerator(AreaModel areaModel, String basePath)
    {
        this.basePath = basePath;
        processData(areaModel);
    }

    /**
     * 处理特殊数据
     */
    protected void processData(AreaModel areaModel)
    {
        this.provinceMap = areaModel.getProvinceMap();
        this.cityMap = areaModel.getCityMap();
        this.countyMap = areaModel.getCountyMap();

        processDirectCounty(areaModel.getDirectlyCountyMap());

    }

    private void processDirectCounty(Map<String, Area> originDirectCountyMap)
    {
        for (Area directlyCounty : originDirectCountyMap.values())
        {
            final String provinceCode = directlyCounty.getParentCode();

            String provincePrefix = StringUtils.substring(provinceCode, 0, 2);

            final Area province = provinceMap.get(provinceCode);
            if (province != null)
            {
                final List<Integer> sortedCityCode = getSortedCityCode(province);
                final Integer last = sortedCityCode.get(sortedCityCode.size() - 1);

                Integer newCode = last + 1;
                if (newCode > 99)
                {
                    for (int i = 99; i > 10; i--)
                    {
                        if (!sortedCityCode.contains(i))
                        {
                            newCode = i;
                        }
                    }
                }


                sortedCityCode.add(newCode);

                String newCityCode = provincePrefix + newCode + "00";
                if (newCode < 10)
                {
                    newCityCode = provincePrefix + "0" + newCode + "00";
                }


                final Area newCity = new Area();
                newCity.setCode(newCityCode);
                newCity.setName(directlyCounty.getName());

                encodeDirectCountyMap.put(directlyCounty.getCode(), newCity);

            }

        }
    }

    private List<Integer> getSortedCityCode(Area province)
    {
        final String code = province.getCode();

        List<Integer> list;
        if (provinceCityCodeMap.containsKey(code))
        {
            list = provinceCityCodeMap.get(code);
        }
        else
        {
            list = new LinkedList<>();
            provinceCityCodeMap.put(code, list);

            for (Area city : province.getChildren())
            {
                String cityCode = StringUtils.substring(city.getCode(), 2, 4);
                final Integer anInt = Integer.parseInt(cityCode);
                list.add(anInt);
            }

            Collections.sort(list);
        }


        return list;
    }


    @Override
    public void generate()
    {
        generateAreaJs();

        generateDecodeJs();
    }

    @Override
    protected String generateCityItem(Area city)
    {
        if (encodeDirectCountyMap.containsKey(city.getCode()))
        {
            return generateAreaItem(encodeDirectCountyMap.get(city.getCode()));
        }
        return generateAreaItem(city);
    }

    private void generateAreaJs()
    {

        final String content = generateAreaJsContent(provinceMap.values(), cityMap.values(), countyMap.values());


        final String filePath = basePath + "/vant-encode/area.js";

        try
        {
            FileUtils.writeStringToFile(new File(filePath), content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    private void generateDecodeJs()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("export default {\n\t");

        builder.append("code: {\n");

        final Iterator<Map.Entry<String, Area>> iterator = encodeDirectCountyMap.entrySet().iterator();
        while (iterator.hasNext())
        {
            final Map.Entry<String, Area> next = iterator.next();

            final String newCode = next.getValue().getCode();
            final String oldCode = next.getKey();

            builder.append("\t\t").append(newCode).append(": ");
            builder.append("\'").append(oldCode).append("\',\n");

        }

        builder.append("\t").append("}\n}");

        final String content = builder.toString();

        final String filePath = basePath + "/vant-encode/area-decode.js";

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
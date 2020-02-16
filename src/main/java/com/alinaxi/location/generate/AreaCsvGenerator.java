package com.alinaxi.location.generate;

import com.alinaxi.location.AreaModel;
import com.alinaxi.location.model.Area;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @desc:生产csv文件，可用于数据库导入
 * @author: xiefapan
 * @date: 2020/2/14  14:42
 */

public class AreaCsvGenerator implements IGenerator
{
    private AreaModel areaModel;

    private String basePath;

    public AreaCsvGenerator(AreaModel areaModel, String basePath)
    {
        this.areaModel = areaModel;
        this.basePath = basePath;
    }

    @Override
    public void generate()
    {
        generateLocationCsv();
    }


    private String generateContent()
    {
        StringBuilder builder = new StringBuilder("code,name,note,parentId");

        for (Area province : areaModel.getProvinceMap().values())
        {
            generateProvicnce(province, builder);
        }

        return builder.toString();


    }

    private void generateLocationCsv()
    {

        final String content = generateContent();


        final String filePath = basePath + "/csv/area.csv";

        try
        {
            FileUtils.writeStringToFile(new File(filePath), content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    private void generateProvicnce(Area province, StringBuilder builder)
    {

        //先生成一条省自身的数据
        final String provinceStr = generateAreaItem(province);
        builder.append("\n").append(provinceStr);

        //生成省下面市的数据
        for (Area city : province.getChildren())
        {
            generateCity(city, builder);
        }


    }

    private String generateCity(Area city, StringBuilder builder)
    {
        //先生成一条市自身的数据
        final String cityStr = generateAreaItem(city);
        builder.append("\n").append(cityStr);

        //生成省下面各区县的数据
        for (Area county : city.getChildren())
        {
            final String countyStr = generateAreaItem(county);
            builder.append("\n").append(countyStr);
        }

        return builder.toString();
    }

    private String generateAreaItem(Area area)
    {
        //"110101","东城区","北京市-东城区","110100"

        final StringBuilder builder = new StringBuilder();

        builder.append("\"").append(area.getCode()).append("\",");

        builder.append("\"").append(area.getName()).append("\",");

        builder.append("\"").append(area.getFullName()).append("\",");

        builder.append("\"").append(area.getParentCode()).append("\"");

        return builder.toString();
    }

}
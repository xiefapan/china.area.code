package com.alinaxi.location.generate;

import com.alinaxi.location.model.Area;

import java.util.Collection;

/**
 * @desc:TODO
 * @author: xiefapan
 * @date: 2020/2/16  11:38
 */

public class BaseVantGenerator
{
    protected String generateAreaJsContent(Collection<Area> provinceList,Collection<Area> cityList,Collection<Area>
            countyList)
    {
        StringBuilder builder = new StringBuilder("export default {\n\t");

        String provinceStr = generateProvince(provinceList);
        builder.append(provinceStr).append(",\n");

        String cityStr = generateCity(cityList);
        builder.append(cityStr).append(",\n");

        String countyStr = generateCounty(countyList);
        builder.append(countyStr);

        builder.append("}");

        return builder.toString();


    }

    protected String generateProvince(Collection<Area> provinceList)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("  province_list: {\n\t");

        for (Area province : provinceList)
        {
            builder.append(generateAreaItem(province));
        }


        builder.append("\n}");

        return builder.toString();
    }

    protected String generateCity(Collection<Area> cityList)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("  city_list: {\n");


        for (Area city : cityList)
        {

            builder.append(generateCityItem(city));

        }

        builder.append("\n}");

        return builder.toString();
    }

    protected String generateCityItem(Area city){
        return generateAreaItem(city);
    }




    protected String generateCounty(Collection<Area> countyList)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("  county_list: {\n");

        for (Area county : countyList)
        {
            builder.append(generateAreaItem(county));
        }


        builder.append("\n}");

        return builder.toString();
    }

    protected String generateAreaItem(Area area)
    {
        return "\t\t"+area.getCode() + ": \'" + area.getName() + "\',\n";
    }

}
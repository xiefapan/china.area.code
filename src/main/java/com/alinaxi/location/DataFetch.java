package com.alinaxi.location;

import com.alinaxi.location.model.Area;
import com.alinaxi.location.model.AreaType;
import com.alinaxi.location.util.AreaUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.MessageFormat;
import java.util.List;

/**
 * @desc:数据抓取
 * @author: xiefapan
 * @date: 2020/2/14  15:56
 */

public class DataFetch
{
    //民政部地区数据网址，会定期更新
    private final static String url = "http://www.mca.gov.cn/article/sj/xzqh/2019/2019/201912251506.html";

    private AreaModel areaModel = new AreaModel();

    private Area lastProvince;

    /**
     * 全名之间的连接
     */
    private final static String FULLNAME_JION_STR = "-";

    public DataFetch(AreaModel areaModel)
    {
        this.areaModel = areaModel;
    }

    public AreaModel fetch()
    {
        try
        {
            //2019年11月中华人民共和国县以上行政区划代码网页
            Document doc = Jsoup.connect(url).maxBodySize(0).get();

            //省和市
            processProvinceAndCityElements(doc);

            //区县
            processCountyElements(doc);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (this.areaModel.getProvinceMap().size() != 34)
        {
            throw new RuntimeException("解析出来的省级单位不是34个");
        }

        return areaModel;

    }

    private void processProvinceAndCityElements(Document doc)
    {
        Elements elementsProAndCity = doc.getElementsByClass("xl7028029");
        List<String> eachTextList = elementsProAndCity.eachText();
        for (int i = 0; i < eachTextList.size(); i = i + 2)
        {
            final String code = eachTextList.get(i);
            final String name = eachTextList.get(i + 1);

            final Area area = new Area();
            area.setCode(code);
            area.setName(name);

            //处理省级单位，省级行政单位编码是有规律的，以0000结尾
            if (AreaUtils.isProvince(area.getCode()))
            {
                processProvince(area, name, code);
            }
            else
            {
                if (lastProvince == null)
                {
                    throw new RuntimeException("未找到对应的省，code:" + code);
                }
                //处理省辖市
                if (AreaUtils.isProvinceCity(lastProvince.getCode(), code))
                {
                    processProvinceCity(lastProvince, area);
                }
                else
                {
                    throw new RuntimeException("未找到对应的省，code:" + code + ",lastProvince:" + lastProvince.toString());
                }
            }

        }
    }

    private void processCountyElements(Document doc)
    {
        //区县
        Elements countyElements = doc.getElementsByClass("xl7128029");
        List<String> eachTextList = countyElements.eachText();
        for (int i = 0; i < eachTextList.size(); i = i + 2)
        {
            final String code = eachTextList.get(i);
            final String name = eachTextList.get(i + 1);


            final String cityCode = AreaUtils.calculateCityCode(code);
            Area city = areaModel.getCityMap().get(cityCode);
            if (city != null)
            {
//                if(AreaType.VIRTUALCITY.equals(city.getAreaType())){
//                    final String parentCode = city.getParentCode();
//                    city = areaModel.getProvinceMap().get(parentCode);
//                }
                addCityCounty(city, name, code);
            }
            else
            {
                //处理湖北仙桃，石河子等特殊的省直辖的县区需要把它们放到省的下一级中
                final String provinceCode = AreaUtils.calculateProvinceCode(code);
                final Area province = areaModel.getProvinceMap().get(provinceCode);
                if (province == null)
                {
                    throw new RuntimeException("未找到对应的省，code:" + provinceCode);
                }

                //处理直辖市下特殊的县区，如重庆市城口县(500229),前4位与虚拟的重庆节点500100不一样
                if (AreaUtils.isDirectlyCity(province.getName()))
                {
                    final Area area = province.getChildren().get(0);
                    addCityCounty(area, name, code);
                }
                else
                {
                    processDirectlyCounty(province, name, code);
                }

            }

        }
    }


    private void processProvince(Area area, String name, String code)
    {
        this.areaModel.addProvince(area);
        area.setParentCode("0");
        area.setFullName(name);

        //如果是直辖市，加一个市辖区
        if (AreaUtils.isDirectlyCity(name))
        {
            processDirectlyCity(area, name, code);

        }
        this.lastProvince = area;
    }

    /**
     * 处理直辖市的情况，直辖市市级单位也叫对应的城市名
     *
     * @param province
     * @param provinceName
     * @param provinceCode
     */
    private void processDirectlyCity(Area province, String provinceName, String provinceCode)
    {
        //直辖市的市级使用倒数第三位加1的规则，如北京110000，市级就是110100
        String cityCode = StringUtils.substring(provinceCode, 0, 3).concat("100");

        Area virtualCity = addProvinceCity(province, "市辖区", cityCode);
        virtualCity.setAreaType(AreaType.VIRTUALCITY);


    }

    /**
     * 处理省辖市
     *
     * @param province
     * @param city
     */
    private void processProvinceCity(Area province, Area city)
    {
        String cityName = city.getName();
        String cityCode = city.getCode();

        addProvinceCity(province, cityName, cityCode);
    }

    /**
     * 处理省直辖县区
     *
     * @param name
     * @param code
     */
    private void processDirectlyCounty(Area province, String name, String code)
    {
        if (AreaUtils.isDirectlyCounty(province.getCode(), code))
        {
            Area directCounty = new Area();
            directCounty.setName(name);
            directCounty.setCode(code);
            directCounty.setParentCode(province.getCode());
            directCounty.setParentName(province.getName());

            processFullName(directCounty, province);

            province.getChildren().add(directCounty);

            areaModel.addCity(directCounty);

            //设置类型为省直辖县区
            directCounty.setAreaType(AreaType.DIRECTCOUNTY);

            areaModel.getDirectlyCountyMap().put(code,directCounty);
        }
    }

    private Area addProvinceCity(Area province, String cityName, String cityCode)
    {
        Area city = new Area();
        city.setName(cityName);
        city.setCode(cityCode);
        city.setParentCode(province.getCode());
        city.setParentName(province.getName());

        processFullName(city, province);

        province.getChildren().add(city);

        areaModel.addCity(city);

        return city;
    }

    private void addCityCounty(Area city, String name, String code)
    {
        Area county = new Area();
        county.setName(name);
        county.setCode(code);
        county.setParentCode(city.getCode());
        county.setParentName(city.getName());

        processFullName(county, city);

        city.getChildren().add(county);

        areaModel.addCounty(county);
    }

    private void processFullName(Area area, Area parent)
    {
        String parentFullName = parent.getFullName();

        //直辖市的，去除中间层
        if (parent.getAreaType().equals(AreaType.VIRTUALCITY))
        {
            parentFullName = parent.getParentName();
        }

        String fullName = parentFullName + FULLNAME_JION_STR + area.getName();
        area.setFullName(fullName);
    }

    private void log(String msg, Object... params)
    {
        final String format = MessageFormat.format(msg, params);

        System.out.println(format);
    }


}
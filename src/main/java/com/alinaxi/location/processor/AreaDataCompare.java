package com.alinaxi.location.processor;

import com.alinaxi.location.AreaModel;
import com.alinaxi.location.model.Area;
import com.alinaxi.location.util.FileUtils;
import com.csvreader.CsvReader;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * @desc:与的数据对比，输出有变更的
 * @author: xiefapan
 * @date: 2020/2/16  14:42
 */

public class AreaDataCompare
{
    private AreaModel areaModel;

    private String basePath;

    public AreaDataCompare(AreaModel areaModel,String basePath){
        this.areaModel = areaModel;
        this.basePath =basePath;
    }

    public void compare(){
        Map<String,Area> oldAreas = readOldAreas();

        final Map<String, Area> areaMap = areaModel.getAreaMap();

        final MapDifference<String, Area> difference = Maps.difference(oldAreas, areaMap);

        generateCompareResult(difference);
    }

    private void generateCompareResult(MapDifference<String, Area> difference){
        final Map<String, Area> removedAreas = difference.entriesOnlyOnLeft();

        generateCsv(removedAreas.values(),"removed.csv");

        final Map<String, Area> addedAreas = difference.entriesOnlyOnRight();

        generateCsv(addedAreas.values(),"addeded.csv");

        generateChanged(difference);



    }

    private void generateChanged(MapDifference<String, Area> difference){
        final Map<String, MapDifference.ValueDifference<Area>> differenceMap = difference.entriesDiffering();

        StringBuilder builder=new StringBuilder("oldCode,newCode,oldName,newName,oldNote,newNote,oldParentId," +
                                                "newParentId");


        final Collection<MapDifference.ValueDifference<Area>> values = differenceMap.values();
        for(MapDifference.ValueDifference<Area> updatedArea:values){
            final Area oldArea = updatedArea.leftValue();
            final Area newArea = updatedArea.rightValue();

            final String str = generateUpdatedAreaItem(oldArea,newArea);
            builder.append("\n").append(str);
        }


        String path = basePath + "/compare/updated.csv";

        FileUtils.writeToFile(builder.toString(), path);
    }

    private void generateCsv(Collection<Area> areas,String fileName){
        StringBuilder builder=new StringBuilder("code,name,note,parentId");
        for(Area area:areas){
            final String str = generateAreaItem(area);
            builder.append("\n").append(str);
        }
        String path = basePath + "/compare/"+fileName;

        FileUtils.writeToFile(builder.toString(),path);
    }

    public Map<String,Area> readOldAreas(){
        Map<String,Area> map =new TreeMap<>();

        try (InputStream resourceAsStream = new FileInputStream(basePath+"/oldData/area.csv")){
            // 第一参数：读取文件的路径 第二个参数：分隔符（不懂仔细查看引用百度百科的那段话） 第三个参数：字符集
            CsvReader csvReader = new CsvReader(resourceAsStream, ',', Charset.forName("UTF-8"));

            // 如果你的文件没有表头，这行不用执行
            // 这行不要是为了从表头的下一行读，也就是过滤表头
            //            csvReader.readHeaders();

            // 读取每行的内容
            while (csvReader.readRecord()) {
                // 获取内容的两种方式
                // 1. 通过下标获取
                final String code = csvReader.get(0);
                final String name = csvReader.get(1);
                final String fullName = csvReader.get(2);
                final String parentCode = csvReader.get(3);

                final Area area = new Area();
                area.setCode(code);
                area.setName(name);
                area.setFullName(fullName);
                area.setParentCode(parentCode);

                map.put(code,area);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return map;
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

    private String generateUpdatedAreaItem(Area oldArea,Area newArea)
    {
        final StringBuilder builder = new StringBuilder();

        builder.append("\"").append(oldArea.getCode()).append("\",");
        builder.append("\"").append(newArea.getCode()).append("\",");

        builder.append("\"").append(oldArea.getName()).append("\",");
        builder.append("\"").append(newArea.getName()).append("\",");

        builder.append("\"").append(oldArea.getFullName()).append("\",");
        builder.append("\"").append(newArea.getFullName()).append("\",");

        builder.append("\"").append(oldArea.getParentCode()).append("\",");
        builder.append("\"").append(newArea.getParentCode()).append("\"");

        return builder.toString();
    }

}
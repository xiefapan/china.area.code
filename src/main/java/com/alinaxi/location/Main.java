package com.alinaxi.location;

import com.alinaxi.location.generate.AreaCsvGenerator;
import com.alinaxi.location.generate.IGenerator;
import com.alinaxi.location.generate.VantAreaJsGenerator;
import com.alinaxi.location.generate.EncodeVantAreaJsGenerator;
import com.alinaxi.location.processor.AreaDataCompare;

import java.util.ArrayList;
import java.util.List;

;

/**
 * @author: xiefapan
 * @date: 2020/2/14  14:31
 */

public class Main
{

    private static String basePath = "E:\\dev\\personal\\china_area_code\\data";
    private final static AreaModel areaModel = new AreaModel();


    public static void main(String[] args)
    {
        new DataFetch(areaModel).fetch();

        final List<IGenerator> generators = getGenerators();

        for(IGenerator generator: generators)
        {
            generator.generate();
        }

        doCompare();
    }

    private static List<IGenerator> getGenerators()
    {
        List<IGenerator> list =new ArrayList<>();

        list.add(new AreaCsvGenerator(areaModel, basePath));
        list.add(new VantAreaJsGenerator(areaModel, basePath));
        list.add(new EncodeVantAreaJsGenerator(areaModel, basePath));


        return list;
    }

    private static void doCompare(){
        new AreaDataCompare(areaModel,basePath).compare();
    }



}

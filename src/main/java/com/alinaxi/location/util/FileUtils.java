package com.alinaxi.location.util;

import java.io.File;
import java.io.IOException;

/**
 * @desc:TODO
 * @author: xiefapan
 * @date: 2020/2/16  16:37
 */

public class FileUtils
{
    public static void writeToFile(String content,String filePath)
    {
        try
        {
            org.apache.commons.io.FileUtils.writeStringToFile(new File(filePath), content);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


    }
}
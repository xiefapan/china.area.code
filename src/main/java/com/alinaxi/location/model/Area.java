package com.alinaxi.location.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc:区域
 * @author: xiefapan
 * @date: 2020/2/14  14:31
 */

public class Area
{
    private String code;

    private String name;

    private String fullName;

    private String parentCode;

    private String parentName;

    private AreaType areaType;

    private List<Area> children=new ArrayList<>();

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public void setFullName(String fullName)
    {
        this.fullName = fullName;
    }

    public String getParentCode()
    {
        return parentCode;
    }

    public void setParentCode(String parentCode)
    {
        this.parentCode = parentCode;
    }

    public List<Area> getChildren()
    {
        return children;
    }

    public AreaType getAreaType()
    {
        return areaType;
    }

    public void setAreaType(AreaType areaType)
    {
        this.areaType = areaType;
    }

    public String getParentName()
    {
        return parentName;
    }

    public void setParentName(String parentName)
    {
        this.parentName = parentName;
    }

    @Override
    public String toString()
    {
        return "Area{" + "code='" + code + '\'' + ", name='" + name + '\'' + '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Area area = (Area) o;

        if (code != null ? !code.equals(area.code) : area.code != null)
        {
            return false;
        }
        if (name != null ? !name.equals(area.name) : area.name != null)
        {
            return false;
        }
        return parentCode != null ? parentCode.equals(area.parentCode) : area.parentCode == null;
    }

    @Override
    public int hashCode()
    {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parentCode != null ? parentCode.hashCode() : 0);
        return result;
    }
}


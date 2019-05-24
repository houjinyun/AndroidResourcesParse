package com.hjy.resourcesparse.type;

import java.util.List;

public class ResStringPool {

    public ResStringPoolHeader resStringPoolHeader;
    public int[] stringOffsetArr; //字符串偏移数组
    public int[] styleOffsetArr;  //字符串样式偏移数组
    public List<String> resStringList;      //字符串池

    public int getSize() {
        return resStringPoolHeader.header.size;
    }

    public String getString(int index) {
        if (resStringList == null)
            return null;
        if (index < resStringList.size() && index >= 0) {
            return resStringList.get(index);
        }
        return null;
    }

    public int getStringSize() {
        return resStringList != null ? resStringList.size() : 0;
    }

    @Override
    public String toString() {
        return "ResStringPool{" +
                "resStringPoolHeader=" + resStringPoolHeader +
                '}';
    }

    public void printAllResStrings() {
        if (resStringList != null) {
            for (String str : resStringList) {
                System.out.println(str);
            }
        }
    }

}

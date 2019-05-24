package com.hjy.resourcesparse.type;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjy on 2019/4/30.
 */

public class ResTableTypeSpec {

    public ResChunkHeader header;
    public byte id;
    public byte res0;
    public short res1;
    public int entryCount;

    //type spec 数组
    public int[] specArray;
    public List<ResTableType> tableTypeList = new ArrayList<>();

    @Override
    public String toString() {
        return "ResTableTypeSpec{" +
                "header=" + header +
                ", id=" + id +
                ", res0=" + res0 +
                ", res1=" + res1 +
                ", entryCount=" + entryCount +
                ", ResTableType count = " + tableTypeList.size() +
                '}';
    }

    public void printTableTypeInfo() {
        for (ResTableType tableType: tableTypeList) {
            tableType.printDetailInfo();
        }
    }

}

package com.hjy.resourcesparse.type;

import com.hjy.resourcesparse.ParseResources;

public class ResTableMapEntry extends ResTableEntry {

    public ResTableRef parent;
    public int count;

    public ResTableMap[] tableMaps;

    @Override
    public int getSize() {
        return super.getSize() + 8;
    }

    @Override
    public String toString() {
        return "ResTableMapEntry{" +
                "size=" + size +
                ", flags=" + flags +
                ", key=" + key +
                ", parent=" + parent +
                ", count=" + count +
                '}';
    }

    public void printNameAndValue() {
        System.out.println("资源名称：" + ParseResources.resKeyStringPool.getString(key.index));
        System.out.println("资源值：ResTableMapEntry 数据");

    }

}

package com.hjy.resourcesparse.type;

import com.hjy.resourcesparse.ParseResources;

import java.util.ArrayList;
import java.util.List;

public class ResTableType {

    public ResChunkHeader header;
    public byte id;
    public byte flags;
    public short reserved;
    public int entryCount;
    public int entriesStart;
    public ResTableConfig config;

    public List<ResTableEntry> tableEntryList = new ArrayList<>();

    public int getHeaderSize() {
        return header.getHeaderSize();
    }

    @Override
    public String toString() {
        return "ResTableType{" +
                "header=" + header +
                ", id=" + id +
                ", flags=" + flags +
                ", reserved=" + reserved +
                ", entryCount=" + entryCount +
                ", entriesStart=" + entriesStart +
                ", config=" + config +
                '}';
    }

    public void printDetailInfo() {
        System.out.println("type: " + ParseResources.resTypeStringPool.getString(id - 1));
        System.out.println(config);
        System.out.println("tableEntry size = " + tableEntryList.size());
        for (ResTableEntry entry : tableEntryList) {
            entry.printNameAndValue();
        }
    }
}

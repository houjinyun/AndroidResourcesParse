package com.hjy.resourcesparse.type;

public class ResTableType {

    public ResChunkHeader header;

    public byte id;

    public byte flags;

    public short reserved;

    public int entryCount;

    public int entriesStart;

    public ResTableConfig config;

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
}

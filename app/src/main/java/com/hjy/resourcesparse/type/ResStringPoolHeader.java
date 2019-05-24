package com.hjy.resourcesparse.type;

public class ResStringPoolHeader {

    public ResChunkHeader header;

    public int stringCount;

    public int styleCount;

    public int flags;

    public int stringsStart;

    public int stylesStart;

    public int getHeaderSize() {
        return header.headerSize;
    }

    @Override
    public String toString() {
        return "ResStringPoolHeader{" +
                "header=" + header +
                ", stringCount=" + stringCount +
                ", styleCount=" + styleCount +
                ", flags=" + flags +
                ", stringsStart=" + stringsStart +
                ", stylesStart=" + stylesStart +
                '}';
    }
}

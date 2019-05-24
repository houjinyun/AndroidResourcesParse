package com.hjy.resourcesparse.type;

/**
 * Created by hjy on 2019/4/29.
 */

public class ResTableHeader {

    public ResChunkHeader header;
    public int packageCount;

    public int getHeaderSize() {
        return header.headerSize;
    }

    @Override
    public String toString() {
        return "ResTableHeader{" +
                "header=" + header +
                ", packageCount=" + packageCount +
                '}';
    }
}

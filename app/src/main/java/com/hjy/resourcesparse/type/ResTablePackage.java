package com.hjy.resourcesparse.type;

/**
 * Created by hjy on 2019/4/30.
 */

public class ResTablePackage {

    public ResChunkHeader header;
    public int id;

    public short[] name = new short[128];
    public String packageName;

    public int typeStrings;
    public int lastPublicType;
    public int keyStrings;
    public int lastPublicKey;
    public int typeIdOffset;

    public int getHeaderSize() {
        return header.getHeaderSize();
    }

    @Override
    public String toString() {
        return "ResTablePackage{" +
                "header=" + header +
                ", id=" + id +
                ", packageName='" + packageName + '\'' +
                ", typeStrings=" + typeStrings +
                ", lastPublicType=" + lastPublicType +
                ", keyStrings=" + keyStrings +
                ", lastPublicKey=" + lastPublicKey +
                ", typeIdOffset=" + typeIdOffset +
                '}';
    }
}
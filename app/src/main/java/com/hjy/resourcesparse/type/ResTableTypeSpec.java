package com.hjy.resourcesparse.type;

/**
 * Created by hjy on 2019/4/30.
 */

public class ResTableTypeSpec {

    public ResChunkHeader header;
    public byte id;
    public byte res0;
    public short res1;
    public int entryCount;

    @Override
    public String toString() {
        return "ResTableTypeSpec{" +
                "header=" + header +
                ", id=" + id +
                ", res0=" + res0 +
                ", res1=" + res1 +
                ", entryCount=" + entryCount +
                '}';
    }
}

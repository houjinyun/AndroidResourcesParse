package com.hjy.resourcesparse.type;


import com.hjy.resourcesparse.util.ByteUtil;

public class ResTableEntry {

    public static final int FLAG_COMPLEX = 0x0001;
    public static final int FLAG_PUBLIC = 0x0002;
    public static final int FLAG_WEAK = 0x0004;

    public short size;
    public short flags;
    public ResStringPoolRef key;

    public int getSize() {
        return 8;
    }

    @Override
    public java.lang.String toString() {
        return "ResTableEntry{" +
                "size=" + size +
                ", flags=" + ByteUtil.toHex(flags) +
                ", key=" + key +
                '}';
    }
}
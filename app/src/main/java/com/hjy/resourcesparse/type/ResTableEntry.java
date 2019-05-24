package com.hjy.resourcesparse.type;


import com.hjy.resourcesparse.ParseResources;
import com.hjy.resourcesparse.util.ByteUtil;

public class ResTableEntry {

    public static final int FLAG_COMPLEX = 0x0001;
    public static final int FLAG_PUBLIC = 0x0002;
    public static final int FLAG_WEAK = 0x0004;

    public short size;
    public short flags;
    public ResStringPoolRef key;

    public ResValue value;

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

    public void printNameAndValue() {
        System.out.println("资源名称：" + ParseResources.resKeyStringPool.getString(key.index));
        System.out.println("资源值：" + value.getDataStr());
    }


}
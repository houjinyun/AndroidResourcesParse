package com.hjy.resourcesparse.type;


import com.hjy.resourcesparse.util.ByteUtil;

public class ResValue {

    public short size;
    public byte res0;
    public byte dataType;
    public int data;

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "ResValue{" +
                "size=" + ByteUtil.toHex(size) +
                ", res0=" + res0 +
                ", dataType=" + ByteUtil.toHex(dataType) +
                ", data=" + data +
                '}';
    }
}

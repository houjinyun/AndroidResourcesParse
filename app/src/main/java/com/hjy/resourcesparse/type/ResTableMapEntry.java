package com.hjy.resourcesparse.type;

public class ResTableMapEntry extends ResTableEntry {

    public ResTableRef parent;
    public int count;

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
}

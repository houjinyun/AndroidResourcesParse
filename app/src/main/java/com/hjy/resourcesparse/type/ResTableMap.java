package com.hjy.resourcesparse.type;

public class ResTableMap {

    public ResTableRef name;
    public ResValue value;

    public int getSize() {
        return 4 + value.getSize();
    }

    @Override
    public String toString() {
        return "ResTableMap{" +
                "name=" + name +
                ", value=" + value +
                '}';
    }
}

package com.hjy.resourcesparse;

import android.content.Context;


import com.hjy.resourcesparse.type.ResChunkHeader;
import com.hjy.resourcesparse.type.ResStringPool;
import com.hjy.resourcesparse.type.ResStringPoolHeader;
import com.hjy.resourcesparse.type.ResStringPoolRef;
import com.hjy.resourcesparse.type.ResTableConfig;
import com.hjy.resourcesparse.type.ResTableEntry;
import com.hjy.resourcesparse.type.ResTableHeader;
import com.hjy.resourcesparse.type.ResTableMap;
import com.hjy.resourcesparse.type.ResTableMapEntry;
import com.hjy.resourcesparse.type.ResTablePackage;
import com.hjy.resourcesparse.type.ResTableRef;
import com.hjy.resourcesparse.type.ResTableType;
import com.hjy.resourcesparse.type.ResTableTypeSpec;
import com.hjy.resourcesparse.type.ResValue;
import com.hjy.resourcesparse.util.ByteUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParseResources {

    private byte[] srcByte = null;

    private ResTableHeader resTableHeader;
    private ResStringPool resStringPool;        //全局字符串
    private ResTablePackage resTablePackage;    //资源包信息
    private ResStringPool resTypeStringPool;    //资源类型字符串
    private ResStringPool resKeyStringPool;     //资源名称字符串

    public void test(Context context) {
        System.out.println("-------开始读取文件----");
        try {
            InputStream is = context.getResources().getAssets().open("resources.arsc");
            int len = is.available();
            srcByte = new byte[len];
            is.read(srcByte);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (srcByte == null) {
            System.out.println("------读取失败------");
            return;
        }

        System.out.println("文件大小为：" + srcByte.length + "字节");
        System.out.println("开始解析文件-----");

        resTableHeader = parseResTableHeader();
        System.out.println(resTableHeader);

        //解析全局字符串池
        resStringPool = parseResStringPool(resTableHeader.getHeaderSize());
//        System.out.println(resStringPool);

        System.out.println("   ");
        System.out.println("资源包信息");
        resTablePackage = parseResTablePackage();
        System.out.println(resTablePackage);

        System.out.println("   ");
        System.out.println("资源类型字符串");
        parseResTypeString();
//        System.out.println(resTypeStringPool);

        System.out.println("    ");
        System.out.println("资源名称字符串");
        parseResKeyString();
//        System.out.println(resKeyStringPool);

        //开始解析 type 信息
        int offset = resTableHeader.getHeaderSize() + resStringPool.getSize() + resTablePackage.keyStrings
                + resKeyStringPool.getSize();

        while (offset < resTableHeader.header.size) {
            offset = parseResTypeInfo(offset);
        }
    }

    /**
     * 解析 ResChunk_header 数据
     *
     * @param src
     * @param offset
     * @return
     */
    private ResChunkHeader parseResChunkHeader(byte[] src, int offset) {
        byte[] typeByte = ByteUtil.copyByte(src, offset, 2);
        byte[] headerSizeByte = ByteUtil.copyByte(src, offset + 2, 2);
        byte[] sizeByte = ByteUtil.copyByte(src, offset + 4, 4);
        ResChunkHeader header = new ResChunkHeader();
        header.type = ByteUtil.byte2short(typeByte);
        header.headerSize = ByteUtil.byte2short(headerSizeByte);
        header.size = ByteUtil.byte2int(sizeByte);
        return header;
    }

    /**
     * 解析 ResTable_header 数据
     */
    private ResTableHeader parseResTableHeader() {
        ResTableHeader resTableHeader = new ResTableHeader();
        ResChunkHeader header = parseResChunkHeader(srcByte, 0);
        byte[] packageByte = ByteUtil.copyByte(srcByte, header.getHeaderSize(), 4);
        int packageCount = ByteUtil.byte2int(packageByte);
        resTableHeader.header = header;
        resTableHeader.packageCount = packageCount;
        return resTableHeader;
    }

    /**
     * 解析 ResStringPool_header
     *
     * @return
     */
    private ResStringPoolHeader parseResStringPoolHeader(int offset) {
        ResStringPoolHeader resStringPoolHeader = new ResStringPoolHeader();
        ResChunkHeader header = parseResChunkHeader(srcByte, offset);
        offset = offset + header.getHeaderSize();
        byte[] stringCountByte = ByteUtil.copyByte(srcByte, offset, 4);
        offset += 4;
        byte[] styleCountByte = ByteUtil.copyByte(srcByte, offset, 4);
        offset += 4;
        byte[] flagsByte = ByteUtil.copyByte(srcByte, offset, 4);
        offset += 4;
        byte[] stringsStartByte = ByteUtil.copyByte(srcByte, offset, 4);
        offset += 4;
        byte[] stylesStartByte = ByteUtil.copyByte(srcByte, offset, 4);

        resStringPoolHeader.header = header;
        resStringPoolHeader.stringCount = ByteUtil.byte2int(stringCountByte);
        resStringPoolHeader.styleCount = ByteUtil.byte2int(styleCountByte);
        resStringPoolHeader.flags = ByteUtil.byte2int(flagsByte);
        resStringPoolHeader.stringsStart = ByteUtil.byte2int(stringsStartByte);
        resStringPoolHeader.stylesStart = ByteUtil.byte2int(stylesStartByte);
        return resStringPoolHeader;
    }

    /**
     * 解析字符串
     */
    private ResStringPool parseResStringPool(int offset) {
        ResStringPool stringPool = new ResStringPool();

        ResStringPoolHeader stringPoolHeader = parseResStringPoolHeader(offset);
        System.out.println(stringPoolHeader);

        byte[] stringPoolByte = ByteUtil.copyByte(srcByte, offset, stringPoolHeader.header.size);

        int arrOffset = stringPoolHeader.getHeaderSize();
        int[] stringOffsetArr = new int[stringPoolHeader.stringCount];
        for (int i = 0; i < stringPoolHeader.stringCount; i++) {
            byte[] data = ByteUtil.copyByte(stringPoolByte, arrOffset, 4);
            stringOffsetArr[i] = ByteUtil.byte2int(data);
            arrOffset += 4;
        }
        int[] styleOffsetArr = new int[stringPoolHeader.styleCount];
        for (int i = 0; i < stringPoolHeader.styleCount; i++) {
            byte[] data = ByteUtil.copyByte(stringPoolByte, arrOffset, 4);
            styleOffsetArr[i] = ByteUtil.byte2int(data);
            arrOffset += 4;
        }

        List<String> resStringList = new ArrayList<>();
        int flags = stringPoolHeader.flags;
        if ((flags & 0x100) != 0) {
            System.out.println("字符串采用UTF-8编码");
            for (int i = 0; i < stringOffsetArr.length; i++) {
                int stringsStart = stringPoolHeader.stringsStart + stringOffsetArr[i];
                int len;
                int c = 1;
                if ((stringPoolByte[stringsStart + 1] & 0x80) == 0) {
                    len = stringPoolByte[stringsStart + c];
                } else {
                    int realSize = stringPoolByte[stringsStart + c];
                    int j = 0;
                    while ((realSize & (0x80 << j)) != 0) {
                        c++;
                        byte tmp = stringPoolByte[stringsStart + c];
                        realSize = ((realSize & 0x7f) << 8) | (tmp & 0xff);
                        j += 4;
                    }
                    len = realSize;
                    //                  System.out.println("len = " + len + "   test len = " + (stringOffsetArr[i + 1]- stringOffsetArr[i]));

                    //TODO 字符串长度太大时，计算还是有问题
                }

                byte[] stringByte = ByteUtil.copyByte(stringPoolByte, stringsStart + c + 1, len);
                String data = null;
                if (stringByte == null || stringByte.length == 0) {
                    data = null;
//                    Log.d("TEST", i + ": 这是一个空字符串");
                } else {
                    try {
                        data = new String(stringByte, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
//                    Log.d("TEST", i + ": " + data);
                }
                resStringList.add(data);
            }
        } else {
            System.out.println("字符串采用UTF-16编码");
            //TODO ....
        }

        stringPool.resStringPoolHeader = stringPoolHeader;
        stringPool.stringOffsetArr = stringOffsetArr;
        stringPool.styleOffsetArr = styleOffsetArr;
        stringPool.resStringList = resStringList;

        return stringPool;
    }

    /**
     * 解析包信息
     *
     * @return
     */
    private ResTablePackage parseResTablePackage() {
        ResTablePackage resTablePackage = new ResTablePackage();
        int offset = resTableHeader.getHeaderSize() + resStringPool.getSize();
        ResChunkHeader resChunkHeader = parseResChunkHeader(srcByte, offset);

        offset += resChunkHeader.getHeaderSize();
        int id = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));

        offset += 4;
        byte[] nameByte = ByteUtil.copyByte(srcByte, offset, 128 * 2);
        short[] nameShort = new short[128];
        for (int i = 0; i < 128; i++) {
            nameShort[i] = ByteUtil.byte2short(ByteUtil.copyByte(nameByte, i * 2, 2));
        }
        resTablePackage.name = nameShort;
        nameByte = new byte[128];
        int len = 0;
        for (int i = 0; i < nameShort.length; i++) {
            len = i;
            if (nameShort[i] == 0)
                continue;
            byte b = (byte) nameShort[i];
            nameByte[i] = b;
        }
        nameByte = Arrays.copyOfRange(nameByte, 0, len);
        String packageName = new String(nameByte);

        offset += 256;
        int typeStrings = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));

        offset += 4;
        int lastPublicType = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));

        offset += 4;
        int keyStrings = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));

        offset += 4;
        int lastPublicKey = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));

        offset += 4;
        int typeIdOffset = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));

        resTablePackage.header = resChunkHeader;
        resTablePackage.id = id;
        resTablePackage.packageName = packageName;
        resTablePackage.typeStrings = typeStrings;
        resTablePackage.lastPublicType = lastPublicType;
        resTablePackage.keyStrings = keyStrings;
        resTablePackage.lastPublicKey = lastPublicKey;
        resTablePackage.typeIdOffset = typeIdOffset;
        return resTablePackage;
    }

    /**
     * 解析资源类型字符串
     */
    private void parseResTypeString() {
        int offset = resTableHeader.getHeaderSize() + resStringPool.getSize() + resTablePackage.typeStrings;
        resTypeStringPool = parseResStringPool(offset);
    }

    /**
     * 解析资源名称字符串
     */
    private void parseResKeyString() {
        int offset = resTableHeader.getHeaderSize() + resStringPool.getSize() + resTablePackage.keyStrings;
        resKeyStringPool = parseResStringPool(offset);
    }

    /**
     * 解析类型规范
     */
    private int parseResTypeInfo(int offset) {
        System.out.println("     ");
        System.out.println("     ");
        System.out.println("开始解析 ResTable_typeSpec========");
        ResTableTypeSpec typeSpec = new ResTableTypeSpec();
        typeSpec.header = parseResChunkHeader(srcByte, offset);
        offset += typeSpec.header.getHeaderSize();
        typeSpec.id = srcByte[offset];
        offset++;
        typeSpec.res0 = srcByte[offset];
        offset++;
        typeSpec.res1 = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset, 2));
        offset += 2;
        byte[] countByte = ByteUtil.copyByte(srcByte, offset, 4);
        typeSpec.entryCount = ByteUtil.byte2int(countByte);
        System.out.println(typeSpec);

        //紧跟着的是 一个 type spec 数组
        offset += 4;
        int[] specArr = new int[typeSpec.entryCount];
        for (int i = 0; i < specArr.length; i++) {
            byte[] data = ByteUtil.copyByte(srcByte, offset, 4);
            offset += 4;
            specArr[i] = ByteUtil.byte2int(data);
        }

        //后面接着是若干个 ResTable_type 类型数据
        if (offset >= resTableHeader.header.size) {
            System.out.println("====解析结束111=====");
            return offset;
        }

        ResChunkHeader chunkHeader = parseResChunkHeader(srcByte, offset);
        System.out.println(chunkHeader);

        int nextChunkOffset = offset;
        while (chunkHeader.type == 0x0201) {
            //接着是 ResTable_type 数据

            System.out.println("     ");
            System.out.println("开始解析 ResTable_type========");
            offset = nextChunkOffset;
            int typeOriginalOffset = offset;

            ResTableType tableType = new ResTableType();
            tableType.header = parseResChunkHeader(srcByte, offset);
            nextChunkOffset += tableType.header.size;
            System.out.println("next chunk offset = " + nextChunkOffset);

            offset += tableType.header.getHeaderSize();
            tableType.id = srcByte[offset++];
            tableType.flags = srcByte[offset++];
            tableType.reserved = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset, 2));
            offset += 2;
            tableType.entryCount = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));
            offset += 4;
            tableType.entriesStart = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));
            offset += 4;
            ResTableConfig config = new ResTableConfig();
            config.size = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));
            config.mcc = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 4, 2));
            config.mnc = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 6, 2));
            config.imsi = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 4, 4));
            config.language = ByteUtil.copyByte(srcByte, offset + 8, 2);
            config.country = ByteUtil.copyByte(srcByte, offset + 10, 2);
            config.locale = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 8, 4));
            config.orientation = srcByte[offset + 12];
            config.touchscreen = srcByte[offset + 13];
            config.density = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 14, 2));
            config.screenType = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 12, 4));
            config.keyboard = srcByte[offset + 16];
            config.navigation = srcByte[offset + 17];
            config.inputFlags = srcByte[offset + 18];
            config.inputPad0 = srcByte[offset + 19];
            config.input = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 16, 4));
            config.screenWidth = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 20, 2));
            config.screenHeight = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 22, 2));
            config.screenSize = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 20, 4));
            config.sdkVersion = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 24, 2));
            config.minorVersion = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 26, 2));
            config.version = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 24, 4));
            config.screenLayout = srcByte[offset + 28];
            config.uiMode = srcByte[offset + 29];
            config.smallestScreenWidthDp = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 30, 2));
            config.screenConfig = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 28, 4));
            config.screenWidthDp = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 32, 2));
            config.screenHeightDp = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 34, 2));
            config.screenSizeDp = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 32, 4));
            config.localeScript = ByteUtil.copyByte(srcByte, offset + 36, 4);
            config.localeVariant = ByteUtil.copyByte(srcByte, offset + 40, 8);
            config.screenLayout2 = srcByte[offset + 48];
            config.colorMode = srcByte[offset + 49];
            config.screenConfigPad2 = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 50, 2));
            config.screenConfig2 = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 48, 4));
            tableType.config = config;

            System.out.println(tableType);
            System.out.println("资源类型：" + resTypeStringPool.getString(tableType.id - 1));

            //紧跟着 entryCount 个 int型偏移数组
            int[] entryOffsetArr = new int[tableType.entryCount];
            offset = typeOriginalOffset + tableType.header.headerSize;
            for (int i = 0; i < entryOffsetArr.length; i++) {
                entryOffsetArr[i] = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));
//                System.out.println("entry offset: " + ByteUtil.toHex(entryOffsetArr[i]) + ", " + entryOffsetArr[i]);
                offset += 4;
            }

            offset = typeOriginalOffset + tableType.entriesStart;
            //接着是 ResTable_entry;
            int entriesStart = offset;
            ResTableEntry[] tableEntryArr = new ResTableEntry[tableType.entryCount];
            for (int i = 0; i < tableType.entryCount; i++) {
                //因为偏移数组中元素数量可能比其后面的ResTable_entry数量多，对于没有对应ResTable_entry结构的偏移数组中元素，其值为0xffffffff.
                //这点尤为关键，否则极有可能解析错误
                if (entryOffsetArr[i] == -1) {
//                    System.out.println("没有对应的 table_entry");
                    continue;
                }
                offset = entriesStart + entryOffsetArr[i];
                ResTableEntry entry = parseResTableEntry(ByteUtil.copyByte(srcByte, offset, 8));
                System.out.println(entry);
                System.out.println("资源名称：" + resKeyStringPool.getString(entry.key.index));

                if ((entry.flags & 0x01) != 0) {
                    //ResTable_map_entry结构
                    System.out.println("map entry structure=============");
                    ResTableMapEntry mapEntry = parseResTableMapEntry(offset);
                    System.out.println(mapEntry);

                    ResTableMap[] mapArr = parseResTableMap(offset + mapEntry.getSize(), mapEntry.count);

                } else {
                    ResValue value = parseResValue(ByteUtil.copyByte(srcByte, offset + entry.getSize(), 8));
                //    System.out.println(value);
                    //TODO 打印出值来
                    // System.out.println(resStringPool.getString(value.data));
                }
            }

            if (nextChunkOffset >= resTableHeader.header.size) {
                System.out.println("====解析结束2222=====");
                return nextChunkOffset;
            }
            chunkHeader = parseResChunkHeader(srcByte, nextChunkOffset);
            System.out.println(chunkHeader);
        }

        System.out.println("====下一个 data chunk 不是 ResTable_type =====");
        System.out.println("下一个chunk type = " + ByteUtil.toHex(chunkHeader.type));
        return nextChunkOffset;
    }

    private ResTableEntry parseResTableEntry(byte[] data) {
        ResTableEntry entry = new ResTableEntry();
        entry.size = ByteUtil.byte2short(ByteUtil.copyByte(data, 0, 2));
        entry.flags = ByteUtil.byte2short(ByteUtil.copyByte(data, 2, 2));
        ResStringPoolRef ref = new ResStringPoolRef();
        ref.index = ByteUtil.byte2int(ByteUtil.copyByte(data, 4, 4));
        entry.key = ref;
        return entry;
    }

    private ResTableMapEntry parseResTableMapEntry(int offset) {
        ResTableMapEntry entry = new ResTableMapEntry();
        entry.size = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset, 2));
        entry.flags = ByteUtil.byte2short(ByteUtil.copyByte(srcByte, offset + 2, 2));
        ResStringPoolRef ref = new ResStringPoolRef();
        ref.index = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 4, 4));
        entry.key = ref;

        ResTableRef resTableRef = new ResTableRef();
        resTableRef.ident = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 8, 4));
        entry.parent = resTableRef;

        entry.count = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset + 12, 4));
        return entry;
    }

    private ResTableMap[] parseResTableMap(int offset, int count) {
        ResTableMap[] mapArr = new ResTableMap[count];
        for (int i = 0; i < mapArr.length; i++) {
            ResTableMap map = new ResTableMap();

            ResTableRef name = new ResTableRef();
            name.ident = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));
            map.name = name;
            offset += 4;
            ResValue value = parseResValue(ByteUtil.copyByte(srcByte, offset, 8));
            map.value = value;
            offset += 8;

            mapArr[i] = map;
        }
        return mapArr;
    }

    private ResValue parseResValue(byte[] data) {
        ResValue value = new ResValue();
        value.size = ByteUtil.byte2short(ByteUtil.copyByte(data, 0, 2));
        value.res0 = data[2];
        value.dataType = data[3];
        value.data = ByteUtil.byte2int(ByteUtil.copyByte(data, 4, 4));
        return value;
    }

}
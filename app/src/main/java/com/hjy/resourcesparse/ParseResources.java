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
import java.util.List;

public class ParseResources {

    private byte[] srcByte = null;

    private ResTableHeader resTableHeader;      //资源表头
    public static ResStringPool resStringPool;        //全局字符串
    private ResTablePackage resTablePackage;    //资源包信息
    public static ResStringPool resTypeStringPool;    //资源类型字符串
    public static ResStringPool resKeyStringPool;     //资源名称字符串

    private List<ResTableTypeSpec> typeSpecList = new ArrayList<>();


    public void test(Context context) {
        System.out.println("开始读取文件------------");
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
            System.out.println("读取失败------------");
            return;
        }

        System.out.println("资源表文件大小为：" + srcByte.length + "字节");
        System.out.println("开始解析文件------------");

        System.out.println("解析ResTable_header--------");
        resTableHeader = parseResTableHeader();
        System.out.println(resTableHeader);

        System.out.println("解析全局资源字符串--------");
        resStringPool = parseResStringPool(resTableHeader.getHeaderSize());
        System.out.println(resStringPool);
        //打印出所有字符串
        resStringPool.printAllResStrings();

        System.out.println("解析包信息ResTable_package--------");
        resTablePackage = parseResTablePackage();
        System.out.println(resTablePackage);

        System.out.println("解析资源类型字符串--------");
        parseResTypeString();
        System.out.println(resTypeStringPool);
        System.out.println("资源类型字符串一共有：" + resTypeStringPool.getStringSize());
        //打印出所有的资源类型字符串
        resTypeStringPool.printAllResStrings();

        System.out.println("解析资源名称字符串--------");
        parseResKeyString();
        System.out.println(resKeyStringPool);
        System.out.println("资源名称字符串一共有：" + resKeyStringPool.getStringSize());
        //打印出所有资源名称字符串
//        resKeyStringPool.printAllResStrings();


        System.out.println("解析ResTable_typeSpec信息--------");
        //开始解析 type 信息
        int offset = resTableHeader.getHeaderSize() + resStringPool.getSize() + resTablePackage.keyStrings
                + resKeyStringPool.getSize();
        int typeSpecCount = 0;
        while (offset < resTableHeader.header.size) {
            System.out.println("开始解析第" + (++typeSpecCount) + "个 ResTable_typeSpec 数据--------");
            offset = parseResTypeInfo(offset);
        }

        System.out.println("ResTable_typeSpec数据一共有 " + typeSpecList.size() + " 个");
        for (ResTableTypeSpec typeSpec : typeSpecList) {
            System.out.println("==================");
            System.out.println("=                =");
            System.out.println(typeSpec);
            String typeName = resTypeStringPool.getString(typeSpec.id - 1);
            System.out.println("类型名称：" + typeName);

            //看看 mipmap 类型
            if ("mipmap".equals(typeName)) {
                typeSpec.printTableTypeInfo();
            }
        }

        System.out.println("文件解析完毕---------------");
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
            for (int i = 0; i < stringOffsetArr.length; i++) {
                int stringsStart = stringPoolHeader.stringsStart + stringOffsetArr[i];

                int val = stringPoolByte[stringsStart];
                int len;
                if ((val & 0x80) != 0) {
                    stringsStart += 2;
                } else {
                    stringsStart += 1;
                }
                val = stringPoolByte[stringsStart];
                stringsStart += 1;
                if ((val & 0x80) != 0) {
                    int low = stringPoolByte[stringsStart] & 0xff;
                    len = ((val & 0x7f) << 8) + low;
                    stringsStart += 1;
                } else {
                    len = val;
                }

                byte[] stringByte = ByteUtil.copyByte(stringPoolByte, stringsStart, len);
                String data = null;
                if (stringByte == null || stringByte.length == 0) {
                    data = null;
                } else {
                    data = new String(stringByte);
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

        //开始解析出包名
        //固定长度为 128 的 short 数组，不足的都补 0
        List<Byte> nameByteList = new ArrayList<>();
        for (byte b : nameByte) {
            if (b > 0)
                nameByteList.add(b);
        }
        nameByte = new byte[nameByteList.size()];
        for (int i = 0; i < nameByteList.size(); i++) {
            nameByte[i] = nameByteList.get(i);
        }
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
        ResTableTypeSpec typeSpec = new ResTableTypeSpec();
        typeSpecList.add(typeSpec);

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

        //紧跟着的是 一个 type spec 数组
        offset += 4;
        int[] specArr = new int[typeSpec.entryCount];
        for (int i = 0; i < specArr.length; i++) {
            byte[] data = ByteUtil.copyByte(srcByte, offset, 4);
            offset += 4;
            specArr[i] = ByteUtil.byte2int(data);
        }
        typeSpec.specArray = specArr;

        //后面接着是若干个 ResTable_type 类型数据
        if (offset >= resTableHeader.header.size) {
            return offset;
        }

        ResChunkHeader chunkHeader = parseResChunkHeader(srcByte, offset);

        int nextChunkOffset = offset;
        while (chunkHeader.type == 0x0201) {
            //接着是 ResTable_type 数据
            offset = nextChunkOffset;
            int typeOriginalOffset = offset;

            ResTableType tableType = new ResTableType();
            typeSpec.tableTypeList.add(tableType);

            tableType.header = parseResChunkHeader(srcByte, offset);
            nextChunkOffset += tableType.header.size;

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

            //紧跟着 entryCount 个 int型偏移数组
            int[] entryOffsetArr = new int[tableType.entryCount];
            offset = typeOriginalOffset + tableType.header.headerSize;
            for (int i = 0; i < entryOffsetArr.length; i++) {
                entryOffsetArr[i] = ByteUtil.byte2int(ByteUtil.copyByte(srcByte, offset, 4));
                offset += 4;
            }

            offset = typeOriginalOffset + tableType.entriesStart;
            //接着是 ResTable_entry;
            int entriesStart = offset;
            for (int i = 0; i < tableType.entryCount; i++) {
                //因为偏移数组中元素数量可能比其后面的ResTable_entry数量多，对于没有对应ResTable_entry结构的偏移数组中元素，其值为0xffffffff.
                //这点尤为关键，否则极有可能解析错误
                if (entryOffsetArr[i] == -1) {
//                    System.out.println("没有对应的 table_entry");
                    continue;
                }
                offset = entriesStart + entryOffsetArr[i];
                ResTableEntry entry = parseResTableEntry(ByteUtil.copyByte(srcByte, offset, 8));

                if ((entry.flags & 0x01) != 0) {
                    //ResTable_map_entry结构
                    ResTableMapEntry mapEntry = parseResTableMapEntry(offset);
                    ResTableMap[] mapArr = parseResTableMap(offset + mapEntry.getSize(), mapEntry.count);
                    mapEntry.tableMaps = mapArr;
                    tableType.tableEntryList.add(mapEntry);
                } else {
                    ResValue value = parseResValue(ByteUtil.copyByte(srcByte, offset + entry.getSize(), 8));
                    entry.value = value;
                    tableType.tableEntryList.add(entry);
                }
            }

            if (nextChunkOffset >= resTableHeader.header.size) {
                //解析结束
                return nextChunkOffset;
            }
            chunkHeader = parseResChunkHeader(srcByte, nextChunkOffset);
        }

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
package com.hjy.resourcesparse.type;

import com.hjy.resourcesparse.util.ByteUtil;

/**
 * Created by hjy on 2019/4/29.
 */

public class ResChunkHeader {

 /*   enum {
        RES_NULL_TYPE               = 0x0000,
        RES_STRING_POOL_TYPE        = 0x0001,
        RES_TABLE_TYPE              = 0x0002,
        RES_XML_TYPE                = 0x0003,

        // Chunk types in RES_XML_TYPE
        RES_XML_FIRST_CHUNK_TYPE    = 0x0100,
        RES_XML_START_NAMESPACE_TYPE= 0x0100,
        RES_XML_END_NAMESPACE_TYPE  = 0x0101,
        RES_XML_START_ELEMENT_TYPE  = 0x0102,
        RES_XML_END_ELEMENT_TYPE    = 0x0103,
        RES_XML_CDATA_TYPE          = 0x0104,
        RES_XML_LAST_CHUNK_TYPE     = 0x017f,
        // This contains a uint32_t array mapping strings in the string
        // pool back to resource identifiers.  It is optional.
        RES_XML_RESOURCE_MAP_TYPE   = 0x0180,

        // Chunk types in RES_TABLE_TYPE
        RES_TABLE_PACKAGE_TYPE      = 0x0200,
        RES_TABLE_TYPE_TYPE         = 0x0201,
        RES_TABLE_TYPE_SPEC_TYPE    = 0x0202,
        RES_TABLE_LIBRARY_TYPE      = 0x0203
    };*/

/*    struct ResChunk_header
    {
        // Type identifier for this chunk.  The meaning of this value depends
        // on the containing chunk.
        uint16_t type;

        // Size of the chunk header (in bytes).  Adding this value to
        // the address of the chunk allows you to find its associated data
        // (if any).
        uint16_t headerSize;

        // Total size of this chunk (in bytes).  This is the chunkSize plus
        // the size of any data associated with the chunk.  Adding this value
        // to the chunk allows you to completely skip its contents (including
        // any child chunks).  If this value is the same as chunkSize, there is
        // no data associated with the chunk.
        uint32_t size;
    };*/

    public short type;
    public short headerSize;
    public int size;

    public int getHeaderSize() {
        return 2 + 2 + 4;
    }

    @Override
    public String toString() {
        return "ResChunkHeader{" +
                "type=" + ByteUtil.toHex(type) +
                ", headerSize=" + headerSize +
                ", size=" + size +
                '}';
    }

    public String getType(short type) {
        switch (type) {
            case 0x0000:
                return "RES_NULL_TYPE";
            case 0x0001:
                return "RES_STRING_POOL_TYPE";
            case 0x0002:
                return "RES_TABLE_TYPE";
            case 0x0003:
                return "RES_XML_TYPE";

            case 0x0200:
                return "RES_TABLE_PACKAGE_TYPE";
            case 0x0201:
                return "RES_TABLE_TYPE_TYPE";
            case 0x0202:
                return "RES_TABLE_TYPE_SPEC_TYPE";
            case 0x0203:
                return "RES_TABLE_LIBRARY_TYPE";

        }
        return ByteUtil.toHex(type);
    }

}

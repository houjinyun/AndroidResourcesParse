package com.hjy.resourcesparse.type;

import java.util.Arrays;

public class ResTableConfig {

    public int size;

    //运营商信息i
/*    union {
        struct {
            // Mobile country code (from SIM).  0 means "any".
            uint16_t mcc;
            // Mobile network code (from SIM).  0 means "any".
            uint16_t mnc;
        };
        uint32_t imsi;
    };*/
    public short mcc;
    public short mnc;
    public int imsi;

    //本地化
    /*union {
        struct {
            char language[2];
            char country[2];
        };
        uint32_t locale;
    };*/
    public byte[] language = new byte[2];
    public byte[] country = new byte[2];
    public int locale;

    //屏幕属性
/*    union {
        struct {
            uint8_t orientation;
            uint8_t touchscreen;
            uint16_t density;
        };
        uint32_t screenType;
    };*/
    public byte orientation;
    public byte touchscreen;
    public short density;
    public int screenType;

    //输入属性
/*    union {
        struct {
            uint8_t keyboard;
            uint8_t navigation;
            uint8_t inputFlags;
            uint8_t inputPad0;
        };
        uint32_t input;
    };*/
    public byte keyboard;
    public byte navigation;
    public byte inputFlags;
    public byte inputPad0;
    public int input;

/*    union {
        struct {
            uint16_t screenWidth;
            uint16_t screenHeight;
        };
        uint32_t screenSize;
    };*/
    public short screenWidth;
    public short screenHeight;
    public int screenSize;

/*    union {
        struct {
            uint16_t sdkVersion;
            // For now minorVersion must always be 0!!!  Its meaning
            // is currently undefined.
            uint16_t minorVersion;
        };
        uint32_t version;
    };*/
    public short sdkVersion;
    public short minorVersion;
    public int version;

  /*  union {
        struct {
            uint8_t screenLayout;
            uint8_t uiMode;
            uint16_t smallestScreenWidthDp;
        };
        uint32_t screenConfig;
    };*/
    public byte screenLayout;
    public byte uiMode;
    public short smallestScreenWidthDp;
    public int screenConfig;

/*    union {
        struct {
            uint16_t screenWidthDp;
            uint16_t screenHeightDp;
        };
        uint32_t screenSizeDp;
    };*/
    public short screenWidthDp;
    public short screenHeightDp;
    public int screenSizeDp;

    public byte[] localeScript = new byte[4];
    public byte[] localeVariant = new byte[8];

/*    struct {
        uint8_t screenLayout2;      // Contains round/notround qualifier.
        uint8_t colorMode;          // Wide-gamut, HDR, etc.
        uint16_t screenConfigPad2;  // Reserved padding.
    };
    uint32_t screenConfig2;*/

    public byte screenLayout2;
    public byte colorMode;
    public short screenConfigPad2;
    public int screenConfig2;

    @Override
    public String toString() {
        return "ResTableConfig{" +
                "size=" + size +
                ", mcc=" + mcc +
                ", mnc=" + mnc +
                ", imsi=" + imsi +
                ", language=" + Arrays.toString(language) +
                ", country=" + Arrays.toString(country) +
                ", locale=" + locale +
                ", orientation=" + orientation +
                ", touchscreen=" + touchscreen +
                ", density=" + density +
                ", screenType=" + screenType +
                ", keyboard=" + keyboard +
                ", navigation=" + navigation +
                ", inputFlags=" + inputFlags +
                ", inputPad0=" + inputPad0 +
                ", input=" + input +
                ", screenWidth=" + screenWidth +
                ", screenHeight=" + screenHeight +
                ", screenSize=" + screenSize +
                ", sdkVersion=" + sdkVersion +
                ", minorVersion=" + minorVersion +
                ", version=" + version +
                ", screenLayout=" + screenLayout +
                ", uiMode=" + uiMode +
                ", smallestScreenWidthDp=" + smallestScreenWidthDp +
                ", screenConfig=" + screenConfig +
                ", screenWidthDp=" + screenWidthDp +
                ", screenHeightDp=" + screenHeightDp +
                ", screenSizeDp=" + screenSizeDp +
                ", localeScript=" + Arrays.toString(localeScript) +
                ", localeVariant=" + Arrays.toString(localeVariant) +
                ", screenLayout2=" + screenLayout2 +
                ", colorMode=" + colorMode +
                ", screenConfigPad2=" + screenConfigPad2 +
                ", screenConfig2=" + screenConfig2 +
                '}';
    }
}

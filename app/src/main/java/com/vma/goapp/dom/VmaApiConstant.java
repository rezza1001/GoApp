/*
    VMA Api에서 사용하는 상수를 정의합니다.
 */
package com.vma.goapp.dom;

public class VmaApiConstant {
    public static final int VMA_TRUE        = 1;
    public static final int VMA_FALSE       = 0;
    public static final int VMA_VALUE_ZERO  = 0;
    public static final int VMA_FAIL        = 2;        //  Comm Fail


    public static final String SAVE_ACT = "action";
    public static final int SAVE_DELETE = 0;         //  0:Delete 1:Insert
    public static final int SAVE_INSERT = 1;
    public static final int SAVE_UPDATE = 2;

    public static final String VMA_MESSAGE_TYPE = "msgtype";             //  Message Type
    public static final String VMA_MESSAGE_DATA = "data";                //  Message(String)

    //  Radio RF Fields
    public static final String RF_ITEM_CMD   = "command";               //  packet command
    public static final String RF_ITEM_ACK   = "ack";                   //
    public static final String RF_ITEM_VID   = "vid";                   //  modem id
    public static final String RF_ITEM_GSTAT   = "gpsstatus";           //  gps status
    public static final String RF_ITEM_DEPAT   = "depature";
    public static final String RF_ITEM_BATT   = "battery";
    public static final String RF_ITEM_SOS   = "sos";
    public static final String RF_ITEM_SN   = "SN";
    public static final String RF_ITEM_EW   = "EW";
    public static final String RF_ITEM_ARRIVE   = "arrive";
    public static final String RF_ITEM_PORT   = "port";
    public static final String RF_ITEM_BATTL   = "battlevel";
    public static final String RF_ITEM_LAT   = "latitude";
    public static final String RF_ITEM_LON   = "longitude";
    public static final String RF_ITEM_SPEED   = "speed";
    public static final String RF_ITEM_TIME   = "time";
    public static final String RF_ITEM_ANALOG  = "analog";
    public static final String RF_ITEM_ALT1   = "alt1";
    public static final String RF_ITEM_DIO   = "dio";

    public static final String RF_ITEM_ICON   = "icon";
    public static final String RF_ITEM_WSPEED1   = "windspeed1";
    public static final String RF_ITEM_WSPEED2   = "windspeed2";
    public static final String RF_ITEM_WDIR1   = "winddir1";
    public static final String RF_ITEM_WDIR2   = "winddir2";
    public static final String RF_ITEM_WHEIG1   = "waveheight1";
    public static final String RF_ITEM_WHEIG2   = "waveheight2";
    public static final String RF_ITEM_REGION   = "region";
    public static final String RF_ITEM_DATE   = "date";

    public static final String RF_ITEM_LINK   = "link";
    public static final String RF_ITEM_RSSI   = "rssi";
    public static final String RF_ITEM_SERVICE   = "service";
    public static final String RF_ITEM_GWID   = "gatewayid";
    public static final String RF_ITEM_REGIST = "register";
    public static final String RF_ITEM_FISHID = "fishid";           //  Fish ID
    public static final String RF_ITEM_QTY = "quantity";
    public static final String RF_ITEM_UNIT = "unit";
    public static final String RF_ITEM_SOSV = "sosverify";          //  3: verify, 4: terminate
    public static final String RF_ITEM_MID = "mid";
    public static final String RF_ITEM_TYPE = "type";
    public static final String RF_ITEM_COUNT = "count";
    public static final String RF_ITEM_MESSAGE = "message";
    public static final String RF_ITEM_TOTAL = "total";
    public static final String RF_ITEM_DEEP = "deepsea";
    public static final String RF_ITEM_TAG = "tag";
    public static final String RF_ITEM_INDEX = "index";
    public static final String RF_ITEM_CODE = "code";
    public static final String RF_ITEM_INSERT = "insert";
    public static final String RF_ITEM_CURRENT = "current";
    public static final String RF_ITEM_DATA = "data";
    public static final String RF_ITEM_PORTID = "portid";
    public static final String RF_ITEM_PRICE = "price";
    public static final String RF_ITEM_TESTCMD = "testcmd";
    public static final String RF_ITEM_CH = "ch";
    public static final String RF_ITEM_DISTANCE = "tistance";

    public static final String RF_ITEM_NOTICEID = "Informasi pesan";

    public static final String BLE_ITEM_BTNTICK = "buttontick";
    public static final String BLE_ITEM_ADCDECT = "adcdect";
    public static final String BLE_ITEM_ADCFMT = "adcfmt";
    public static final String BLE_ITEM_TXINT = "txinterval";
    public static final String BLE_ITEM_VER = "blever";
    public static final String BLE_ITEM_STATUS = "status";
    public static final String BLE_ITEM_SUBTY = "subtype";
    public static final String BLE_ITEM_BUTTON = "button";
    public static final String BLE_ITEM_LONGPRESS = "longpress";
    public static final String BLE_ITEM_ADCVAL = "adcvalue";
    public static final String BLE_ITEM_QUEUE = "queuesize";

    //  Test
    public static final String TEST_ITEM_DATA = "data";
    public static final String TEST_ITEM_PACKET = "packet";

    //  GPS Command
    public static final byte GPS_START_PREFIX   = '$';
    public static final String GPS_CHECKSUM_FLAG  = "*";
    public static final String GPS_SPRIT_COMMA   = ",";
    public static final byte GPS_PROTO_VALID   = 'A';             //  GPS GeoData is valid
    public static final byte GPS_PROTO_INVALID = 'V';             //  GPS GeoData is invalid
    public static final String VMA_PROTOCOL_GPS_GNRMC    = "GNRMC";        //  GPS Commnads ($GNRMC)
    public static final String VMA_PROTOCOL_GPS_GNRMCPATT="(\\GNRMC)\\,((\\d{6})\\.?\\d*)\\,([AV]{1})\\,(\\d*\\.?\\d*)\\,([NS]{1})\\,(\\d*\\.?\\d*)\\,([EW]{1})\\,([0-9]*\\.?[0-9]*)\\,([0-9]*\\.?[0-9]*)\\,([0-9]{6})\\,([0-9]*\\.?[0-9]*)\\,([EW]?)\\,([ADEMSN]{1}.*.[0-9A-F]{2})";
    public static final char    GPS_DIR_NORTH   = 'N';
    public static final char    GPS_DIR_SOUTH   = 'S';
    public static final char    GPS_DIR_EAST   = 'E';
    public static final char    GPS_DIR_WEST   = 'W';

    //  GPS fields
    public static final String GPS_ITEM_STATUS   = "status";
    public static final String GPS_ITEM_SNR   = "snr";
    public static final String GPS_ITEM_DATE   = "date";
    public static final String GPS_ITEM_LAT    = "latitude";
    public static final String GPS_ITEM_LATSIGN   = "latsign";
    public static final String GPS_ITEM_LON   = "longitude";
    public static final String GPS_ITEM_LONSIGN   = "lonsign";
    public static final String GPS_ITEM_SPEED   = "speed";
    public static final String GPS_ITEM_BEARING   = "bearing"; // bearing
    public static final String GPS_LSAT_DATA   = "gpsLastData";
    public static final String GPS_NOTE   = "gpsNote";

    //  Add-on Version
    public static final String ADDON_VER_1_05  = "1.05";
    public static final String ADDON_VER_2_05  = "2.05";        //  Default

    //  H/W Version
    public static final String HW_VER_1_2       = "1.2";
    public static final String HW_VER_1_3       = "1.3";        //  Default

    //  LCD Bright
    //  Bright
    public static final int BRIGHT_DAY = 100;
    public static final int BRIGHT_NIGHT = 20;

}


package com.fstn.mobile.mymealcard;

/*----------------------------------------------------
| Class : DefaultSettings
| Author: JPF, 2014
+----------------------------------------------------- */
public class DefaultSettings {


    public static final int     CARD_LEN        = 16;
    public static final int     CVV_LEN         = 3;

    public static final String PREFS_NAME       = "MyBalance";
    public static final String CARDNUMBER       = "CARDNUMBER";
    public static final String CARDCVV          = "CARDCVV";
    public static final String STOREDBALANCE    = "STOREDBALANCE";
    public static final String STOREDDATE       = "STOREDDATE";
    public static final String STOREDHOUR       = "STOREDHOUR";
    public static final String STOREDTRANSACTIONS = "STOREDTRANSACTIONS";

    public static final String DATE_FORMAT      = "dd MMM";
    public static final String HOUR_FORMAT      = "HH:mm";


    public static final String EMPTY            = "";
    public static final String EURONULL         = "--- EUR";
    public static final String NO_UPDATE_DATE   = "---";
    public static final String EURO             = "EUR";
    public static final String EURO_SYMBOL      = "€";

    // connection properties
    public static final int    readTimeout      = 30000;
    public static final int    connectTimeout   = 25000;
    public static final int    HTTP_OK          = 200;
    public static final String USER             = "#USER#";
    public static final String PASS             = "#PASS#";


    public static final String PARSER_TD        = "TD";
    public static final String PARSER_TR        = "TR";
    public static final String PARSER_ON        = "on";
    public static final String PARSER_OFF       = "off";
    public static final String PARSER_CLASS     = "class";

    public static final String [] PARSER_MOVS   = {"-CCR-","-CPR-"};


}

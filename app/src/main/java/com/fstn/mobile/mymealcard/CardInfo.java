package com.fstn.mobile.mymealcard;

public class CardInfo {

    public static final int BALANCE_OK = 1;
    public static final int BALANCE_ERROR = 0;

    public String lastHttpResponseText;
    public String lastMessage;
    public String balance;
    public int status;

    String cardNumber;
    String cardCVV;
}

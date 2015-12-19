package com.fstn.mobile.mymealcard;

/*----------------------------------------------------
| Class : CardTransaction
| Author: JPF, 2014
+----------------------------------------------------- */
public class CardTransaction {

    private String trxDate;
    private String trxDescription;
    private String trxAmount;
    private int trxIcon;

    public String getTrxDate() {
        return trxDate;
    }

    public void setTrxDate(String trxDate) {
        this.trxDate = trxDate;
    }

    public String getTrxDescription() {
        return trxDescription;
    }

    public void setTrxDescription(String trxDescription) {
        this.trxDescription = trxDescription;
    }

    public String getTrxAmount() {
        return trxAmount;
    }

    public void setTrxAmount(String trxAmount) {
        this.trxAmount = trxAmount;
    }

    public int getTrxIcon() {
        return trxIcon;
    }

    public void setTrxIcon(int trxIcon) {
        this.trxIcon = trxIcon;
    }

}

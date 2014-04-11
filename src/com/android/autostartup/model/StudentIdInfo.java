package com.android.autostartup.model;

public class StudentIdInfo {

    public String cardId;
    public long updatedAt;

    public String toString() {
        return String.format("cardId=%1$s, updatedAt=%2$s", cardId, updatedAt);
    }

}

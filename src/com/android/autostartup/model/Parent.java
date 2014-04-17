package com.android.autostartup.model;

public class Parent {

    public long id;
    public String cardId;
    public String name;
    public String role;
    public String avatar;
    public String phone;
    public long createdAt;
    public long updatedAt;
    public long student;

    public Parent(long id, String cardId, String name, String role, String avatar, String phone,
            long createdAt, long updatedAt, long student) {
        this.id = id;
        this.cardId = cardId;
        this.name = name;
        this.role = role;
        this.avatar = avatar;
        this.phone = phone;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.student = student;
    }

    public String toString() {
        String value = "id=%s, cardId=%s, name=%s, role=%s, avatar=%s, phone=%s, createdAt=%s, updatedAt=%s, student=%s";
        return String.format(value, id, cardId, name, role, avatar, phone, createdAt, updatedAt,
                student);
    }

}

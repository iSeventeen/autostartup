package com.android.autostartup.model;

import java.io.IOException;

import com.android.autostartup.app.Application;

public class Student {

    public long id;
    public String cardId;
    public String name;
    public int grade;
    public int gender;
    public String address;
    public String avatar;
    public String notes;
    public long createdAt;
    public long updatedAt;

    public Student(long id, String cardId, String name, int grade, int gender, String address,
            String avatar, String notes, long createdAt, long updatedAt) {
        this.id = id;
        this.cardId = cardId;
        this.name = name;
        this.grade = grade;
        this.gender = gender;
        this.address = address;
        this.avatar = avatar;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String toJson() {
        return Application.getGson().toJson(this);
    }

    public static Student fromJson(String json) throws IOException {
        return Application.getGson().fromJson(json, Student.class);
    }

    public String toString() {
        String value = "id=%s, cardId=%s, name=%s, grade=%s, gender=%s, address=%s, avatar=%s, notes=%s, createdAt=%s, updatedAt=%s";
        return String.format(value, id, cardId, name, grade, gender, address, avatar, notes,
                createdAt, updatedAt);
    }

}

package com.android.autostartup.model;

import java.io.IOException;

import com.android.autostartup.app.Application;

public class Student {

    public long id;
    public String cardId;
    public String name;
    public int age;
    public int gender;
    public String avatar;
    public long createdAt;
    public long updatedAt;
//    public ArrayList<Parent> parents = new ArrayList<Parent>();

    public Student(long id, String cardId, String name, int age, int gender, String avatar,
            long createdAt, long updatedAt) {
        this.id = id;
        this.cardId = cardId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.avatar = avatar;
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
        String value = "id=%s, cardId=%s, name=%s, age=%s, gender=%s, avatar=%s, createdAt=%s, updatedAt=%s";
        return String.format(value, id, cardId, name, age, gender, avatar, createdAt, updatedAt);
    }

}

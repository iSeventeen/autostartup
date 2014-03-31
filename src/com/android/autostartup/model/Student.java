package com.android.autostartup.model;

import java.io.IOException;
import java.util.ArrayList;

import com.android.autostartup.app.Application;

public class Student {

    public Long id;
    public String cardId;
    public String name;
    public int age;
    public int gender;
    public String avatar;
    public ArrayList<Parent> parents = new ArrayList<Parent>();

    public String toJson() {
        return Application.getGson().toJson(this);
    }

    public static Student fromJson(String json) throws IOException {
        return Application.getGson().fromJson(json, Student.class);
    }

    public String toString() {
        String value = "id=%s, cardId=%s, name=%s, age=%s, gender=%s, avatar=%s, parents.length=%s";
        return String.format(value, id, cardId, name, age, gender, avatar, parents.size());
    }

}

package com.android.autostartup.model;

public class Parent {

    public long id;
    public String name;
    public String role;
    public String avatar;
    public long createdAt;
    public long updatedAt;
    public String student;

    public Parent(long id, String name, String role, String avatar, long createdAt, long updatedAt,
            String student) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.avatar = avatar;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.student = student;
    }

}

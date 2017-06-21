package com.heinrichreimersoftware.materialdrawerdemo.item;

public class Item {
    int id;
    String course_head;
    String hw_head;
    String hw_content;
    String hw_deadline;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourse_head() {
        return course_head;
    }

    public void setCourse_head(String course_head) {
        this.course_head = course_head;
    }

    public String getHw_head() {
        return hw_head;
    }

    public void setHw_head(String hw_head) {
        this.hw_head = hw_head;
    }

    public String getHw_content() {
        return hw_content;
    }

    public void setHw_content(String hw_content) {
        this.hw_content = hw_content;
    }

    public String getHw_deadline() {
        return hw_deadline;
    }

    public void setHw_deadline(String hw_deadline) {
        this.hw_deadline = hw_deadline;
    }
}

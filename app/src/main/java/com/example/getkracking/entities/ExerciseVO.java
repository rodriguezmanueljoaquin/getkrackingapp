package com.example.getkracking.entities;

public class ExerciseVO {

    private String name, desc;
    private int duration, quantity;

    public ExerciseVO(String name, String desc, int quantity, int duration) {
        this.name = name;
        this.desc = desc;
        this.quantity = quantity;
        this.duration = duration;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getDuration() {
        return duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

}

package com.half.wowsca.model;

/**
 * Created by slai4 on 9/22/2015.
 */
public class Achievement {

    private String name;
    private int number;

    @Override
    public String toString() {
        return "Ach{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

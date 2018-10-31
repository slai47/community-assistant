package com.half.wowsca.model.listModels;

import com.half.wowsca.model.enums.AverageType;

/**
 * Created by slai4 on 11/8/2015.
 */
public class ListAverages {

    private String title;
    private float average;
    private Float previous;
    private float expected;
    private AverageType type;

    public static ListAverages create(String title, float amount, float expected, AverageType type){
        ListAverages averages = new ListAverages();
        averages.setAverage(amount);
        averages.setTitle(title);
        averages.setExpected(expected);
        averages.setType(type);
        return averages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public float getExpected() {
        return expected;
    }

    public void setExpected(float expected) {
        this.expected = expected;
    }

    public AverageType getType() {
        return type;
    }

    public void setType(AverageType type) {
        this.type = type;
    }

    public Float getPrevious() {
        return previous;
    }

    public ListAverages setPrevious(Float previous) {
        this.previous = previous;
        return this;
    }
}

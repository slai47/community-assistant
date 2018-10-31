package com.clanassist.model.events.details;

/**
 * Created by Harrison on 6/5/2015.
 */
public class PlayerWN8Event {

    private String name;
    private int pastDay;
    private int pastWeek;
    private int pastMonth;
    private int pastTwoMonths;

    @Override
    public String toString() {
        return "PlayerWN8Event{" +
                "pastDay=" + pastDay +
                ", pastWeek=" + pastWeek +
                ", pastMonth=" + pastMonth +
                ", pastTwoMonths=" + pastTwoMonths +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPastDay() {
        return pastDay;
    }

    public void setPastDay(int pastDay) {
        this.pastDay = pastDay;
    }

    public int getPastWeek() {
        return pastWeek;
    }

    public void setPastWeek(int pastWeek) {
        this.pastWeek = pastWeek;
    }

    public int getPastMonth() {
        return pastMonth;
    }

    public void setPastMonth(int pastMonth) {
        this.pastMonth = pastMonth;
    }

    public int getPastTwoMonths() {
        return pastTwoMonths;
    }

    public void setPastTwoMonths(int pastTwoMonths) {
        this.pastTwoMonths = pastTwoMonths;
    }
}

package com.clanassist.model.player;

/**
 * Created by Harrison on 7/24/2014.
 */
public class Badges {

    private int mastery;
    private int firstClass;
    private int secondClass;
    private int thirdClass;

    public void addPlayerVehicleInfo(PlayerVehicleInfo info) {
        switch (info.getMarkOfMastery()) {
            case 4:
                mastery++;
                break;
            case 3:
                firstClass++;
                break;
            case 2:
                secondClass++;
                break;
            case 1:
                thirdClass++;
                break;
            default:
                break;
        }
    }

    public int getMastery() {
        return mastery;
    }

    public void setMastery(int mastery) {
        this.mastery = mastery;
    }

    public int getFirstClass() {
        return firstClass;
    }

    public void setFirstClass(int firstClass) {
        this.firstClass = firstClass;
    }

    public int getSecondClass() {
        return secondClass;
    }

    public void setSecondClass(int secondClass) {
        this.secondClass = secondClass;
    }

    public int getThirdClass() {
        return thirdClass;
    }

    public void setThirdClass(int thirdClass) {
        this.thirdClass = thirdClass;
    }
}

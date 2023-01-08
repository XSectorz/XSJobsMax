package net.xsapi.panat.xsjobsmax.core;

import java.util.ArrayList;

public class jobsSkill {

    private String nameID;
    private int maxLevel;
    private ArrayList<jobsLevel> jobsLevels = new ArrayList<>();

    public jobsSkill(String nameID,int maxLevel) {
        this.nameID = nameID;
        this.maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public String getNameID() {
        return nameID;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setNameID(String nameID) {
        this.nameID = nameID;
    }

    public ArrayList<jobsLevel> getJobsLevels() {
        return jobsLevels;
    }
}
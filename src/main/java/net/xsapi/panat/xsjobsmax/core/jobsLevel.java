package net.xsapi.panat.xsjobsmax.core;

import java.util.ArrayList;

public class jobsLevel {

    private ArrayList<String> abilityList = new ArrayList<>();
    private ArrayList<String> requiredList = new ArrayList<>();

    public jobsLevel(ArrayList<String> abilityList,ArrayList<String> requiredList) {
        this.abilityList = abilityList;
        this.requiredList = requiredList;
    }

    public ArrayList<String> getAbilityList() {
        return abilityList;
    }

    public ArrayList<String> getRequiredList() {
        return requiredList;
    }
}
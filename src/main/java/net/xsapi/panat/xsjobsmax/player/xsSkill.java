package net.xsapi.panat.xsjobsmax.player;

public class xsSkill {

    private String skillName;
    private int level;
    private int page = 1;

    public xsSkill(String skillName,int level) {

        this.skillName = skillName;
        this.level = level;

    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public int getLevel() {
        return level;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
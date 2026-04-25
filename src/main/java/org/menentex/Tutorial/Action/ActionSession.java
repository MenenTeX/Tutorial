package org.menentex.Tutorial.Action;

import org.bukkit.Location;

public class ActionSession {

    private final ActionManager.ActionType type;
    private int step = 0;

    private String input1;
    private String input2;
    private String input3;
    private String input4;

    private Location pos1;
    private Location pos2;

    public ActionSession(ActionManager.ActionType type){
        this.type = type;
    }

    public ActionManager.ActionType getType(){ return type; }

    public int getStep(){ return step; }
    public void nextStep(){ step++; }

    public String getInput1(){ return input1; }
    public void setInput1(String s){ input1 = s; }

    public String getInput2(){ return input2; }
    public void setInput2(String s){ input2 = s; }

    public String getInput3(){ return input3; }
    public void setInput3(String s){ input3 = s; }

    public String getInput4(){ return input4; }
    public void setInput4(String s){ input4 = s; }

    public Location getPos1() { return pos1; }
    public void setPos1(Location pos1){ this.pos1 = pos1; }

    public Location getPos2() { return pos2; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }

}

package org.menentex.Tutorial.Action;

public class ActionSession {

    private final ActionManager.ActionType type;
    private int step = 0;

    private String input1;
    private String input2;


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

}

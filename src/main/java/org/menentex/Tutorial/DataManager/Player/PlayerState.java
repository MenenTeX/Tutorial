package org.menentex.Tutorial.DataManager.Player;

import org.menentex.Tutorial.Events.TutorialEvent;

import java.util.UUID;

public class PlayerState {

    private final UUID playerId;

    private String guiName;
    private int currentEventIndex;

    private long waitUntilTick = 0;

    private boolean conditionBlocked = false;
    private TutorialEvent blockingEvent  = null;

    public PlayerState(UUID playerId, String guiName) {
        this.playerId = playerId;
        this.guiName = guiName;
        this.currentEventIndex = 0;
    }

    public void setWaitUntil(long tick) {
        this.waitUntilTick = tick;
    }

    public boolean isWaiting(long currentTick) {
        return currentTick < waitUntilTick;
    }

    public UUID getPlayerId() { return playerId; }

    public String getGuiName() { return guiName; }
    public void setGuiName(String guiName) { this.guiName = guiName; }

    public int getCurrentEventIndex() { return currentEventIndex; }
    public void setCurrentEventIndex(int idx) { this.currentEventIndex = idx; }
    public void nextEvent() { this.currentEventIndex++; }

    public void blockByCondition(TutorialEvent event) {
        this.conditionBlocked = true;
        this.blockingEvent = event;
    }

    public void unblockCondition() {
        this.conditionBlocked = false;
        this.blockingEvent = null;
    }

    public boolean isConditionBlocked() {
        return conditionBlocked;
    }

    public TutorialEvent getBlockingEvent() {
        return blockingEvent;
    }

}

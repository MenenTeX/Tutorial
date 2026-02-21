package org.menentex.Tutorial.DataManager.Player;

public class EditorState {

    private final String guiName;
    private String inventoryKey;
    private int currentPage = 1;
    private int currentEventSelected = -1;

    public EditorState(String guiName, String inventoryKey) {
        this.guiName = guiName;
        this.inventoryKey = inventoryKey;
    }

    public String getGuiName() {
        return guiName;
    }

    public String getInventoryKey() {
        return inventoryKey;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setInventoryKey(String inventoryKey) {
        this.inventoryKey = inventoryKey;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentEventSelected() {
        return currentEventSelected;
    }

    public void setCurrentEventSelected(int currentEventSelected) {
        this.currentEventSelected = currentEventSelected;
    }
}

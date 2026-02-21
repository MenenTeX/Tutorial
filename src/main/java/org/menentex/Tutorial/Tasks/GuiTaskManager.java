package org.menentex.Tutorial.Tasks;

import java.util.HashMap;
import java.util.Map;

public class GuiTaskManager {

    private final Map<String, GuiTask> tasks = new HashMap<>();

    public boolean isRunning(String guiName) {
        return tasks.containsKey(guiName);
    }

    public void register(String guiName, GuiTask task) {
        tasks.put(guiName, task);
    }

    public void unregister(String guiName) {
        tasks.remove(guiName);
    }

    public Map<String, GuiTask> getAllTasks(){
        return tasks;
    }

    public GuiTask getTaskForGui(String guiName){
        return tasks.get(guiName);
    }
}

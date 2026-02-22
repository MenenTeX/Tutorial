package org.menentex.Tutorial.DataManager.Gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RegistryGui {

    private final Map<String, InMemoryGui> guiList = new HashMap<>();

    public void registerGui(InMemoryGui gui){
        guiList.put(gui.getGuiName(), gui);
    }

    public Optional<InMemoryGui> getGui(String guiName){
        return Optional.ofNullable(guiList.get(guiName));
    }

    public Collection<InMemoryGui> getAllGuis() {
        return guiList.values();
    }

    public boolean exists(String guiName) {
        return guiList.containsKey(guiName);
    }

    public void removeGui(String guiName){
        if (exists(guiName))
            guiList.remove(guiName);
    }

    public void removeAllGui(){
        guiList.clear();
    }


}

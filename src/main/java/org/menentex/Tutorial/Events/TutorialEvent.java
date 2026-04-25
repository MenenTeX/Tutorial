package org.menentex.Tutorial.Events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.menentex.Tutorial.Main;

public abstract class TutorialEvent {

    protected int index;

    private String permission = null;

    public TutorialEvent(int index){
        this.index = index;
    }

    public int getIndex(){
        return index;
    }

    public void setIndex(int newIndex) { this.index = newIndex;}

    public abstract void execute(Player player);

    public abstract String getDisplayName();

    public abstract void serialize(ConfigurationSection section);

    public long getBlockingTicks() {
        return 0;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String perm){
        permission = perm;
    }

    public static TutorialEvent deserialize(ConfigurationSection section) {
        if (section == null) return null;

        String type = section.getString("type");
        if (type == null) return null;

        int index;
        try {
            index = Integer.parseInt(section.getName());
        } catch (NumberFormatException e) {
            index = 0;
        }

        TutorialEvent event = null;

        switch (type) {
            case "UnLockHeadMovement":
                event = new UnLockHeadMovementEvent(index);
                break;
            case "UnLockMovement":
                event = new UnLockMovementEvent(index);
                break;
            case "UnGodMode":
                event = new UnGodEvent(index);
                break;
            case "UnFly":
                event = new UnFlyEvent(index);
                break;
            case "LockHeadMovement":
                event = new LockHeadMovementEvent(index);
                break;
            case "LockMovement":
                event = new LockMovementEvent(index);
                break;
            case "GodMode":
                event = new GodModeEvent(index);
                break;
            case "Fly":
                event = new FlyEvent(index);
                break;
            case "ClearInventory":
                event = new ClearInventoryEvent(index);
                break;
            case "ClearChat":
                event = new ClearChatEvent(index);
                break;
            case "Title":
                event = TitleEvent.deserialize(index, section);
                break;
            case "Teleport":
                event = TeleportEvent.deserialize(index, section);
                break;
            case "SetGameMode":
                event = SetGameModeEvent.deserialize(index, section);
                break;
            case "Potion":
                event = PotionEvent.deserialize(index, section);
                break;
            case "PlaySound":
                event = PlaySoundEvent.deserialize(index, section);
                break;
            case "PlayerCommand":
                event = PlayerCmdEvent.deserialize(index, section);
                break;
            case "Message":
                event = MessageEvent.deserialize(index, section);
                break;
            case "GiveItem":
                event = GiveItemEvent.deserialize(index, section);
                break;
            case "Delay":
                event = DelayEvent.deserialize(index, section);
                break;
            case "ActionBar":
                event = ActionBarEvent.deserialize(index, section);
                break;
            default:
                Main.getInstance().getLogger()
                        .warning("Unknown tutorial event type: " + type);
                return null;
        }
        if (event != null && section.contains("permission")) {
            event.setPermission(section.getString("permission"));
        }
        return event;
    }

    public abstract ItemStack createItemForInv();
    
}

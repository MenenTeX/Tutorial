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
            case "UnLockHeadMovement" -> event = new UnLockHeadMovementEvent(index);
            case "UnLockMovement" -> event = new UnLockMovementEvent(index);
            case "UnGodMode" -> event = new UnGodEvent(index);
            case "UnFly" -> event = new UnFlyEvent(index);
            case "LockHeadMovement" -> event = new LockHeadMovementEvent(index);
            case "LockMovement" -> event = new LockMovementEvent(index);
            case "GodMode" -> event = new GodModeEvent(index);
            case "Fly" -> event = new FlyEvent(index);
            case "ClearInventory" -> event = new ClearInventoryEvent(index);
            case "ClearChat" -> event = new ClearChatEvent(index);
            case "Title" -> event = TitleEvent.deserialize(index, section);
            case "Teleport" -> event = TeleportEvent.deserialize(index, section);
            case "SetGameMode" -> event = SetGameModeEvent.deserialize(index, section);
            case "Potion" -> event = PotionEvent.deserialize(index, section);
            case "PlaySound" -> event = PlaySoundEvent.deserialize(index, section);
            case "PlayerCommand" -> event = PlayerCmdEvent.deserialize(index, section);
            case "Message" -> event = MessageEvent.deserialize(index, section);
            case "GiveItem" -> event = GiveItemEvent.deserialize(index, section);
            case "Delay" -> event = DelayEvent.deserialize(index, section);
            case "ActionBar" -> event = ActionBarEvent.deserialize(index, section);
            case "BossBar" -> event = BossBarEvent.deserialize(index, section);
            case "Cinematic" -> event = CinematicEvent.deserialize(index, section);
            case "Push" -> event = PushEvent.deserialize(index, section);
            case "StrikeLightning" -> event = StrikeLightningEvent.deserialize(index, section);
            case "SetRotation" -> event = SetRotationEvent.deserialize(index, section);
            case "WaitRegionEnter" -> event = WaitRegionEnterEvent.deserialize(index, section);


            default -> {
                Main.getInstance().getLogger()
                        .warning("Unknown tutorial event type: " + type);
                return null;
            }
        }
        if (event != null && section.contains("permission")) {
            event.setPermission(section.getString("permission"));
        }
        return event;
    }

    public abstract ItemStack createItemForInv();
    
}

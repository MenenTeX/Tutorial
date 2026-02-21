package org.menentex.Tutorial.Events;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DelayEvent extends TutorialEvents{

    private final long duration;


    public DelayEvent(int index, long duration){
        super(index);
        this.duration = duration;
    }

    public long getDuration(){
        return duration;
    }

    @Override
    public long getBlockingTicks() {
        return (duration);
    }

    @Override
    public void execute(Player player) {

    }

    @Override
    public String getDisplayName(){
        return "Delay";
    }

    @Override
    public void serialize(ConfigurationSection section) {
        section.set("type", getDisplayName());
        section.set("duration", duration);
    }

    public static DelayEvent deserialize(int index, ConfigurationSection section) {
        long duration = section.getLong("duration", 0L);
        return new DelayEvent(index, duration);
    }

}

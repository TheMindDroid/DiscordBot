package commands.HypixelClasses;

import net.dv8tion.jda.api.entities.MessageEmbed;

public class Minions {
    private final String name;
    private final int level;

    public Minions(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public MessageEmbed.Field getCompletedString() {
        return new MessageEmbed.Field(name, "Level: " + level, true);
    }
}

//Manages data for the Skyblock Collections.
package commands.HypixelClasses;

import commands.HypixelClasses.ComputationalClasses.AddCommas;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class CollectionBuilder {

    private int tiers;
    private final int exp;
    private final String collectionName;

    public CollectionBuilder(int tiers, int exp, String collectionName) {
        this.tiers = tiers;
        this.exp = exp;
        this.collectionName = collectionName;

        if (tiers == -1) {
            this.tiers *= -1;
        }
    }

    public MessageEmbed.Field getCompletedString() {
        return new MessageEmbed.Field(collectionName, "Level: " + tiers + "\nExperience: " +
                new AddCommas(exp).addCommas(), true);
    }
}
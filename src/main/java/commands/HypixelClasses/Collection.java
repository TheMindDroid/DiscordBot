//Manages data for the Skyblock Collections.
package commands.HypixelClasses;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DecimalFormat;

public class Collection {

    private int tiers;
    private final int exp;
    private final String collectionName;

    public Collection(int tiers, int exp, String collectionName) {
        this.tiers = tiers;
        this.exp = exp;
        this.collectionName = collectionName;

        if (tiers == -1) {
            this.tiers *= -1;
        }
    }

    public MessageEmbed.Field getCompletedString() {
        return new MessageEmbed.Field(collectionName, "Level: " + tiers + "\nExperience: " + addCommas(exp), true);
    }

    public static String addCommas(double withoutCommas) {
        DecimalFormat decimalFormat = new DecimalFormat(",###");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(withoutCommas);
    }
}
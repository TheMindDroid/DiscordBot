package commands.HypixelClasses.Collections;

import commands.HypixelClasses.CollectionBuilder;
import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import zone.nora.slothpixel.skyblock.players.collection.SkyblockCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CombatCollection {

    private final GuildMessageReceivedEvent event;
    private final SkyblockCollection tiers;
    private final SkyblockCollection exp;
    private final String playerName;

    public CombatCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        this.event = event;
        this.tiers = tiers;
        this.exp = exp;
        this.playerName = playerName;
    }

    //Returns combat collection data.
    public void getCollection() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Combat Collection :video_game:");

        List<CollectionBuilder> collections = new ArrayList<>();
        collections.add(new CollectionBuilder(tiers.getBlazeRod(), exp.getBlazeRod(), ":crossed_swords: Blaze Rod"));
        collections.add(new CollectionBuilder(tiers.getBone(), exp.getBone(), ":crossed_swords: Bone"));
        collections.add(new CollectionBuilder(tiers.getEnderpearl(), exp.getEnderpearl(), ":crossed_swords: Ender Pearl"));
        collections.add(new CollectionBuilder(tiers.getGhastTear(), exp.getGhastTear(), ":crossed_swords: Ghast Tear"));
        collections.add(new CollectionBuilder(tiers.getMagmaCream(), exp.getMagmaCream(), ":crossed_swords: Magma Cream"));
        collections.add(new CollectionBuilder(tiers.getBlazeRod(), exp.getBlazeRod(), ":crossed_swords: Blaze Rod"));
        collections.add(new CollectionBuilder(tiers.getRottenFlesh(), exp.getRottenFlesh(), ":crossed_swords: Rotten Flesh"));
        collections.add(new CollectionBuilder(tiers.getSlimeBall(), exp.getSlimeBall(), ":crossed_swords: Slime Ball"));
        collections.add(new CollectionBuilder(tiers.getBlazeRod(), exp.getBlazeRod(), ":crossed_swords: Blaze Rod"));
        collections.add(new CollectionBuilder(tiers.getSpiderEye(), exp.getSpiderEye(), ":crossed_swords: Spider Eye"));
        collections.add(new CollectionBuilder(tiers.getString(), exp.getString(), ":crossed_swords: String"));

        for (CollectionBuilder collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
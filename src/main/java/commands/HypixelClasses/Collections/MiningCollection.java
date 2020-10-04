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

public class MiningCollection {

    private final GuildMessageReceivedEvent event;
    private final SkyblockCollection tiers;
    private final SkyblockCollection exp;
    private final String playerName;

    public MiningCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        this.event = event;
        this.tiers = tiers;
        this.exp = exp;
        this.playerName = playerName;
    }

    //Returns mining collection data.
    public void getCollection() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Mining Collection :video_game:");

        List<CollectionBuilder> collections = new ArrayList<>();
        collections.add(new CollectionBuilder(tiers.getCoal(), exp.getCoal(), ":pick: Coal"));
        collections.add(new CollectionBuilder(tiers.getCobblestone(), exp.getCobblestone(), ":pick: Cobblestone"));
        collections.add(new CollectionBuilder(tiers.getDiamond(), exp.getDiamond(), ":pick: Diamond"));
        collections.add(new CollectionBuilder(tiers.getEmerald(), exp.getEmerald(), ":pick: Emerald"));
        collections.add(new CollectionBuilder(tiers.getEnderStone(), exp.getEnderStone(), ":pick: Endstone"));
        collections.add(new CollectionBuilder(tiers.getGlowstoneDust(), exp.getGlowstoneDust(), ":pick: Glowstone Dust"));
        collections.add(new CollectionBuilder(tiers.getGoldIngot(), exp.getGoldIngot(), ":pick: Gold"));
        collections.add(new CollectionBuilder(tiers.getGravel(), exp.getGravel(), ":pick: Gravel"));
        collections.add(new CollectionBuilder(tiers.getIce(), exp.getIce(), ":pick: Ice"));
        collections.add(new CollectionBuilder(tiers.getIronIngot(), exp.getIronIngot(), ":pick: Iron"));
        collections.add(new CollectionBuilder(tiers.getNetherrack(), exp.getNetherrack(), ":pick: Netherrack"));
        collections.add(new CollectionBuilder(tiers.getQuartz(), exp.getQuartz(), ":pick: Nether Quartz"));
        collections.add(new CollectionBuilder(tiers.getObsidian(), exp.getObsidian(), ":pick: Obsidian"));
        collections.add(new CollectionBuilder(tiers.getRedstone(), exp.getRedstone(), ":pick: Redstone"));
        collections.add(new CollectionBuilder(tiers.getSand(), exp.getSand(), ":pick: Sand"));

        for (CollectionBuilder collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
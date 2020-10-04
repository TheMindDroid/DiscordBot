package commands.HypixelClasses;

import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import commands.HypixelClasses.ComputationalClasses.ErrorMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import zone.nora.slothpixel.Slothpixel;
import zone.nora.slothpixel.player.Player;
import zone.nora.slothpixel.skyblock.players.minions.SkyblockMinions;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Minion {

    private final GuildMessageReceivedEvent event;
    private final Slothpixel hypixel;
    private final String[] messageSentArray;

    public Minion(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
        this.event = event;
        this.hypixel = hypixel;
        this.messageSentArray = messageSentArray;
    }

    public void getMinions() {
        if (messageSentArray.length == 2) {
            new ErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n" +
                    "Usage: ~hypixel minions <player>", false).sendErrorMessage();
            return;
        }

        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);

            String playerName = messageSentArray[2];
            Player player = hypixel.getPlayer(playerName);
            SkyblockMinions skyblockMinions = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid()).getMinions();

            eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
            eb.setTitle(":video_game: " + playerName + "'s Minions :video_game:");

            List<MinionBuilder> minions = new ArrayList<>();
            minions.add(new MinionBuilder(":evergreen_tree: Acacia Minion", skyblockMinions.getAcacia()));
            minions.add(new MinionBuilder(":evergreen_tree: Birch Minion", skyblockMinions.getBirch()));
            minions.add(new MinionBuilder(":tractor: Cactus Minion", skyblockMinions.getCactus()));
            minions.add(new MinionBuilder(":pick: Cobblestone Minion", skyblockMinions.getCobblestone()));
            minions.add(new MinionBuilder(":tractor: Cocoa Minion", skyblockMinions.getCocoa()));
            minions.add(new MinionBuilder(":crossed_swords: Cow Minion", skyblockMinions.getCow()));
            minions.add(new MinionBuilder(":evergreen_tree: Dark Oak Minion", skyblockMinions.getDarkOak()));
            minions.add(new MinionBuilder(":pick: End Stone Minion", skyblockMinions.getEnderStone()));
            minions.add(new MinionBuilder(":crossed_swords: Ghast Minion", skyblockMinions.getGhast()));
            minions.add(new MinionBuilder(":pick: Gold Minion", skyblockMinions.getGold()));
            minions.add(new MinionBuilder(":pick: Gravel Minion", skyblockMinions.getGravel()));
            minions.add(new MinionBuilder(":evergreen_tree: Jungle Minion", skyblockMinions.getJungle()));
            minions.add(new MinionBuilder(":tractor: Melon Minion", skyblockMinions.getMelon()));
            minions.add(new MinionBuilder(":tractor: Nether Warts Minion", skyblockMinions.getNetherWarts()));
            minions.add(new MinionBuilder(":pick: Quartz Minion", skyblockMinions.getQuartz()));
            minions.add(new MinionBuilder(":swords_crossed: Rabbit Minion", skyblockMinions.getRabbit()));
            minions.add(new MinionBuilder(":crossed_swords: Revenant Minion", skyblockMinions.getRevenant()));
            minions.add(new MinionBuilder(":pick: Snow Minion", skyblockMinions.getSnow()));
            minions.add(new MinionBuilder(":evergreen_tree: Spruce Minion", skyblockMinions.getSpruce()));
            minions.add(new MinionBuilder(":tractor: Sugar Cane Minion", skyblockMinions.getSugarCane()));
            minions.add(new MinionBuilder(":tractor: Wheat Minion", skyblockMinions.getWheat()));

            for (MinionBuilder minion : minions) {
                eb.addField(minion.getCompletedString());
            }

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());

            event.getChannel().sendMessage(eb.build()).queue();
        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve Minions data.*", true)
                    .sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }
}
package commands.HypixelClasses;

import commands.HypixelClasses.ComputationalClasses.AddCommas;
import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import commands.HypixelClasses.ComputationalClasses.ErrorMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import zone.nora.slothpixel.Slothpixel;
import zone.nora.slothpixel.player.Player;
import zone.nora.slothpixel.skyblock.players.SkyblockPlayer;
import zone.nora.slothpixel.skyblock.players.slayer.Slayer;

import java.awt.*;
import java.util.Objects;

public class SlayerStats {

    private final GuildMessageReceivedEvent event;
    private final Slothpixel hypixel;
    private final String[] messageSentArray;

    public SlayerStats(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
        this.event = event;
        this.hypixel = hypixel;
        this.messageSentArray = messageSentArray;
    }

    public void getSlayerStats() {

        if (messageSentArray.length == 2) {
            new ErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n"
                    + "Usage: ~hypixel slayer <player>", false).sendErrorMessage();
            return;
        }

        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);

            String playerName = messageSentArray[2];
            Player player = hypixel.getPlayer(playerName);
            SkyblockPlayer skyblockPlayer = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid());

            //Sets user's MC profile picture.
            eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
            eb.setTitle(":video_game: " + playerName + "'s Slayers :video_game:");

            Slayer zombie = skyblockPlayer.getSlayer().getZombie();
            String zombieStats = unwrapSlayer(zombie);

            Slayer spider = skyblockPlayer.getSlayer().getSpider();
            String spiderStats = unwrapSlayer(spider);

            Slayer wolf = skyblockPlayer.getSlayer().getWolf();
            String wolfStats = unwrapSlayer(wolf);

            eb.addField("Revenant Horror :zombie:", zombieStats, true);
            eb.addField("Tarantula Broodfather :spider_web:", spiderStats, true);
            eb.addField("Sven Packmaster :wolf:", wolfStats, true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());

            event.getChannel().sendMessage(eb.build()).queue();

        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve Slayer data.*", true)
                    .sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }

    //Cycles through a map to gather Slayer data.
    public static String unwrapSlayer(Slayer slayer) {

        StringBuilder sb = new StringBuilder();

        sb.append("Level: ").append(slayer.getClaimedLevels()).append("\n").append("Experience: ")
                .append(new AddCommas(slayer.getXp()).addCommas()).append("\n");

        slayer.getKillsTier().forEach( (level, kills) ->
                sb.append("Level: ").append(level).append(" - ").append("Kills: ").append(kills).append("\n"));

        return sb.toString();
    }
}
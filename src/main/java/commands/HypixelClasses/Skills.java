package commands.HypixelClasses;

import commands.HypixelClasses.ComputationalClasses.AddCommas;
import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import commands.HypixelClasses.ComputationalClasses.ErrorMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import zone.nora.slothpixel.Slothpixel;
import zone.nora.slothpixel.player.Player;
import zone.nora.slothpixel.skyblock.players.skills.SkyblockSkill;
import zone.nora.slothpixel.skyblock.players.skills.SkyblockSkills;

import java.awt.*;
import java.util.Objects;

public class Skills {

    private final GuildMessageReceivedEvent event;
    private final Slothpixel hypixel;
    private final String[] messageSentArray;

    public Skills(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
        this.event = event;
        this.hypixel = hypixel;
        this.messageSentArray = messageSentArray;
    }

    public void getSkills() {

        if (messageSentArray.length == 2) {
            new ErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n" +
                    "Usage: ~hypixel skills <player>", false).sendErrorMessage();
            return;
        }

        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);

            String playerName = messageSentArray[2];
            Player player = hypixel.getPlayer(playerName);
            SkyblockSkills skyblockSkills = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid()).getSkills();

            eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
            eb.setTitle(":video_game: " + playerName + "'s Skills :video_game:");

            double totalSkillLevelsAdded = 0;
            try {
                totalSkillLevelsAdded += skyblockSkills.getAlchemy().getLevel();
                eb.addField(":test_tube: Alchemy - Level: " + skyblockSkills.getAlchemy().getLevel(),
                        formatSkills(skyblockSkills.getAlchemy()), false);
            } catch (Exception e) {
                eb.addField(":test_tube: Alchemy", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getCarpentry().getLevel();
                eb.addField(":tools: Carpentry - Level: " + skyblockSkills.getCarpentry().getLevel(),
                        formatSkills(skyblockSkills.getCarpentry()), false);
            } catch (Exception e) {
                eb.addField(":tools: Carpentry", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getCombat().getLevel();
                eb.addField(":crossed_swords: Combat - Level: " + skyblockSkills.getCombat().getLevel(),
                        formatSkills(skyblockSkills.getCombat()), false);
            } catch (Exception e) {
                eb.addField(":crossed_swords: Combat", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getEnchanting().getLevel();
                eb.addField(":book: Enchanting - Level: " + skyblockSkills.getEnchanting().getLevel(),
                        formatSkills(skyblockSkills.getEnchanting()), false);
            } catch (Exception e) {
                eb.addField(":book: Enchanting", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getFarming().getLevel();
                eb.addField(":tractor: Farming - Level: " + skyblockSkills.getFarming().getLevel(),
                        formatSkills(skyblockSkills.getFarming()), false);
            } catch (Exception e) {
                eb.addField(":tractor: Farming", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getFishing().getLevel();
                eb.addField(":fishing_pole_and_fish: Fishing - Level: " + skyblockSkills.getFishing().getLevel(),
                        formatSkills(skyblockSkills.getFishing()), false);
            } catch (Exception e) {
                eb.addField(":fishing_pole_and_fish: Fishing", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getForaging().getLevel();
                eb.addField(":evergreen_tree: Foraging - Level: " + skyblockSkills.getForaging().getLevel(),
                        formatSkills(skyblockSkills.getForaging()), false);
            } catch (Exception e) {
                eb.addField(":evergreen_tree: Foraging", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getMining().getLevel();
                eb.addField(":pick: Mining - Level: " + skyblockSkills.getMining().getLevel(),
                        formatSkills(skyblockSkills.getMining()), false);
            } catch (Exception e) {
                eb.addField(":pick: Mining", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getRunecrafting().getLevel();
                eb.addField(":rainbow: Runecrafting - Level: " + skyblockSkills.getRunecrafting().getLevel(),
                        formatSkills(skyblockSkills.getRunecrafting()), false);
            } catch (Exception e) {
                eb.addField(":rainbow: Runecrafting", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getTaming().getLevel();
                eb.addField(":service_dog: Taming - Level: " + skyblockSkills.getTaming().getLevel(),
                        formatSkills(skyblockSkills.getTaming()), false);
            } catch (Exception e) {
                eb.addField(":service_dog: Taming ", "Player has not progressed in this skill.", false);
            }

            if (totalSkillLevelsAdded == 0) {
                new ErrorMessage(event,"API Unavailable", "Player's API settings are disabled.",
                        true).sendErrorMessage();
                return;
            }

            eb.addField(":bar_chart: Average Skill Level:", "Level: " + (totalSkillLevelsAdded / 10), true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());

            event.getChannel().sendMessage(eb.build()).queue();

        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve Skills data.*", true)
                    .sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    //Formats skills for the embed.
    public String formatSkills(SkyblockSkill skyblockSkill) {

        int stringToPercent = (int) Math.round(Double.parseDouble(skyblockSkill.getProgress()) * 100);

        StringBuilder sb = new StringBuilder();

        if (skyblockSkill.getXpForNext() == 0) {
            sb.append("Skill Maxed.\n").append("Total Experience: ")
                    .append(new AddCommas(Double.parseDouble(skyblockSkill.getXp())).addCommas())
                    .append("\n").append("100%\n").append(":green_square:".repeat(Math.max(0, 10)));
            return sb.toString();
        } else {
            sb.append("Experience to next level: ").append(new AddCommas(skyblockSkill.getXpCurrent()).addCommas()).append(" / ")
                    .append(new AddCommas(skyblockSkill.getXpForNext()).addCommas()).append("\n").append("Total Experience: ")
                    .append(new AddCommas(Double.parseDouble(skyblockSkill.getXp())).addCommas()).append("\n");
            return sb + percentVisualizer(stringToPercent);
        }
    }


    //Converts a percentage to a visualized progress bar.
    public String percentVisualizer(int stringToPercent) {

        StringBuilder sb = new StringBuilder();

        int completed = stringToPercent - stringToPercent % 10;
        int remaining = 100 - completed;

        sb.append("Progress: ").append(stringToPercent).append("%").append("\n");

        if (stringToPercent >= 10) {
            sb.append(":green_square:".repeat(Math.max(0, (completed - 1) / 10)));
            sb.append(":yellow_square:");
            sb.append(":red_square:".repeat(Math.max(0, remaining / 10)));
        } else {
            sb.append(":yellow_square:");
            sb.append(":red_square:".repeat(Math.max(0, 9)));
        }

        return sb.toString();
    }
}
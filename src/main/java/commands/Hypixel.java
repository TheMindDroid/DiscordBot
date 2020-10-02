//Hypixel Bot: Sends Hypixel Skyblock player statistics to Discord channels.

package commands;

import commands.HypixelClasses.AddDatedFooter;
import commands.HypixelClasses.Collection;
import commands.HypixelClasses.Minions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.nora.slothpixel.Slothpixel;
import zone.nora.slothpixel.guild.Guild;
import zone.nora.slothpixel.guild.member.GuildMember;
import zone.nora.slothpixel.player.Player;
import zone.nora.slothpixel.skyblock.items.SkyblockItem;
import zone.nora.slothpixel.skyblock.players.SkyblockPlayer;
import zone.nora.slothpixel.skyblock.players.collection.SkyblockCollection;
import zone.nora.slothpixel.skyblock.players.minions.SkyblockMinions;
import zone.nora.slothpixel.skyblock.players.skills.SkyblockSkill;
import zone.nora.slothpixel.skyblock.players.skills.SkyblockSkills;
import zone.nora.slothpixel.skyblock.players.slayer.Slayer;
import zone.nora.slothpixel.skyblock.players.stats.auctions.SkyblockPlayerAuctions;
import zone.nora.slothpixel.skyblock.players.stats.kills.SkyblockPlayerKills;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Hypixel extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] messageSentArray = event.getMessage().getContentRaw().split(" ");

        if (!messageSentArray[0].equalsIgnoreCase("~hypixel")) {
            return;
        }

        if (messageSentArray.length == 1) {
            sendErrorMessage(event, "Invalid Arguments...",
                    "Usage: ~hypixel [collections/minions/guild/skills/slayer/stats]", false);
            return;
        }

        Slothpixel hypixel = new Slothpixel();

        switch (messageSentArray[1]) {
            case "guild":
                getGuildInformation(event, hypixel);
                break;
            case "stats":
                getPlayerStatistic(event, hypixel, messageSentArray);
                break;
            case "slayer":
                getSlayerStats(event, hypixel, messageSentArray);
                break;
            case "skills":
                getSkills(event, hypixel, messageSentArray);
                break;
            case "minions":
                getMinions(event, hypixel, messageSentArray);
                break;
            case "collections":
                getCollections(event, hypixel, messageSentArray);
                break;
            default:
                sendErrorMessage(event, "Invalid Arguments...",
                        "Usage: ~hypixel [collections/minions/guild/stats/slayer/skills]", false);
                break;
        }
    }


    public void getGuildInformation(GuildMessageReceivedEvent event, Slothpixel hypixel) {
        //Currently only gathers information of personal Guild as larger Guilds have a tendency to throw exceptions.
        event.getChannel().sendMessage("Processing request...").queue();

        try {
            Guild guild = hypixel.getGuild("86473522c26e4c2daf8b3fff05540b85");

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.GREEN);
            eb.setTitle(":shield: Guild Name: " + guild.getName());
            eb.addField(":label: Guild Tag:", guild.getTag(), true);
            eb.addField(":memo: Description", guild.getDescription(), true);

            //Calls guild public.
            if (guild.getPublic()) {
                eb.addField(":busts_in_silhouette: Guild Public:", ":white_check_mark:", true);
            } else {
                eb.addField(":busts_in_silhouette: Guild Public:", ":x:", true);
            }

            StringBuilder online = new StringBuilder();
            StringBuilder offline = new StringBuilder();

            //Calls member list.
            for (GuildMember member : guild.getMembers()) {
                try {
                    String name = Objects.requireNonNull(member.getProfile()).getUsername();
                    Player player = hypixel.getPlayer(name);

                    if (player.getOnline()) {
                        online.append(name).append("\n");
                    } else {
                        offline.append(name).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            eb.addField("Online Players: :green_circle:", online.toString(), true);
            eb.addField("Offline Players: :red_circle:", offline.toString(), true);

            //Calls guild experience.
            eb.addField(":chart_with_upwards_trend: Guild Level:", "Level: " + guild.getLevel()
                    + " \nExperience: " + addCommas(guild.getExp()), true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
            event.getChannel().sendMessage(eb.build()).queue();
        } catch (Exception e) {
            sendErrorMessage(event, "API Unavailable", "Unable to gather Guild information." , false);
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    public void getPlayerStatistic(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {

        if (messageSentArray.length == 2) {
            sendErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n" +
                    "Usage: ~hypixel stats <player>", false);
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
            eb.setTitle(":video_game: Player Name: " + playerName + " :video_game:");

            //Calls if player is online.
            if (player.getOnline()) {
                eb.addField("Player Online: ", ":green_circle: \n"
                        + "Location: " + hypixel.getPlayerStatus(playerName).getGame().getMode(), true);
            } else {
                String lastLoginDate = convertMillisToDate(player.getLastLogin());
                eb.addField("Player Online: ", ":red_circle: \n [Last Online: " + lastLoginDate + "] :hourglass:", true);
            }

            //Calls the rank of the player.
            StringBuilder rank = new StringBuilder();
            try {
                String[] rankArray = player.getRank().split("");

                if (player.getRank().contains("_PLUS")) {
                    if (player.getRank().contains("_PLUS_PLUS")) {
                        rank.append("MVP++");
                    } else {
                        for (int i = 0; i < 3; i++) {
                            rank.append(rankArray[i]);
                        }
                        rank.append("+");
                    }
                } else {
                    rank.append(player.getRank());
                }
                eb.addField(":military_medal: Player Rank: ", rank.toString(), true);
            } catch (Exception e) {
                eb.addField(":military_medal: Player Rank: ", "No Rank", true);
            }

            //Calls money in purse.
            if (hypixel.getSkyblockProfile(playerName).getBanking().getBalance() == 0) {
                eb.addField(":moneybag: Money:","Purse: $" + addCommas(skyblockPlayer.getCoinPurse()), true);
            } else {
                eb.addField(":moneybag: Money:","Bank: $"
                        + addCommas(hypixel.getSkyblockProfile(playerName).getBanking().getBalance())
                        + "\nPurse: $" + addCommas(skyblockPlayer.getCoinPurse()), true);
            }

            //Calls the armor the player is wearing.
            StringBuilder armor = new StringBuilder();
            for (SkyblockItem skyblockItem : skyblockPlayer.getArmor()) {
                String[] shortenedName = skyblockItem.getName().split("");

                for (int i = 2; i < shortenedName.length; i++) {
                    if (!shortenedName[i].equalsIgnoreCase("§")) {
                        armor.append(shortenedName[i]);
                    } else {
                        break;
                    }
                }

                if (!skyblockItem.getName().isEmpty()) {
                    armor.append("\n");
                }
            }

            if (armor.toString().equalsIgnoreCase("")) {
                eb.addField(":shield: Armor Loadout: ", "No Armor Equipped.", true);
            } else {
                eb.addField(":shield: Armor Loadout: ", armor.toString(), true);
            }

            //Calls auction statistics for the player.
            try {
                SkyblockPlayerAuctions skyblockPlayerAuctions = skyblockPlayer.getStats().getAuctions();
                eb.addField(":chart_with_upwards_trend: Auction Stats:", "Money Spent: $"
                        + addCommas(skyblockPlayerAuctions.getGoldSpent())
                        + "\n Money Earned: $" + addCommas(skyblockPlayerAuctions.getGoldEarned())
                        + "\n Commons Sold: " + skyblockPlayerAuctions.getSold().getCommon()
                        + "\n Uncommons Sold: " + skyblockPlayerAuctions.getSold().getUncommon()
                        + "\n Rares Sold: " + skyblockPlayerAuctions.getSold().getRare()
                        + "\n Epics Sold: " + skyblockPlayerAuctions.getSold().getRare()
                        + "\n Legendaries Sold: " + skyblockPlayerAuctions.getSold().getLegendary(), true);
            } catch (Exception e) {
                eb.addField(":chart_with_upwards_trend: Auction Stats:", "Player has no auction data.", true);
            }

            //Calls combat statistics for the player.
            try {
                eb.addField(":crossed_swords: Combat Statistics:", "Total Kills: "
                        + addCommas(skyblockPlayer.getStats().getTotalKills())
                        + "\nTotal Deaths: " + addCommas(skyblockPlayer.getStats().getTotalDeaths())
                        + "\nHighest Crit Damage: " + addCommas(skyblockPlayer.getStats().getHighestCriticalDamage()), true);
            } catch (Exception e) {
                eb.addField(":crossed_swords: Combat Statistics:", "Player does not have available combat statistics.", true);
            }

            //Calls fishing catches.
            try {
                SkyblockPlayerKills skyblockPlayerKills = skyblockPlayer.getStats().getKills();

                int totalSeaCreatureKills = skyblockPlayerKills.getWaterHydra()
                        + skyblockPlayerKills.getSeaWitch()
                        + skyblockPlayerKills.getSeaWalker()
                        + skyblockPlayerKills.getSeaLeech()
                        + skyblockPlayerKills.getSeaGuardian()
                        + skyblockPlayerKills.getSeaArcher()
                        + skyblockPlayerKills.getPondSquid()
                        + skyblockPlayerKills.getNightSquid()
                        + skyblockPlayerKills.getGuardianEmperor()
                        + skyblockPlayerKills.getFrozenSteve()
                        + skyblockPlayerKills.getFrostyTheSnowman()
                        + skyblockPlayerKills.getDeepSeaProtector()
                        + skyblockPlayerKills.getChickenDeep()
                        + skyblockPlayerKills.getCatfish()
                        + skyblockPlayerKills.getCarrotKing();

                eb.addField(":fishing_pole_and_fish: Items Fished:", "Total Items: "
                        + addCommas(skyblockPlayer.getStats().getItemsFished().getTotal())
                        + "\nNormal Items: " + addCommas(skyblockPlayer.getStats().getItemsFished().getNormal())
                        + "\nTreasures Fished: " + addCommas(skyblockPlayer.getStats().getItemsFished().getTreasure())
                        + "\nLarge Treasures Fished: " + addCommas(skyblockPlayer.getStats().getItemsFished().getLargeTreasure())
                        + "\nSea Creatures Killed: " + addCommas(totalSeaCreatureKills), true);
            } catch (Exception e) {
                eb.addField(":tropical_fish: Items Fished:", "Unable to retrieve fishing data.", true);
            }

            //Calls fairy souls collected.
            eb.addField(":woman_fairy_tone2: Fairy Souls:", skyblockPlayer.getFairySoulsCollected() + " / "
                    + "209", true);

            //Calls date first joined.
            eb.addField(":calendar: Player First Joined:", convertMillisToDate(skyblockPlayer.getFirstJoin()), true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
            event.getChannel().sendMessage(eb.build()).queue();

        } catch (Exception e) {
            sendErrorMessage(event, "API Unavailable", "Unable to retrieve player data.*", true);
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    public void getSlayerStats(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {

        if (messageSentArray.length == 2) {
            sendErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n"
                    + "Usage: ~hypixel slayer <player>", false);
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
            sendErrorMessage(event, "API Unavailable", "Unable to retrieve Slayer data.*", true);
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    public void getSkills(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {

        if (messageSentArray.length == 2) {
            sendErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n" +
                    "Usage: ~hypixel skills <player>", false);
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
            } catch (Exception e){
                eb.addField(":crossed_swords: Combat", "Player has not progressed in this skill.", false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getEnchanting().getLevel();
                eb.addField(":book: Enchanting - Level: " + skyblockSkills.getEnchanting().getLevel(),
                        formatSkills(skyblockSkills.getEnchanting()), false);
            } catch (Exception e) {
                eb.addField(":book: Enchanting", "Player has not progressed in this skill.",false);
            }
            try {
                totalSkillLevelsAdded += skyblockSkills.getFarming().getLevel();
                eb.addField(":tractor: Farming - Level: " + skyblockSkills.getFarming().getLevel(),
                        formatSkills(skyblockSkills.getFarming()), false);
            } catch (Exception e) {
                eb.addField(":tractor: Farming", "Player has not progressed in this skill.",false);
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
            eb.addField(":bar_chart: Average Skill Level:", "Level: " + (totalSkillLevelsAdded / 10), true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());

            event.getChannel().sendMessage(eb.build()).queue();

        } catch (Exception e) {
            sendErrorMessage(event, "API Unavailable", "Unable to retrieve Skills data.*", true);
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    public void getMinions(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
        if (messageSentArray.length == 2) {
            sendErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n" +
                    "Usage: ~hypixel minions <player>", false);
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

            List<Minions> minions = new ArrayList<>();
            minions.add(new Minions(":evergreen_tree: Acacia Minion", skyblockMinions.getAcacia()));
            minions.add(new Minions(":evergreen_tree: Birch Minion", skyblockMinions.getBirch()));
            minions.add(new Minions(":tractor: Cactus Minion", skyblockMinions.getCactus()));
            minions.add(new Minions(":pick: Cobblestone Minion", skyblockMinions.getCobblestone()));
            minions.add(new Minions(":tractor: Cocoa Minion", skyblockMinions.getCocoa()));
            minions.add(new Minions(":crossed_swords: Cow Minion", skyblockMinions.getCow()));
            minions.add(new Minions(":evergreen_tree: Dark Oak Minion", skyblockMinions.getDarkOak()));
            minions.add(new Minions(":pick: End Stone Minion", skyblockMinions.getEnderStone()));
            minions.add(new Minions(":crossed_swords: Ghast Minion", skyblockMinions.getGhast()));
            minions.add(new Minions(":pick: Gold Minion", skyblockMinions.getGold()));
            minions.add(new Minions(":pick: Gravel Minion", skyblockMinions.getGravel()));
            minions.add(new Minions(":evergreen_tree: Jungle Minion", skyblockMinions.getJungle()));
            minions.add(new Minions(":tractor: Melon Minion", skyblockMinions.getMelon()));
            minions.add(new Minions(":tractor: Nether Warts Minion", skyblockMinions.getNetherWarts()));
            minions.add(new Minions(":pick: Quartz Minion", skyblockMinions.getQuartz()));
            minions.add(new Minions(":swords_crossed: Rabbit Minion", skyblockMinions.getRabbit()));
            minions.add(new Minions(":crossed_swords: Revenant Minion", skyblockMinions.getRevenant()));
            minions.add(new Minions(":pick: Snow Minion", skyblockMinions.getSnow()));
            minions.add(new Minions(":evergreen_tree: Spruce Minion", skyblockMinions.getSpruce()));
            minions.add(new Minions(":tractor: Sugar Cane Minion", skyblockMinions.getSugarCane()));
            minions.add(new Minions(":tractor: Wheat Minion", skyblockMinions.getWheat()));

            for (Minions minion : minions) {
                eb.addField(minion.getCompletedString());
            }

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());

            event.getChannel().sendMessage(eb.build()).queue();
        } catch (Exception e) {
            sendErrorMessage(event, "API Unavailable", "Unable to retrieve Minions data.*", true);
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    //Returns the collection stats of the player.
    public void getCollections(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
        if (messageSentArray.length == 2 || messageSentArray.length == 3) {
            sendErrorMessage(event, "Invalid Arguments...",
                    "Usage: ~hypixel collections <player> [combat/farming/fishing/foraging/mining]",true);
            return;
        }

        try {
            String playerName = messageSentArray[2];
            Player player = hypixel.getPlayer(playerName);
            SkyblockCollection skyblockCollectionTiers = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid()).getCollectionTiers();
            SkyblockCollection skyblockCollectionExp = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid()).getCollection();

            switch (messageSentArray[3]) {
                case "farming":
                    getFarmingCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName);
                    break;
                case "mining":
                    getMiningCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName);
                    break;
                case "combat":
                    getCombatCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName);
                    break;
                case "foraging":
                    getForagingCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName);
                    break;
                case "fishing":
                    getFishingCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName);
                    break;
                default:
                    sendErrorMessage(event, "Invalid Arguments...",
                            "Usage: ~hypixel collections <player> [combat/farming/fishing/foraging/mining]",true);
                    break;
            }
        } catch (Exception e) {
            sendErrorMessage(event, "API Unavailable", "Unable to retrieve Minions data.*", true);
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    //Returns farming collection data.
    public static void getFarmingCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Farming Collection :video_game:");

        List<Collection> collections = new ArrayList<>();
        collections.add(new Collection(tiers.getCactus(), exp.getCactus(), ":tractor: Cactus"));
        collections.add(new Collection(tiers.getCarrotItem(), exp.getCarrotItem(), ":tractor: Carrot"));
        collections.add(new Collection(tiers.getRawChicken(), exp.getRawChicken(), ":tractor: Chicken"));
        collections.add(new Collection(tiers.getFeather(), exp.getFeather(), ":tractor: Feather"));
        collections.add(new Collection(tiers.getLeather(), exp.getLeather(), ":tractor: Leather"));
        collections.add(new Collection(tiers.getMelon(), exp.getMelon(), ":tractor: Melon"));
        collections.add(new Collection(tiers.getMushroomCollection(), exp.getMushroomCollection(), ":tractor: Mushroom"));
        collections.add(new Collection(tiers.getMutton(), exp.getMutton(), ":tractor: Mutton"));
        collections.add(new Collection(tiers.getNetherStalk(), exp.getNetherStalk(), ":tractor: Nether Wart"));
        collections.add(new Collection(tiers.getPork(), exp.getPork(), ":tractor: Pork Chop"));
        collections.add(new Collection(tiers.getPotatoItem(), exp.getPotatoItem(), ":tractor: Potato"));
        collections.add(new Collection(tiers.getPumpkin(), exp.getPumpkin(), ":tractor: Pumpkin"));
        collections.add(new Collection(tiers.getRabbit(), exp.getRabbit(), ":tractor: Rabbit"));
        collections.add(new Collection(tiers.getSeeds(), exp.getSeeds(), ":tractor: Seeds"));
        collections.add(new Collection(tiers.getSugarCane(), exp.getSugarCane(), ":tractor: Sugar Cane"));
        collections.add(new Collection(tiers.getWheat(), exp.getWheat(), ":tractor: Wheat"));

        for (Collection collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }


    //Returns mining collection data.
    public static void getMiningCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Mining Collection :video_game:");

        List<Collection> collections = new ArrayList<>();
        collections.add(new Collection(tiers.getCoal(), exp.getCoal(), ":pick: Coal"));
        collections.add(new Collection(tiers.getCobblestone(), exp.getCobblestone(), ":pick: Cobblestone"));
        collections.add(new Collection(tiers.getDiamond(), exp.getDiamond(), ":pick: Diamond"));
        collections.add(new Collection(tiers.getEmerald(), exp.getEmerald(), ":pick: Emerald"));
        collections.add(new Collection(tiers.getEnderStone(), exp.getEnderStone(), ":pick: Endstone"));
        collections.add(new Collection(tiers.getGlowstoneDust(), exp.getGlowstoneDust(), ":pick: Glowstone Dust"));
        collections.add(new Collection(tiers.getGoldIngot(), exp.getGoldIngot(), ":pick: Gold"));
        collections.add(new Collection(tiers.getGravel(), exp.getGravel(), ":pick: Gravel"));
        collections.add(new Collection(tiers.getIce(), exp.getIce(), ":pick: Ice"));
        collections.add(new Collection(tiers.getIronIngot(), exp.getIronIngot(), ":pick: Iron"));
        collections.add(new Collection(tiers.getNetherrack(), exp.getNetherrack(), ":pick: Netherrack"));
        collections.add(new Collection(tiers.getQuartz(), exp.getQuartz(), ":pick: Nether Quartz"));
        collections.add(new Collection(tiers.getObsidian(), exp.getObsidian(), ":pick: Obsidian"));
        collections.add(new Collection(tiers.getRedstone(), exp.getRedstone(), ":pick: Redstone"));
        collections.add(new Collection(tiers.getSand(), exp.getSand(), ":pick: Sand"));

        for (Collection collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }


    //Returns combat collection data.
    public static void getCombatCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Combat Collection :video_game:");

        List<Collection> collections = new ArrayList<>();
        collections.add(new Collection(tiers.getBlazeRod(), exp.getBlazeRod(), ":crossed_swords: Blaze Rod"));
        collections.add(new Collection(tiers.getBone(), exp.getBone(), ":crossed_swords: Bone"));
        collections.add(new Collection(tiers.getEnderpearl(), exp.getEnderpearl(), ":crossed_swords: Ender Pearl"));
        collections.add(new Collection(tiers.getGhastTear(), exp.getGhastTear(), ":crossed_swords: Ghast Tear"));
        collections.add(new Collection(tiers.getMagmaCream(), exp.getMagmaCream(), ":crossed_swords: Magma Cream"));
        collections.add(new Collection(tiers.getBlazeRod(), exp.getBlazeRod(), ":crossed_swords: Blaze Rod"));
        collections.add(new Collection(tiers.getRottenFlesh(), exp.getRottenFlesh(), ":crossed_swords: Rotten Flesh"));
        collections.add(new Collection(tiers.getSlimeBall(), exp.getSlimeBall(), ":crossed_swords: Slime Ball"));
        collections.add(new Collection(tiers.getBlazeRod(), exp.getBlazeRod(), ":crossed_swords: Blaze Rod"));
        collections.add(new Collection(tiers.getSpiderEye(), exp.getSpiderEye(), ":crossed_swords: Spider Eye"));
        collections.add(new Collection(tiers.getString(), exp.getString(), ":crossed_swords: String"));

        for (Collection collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }


    //Returns foraging collection data.
    public static void getForagingCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Foraging Collection :video_game:");

        List<Collection> collections = new ArrayList<>();
        collections.add(new Collection(tiers.getLog_2(), exp.getLog_2(), ":evergreen_tree: Acacia Wood"));
        collections.add(new Collection(tiers.getLog2(), exp.getLog2(), ":evergreen_tree: Birch Wood"));
        collections.add(new Collection(tiers.getLog_21(), exp.getLog_21(), ":evergreen_tree: Dark Oak Wood"));
        collections.add(new Collection(tiers.getLog(), exp.getLog(), ":evergreen_tree: Oak Wood"));
        collections.add(new Collection(tiers.getLog3(), exp.getLog3(), ":evergreen_tree: Jungle Wood"));
        collections.add(new Collection(tiers.getLog1(), exp.getLog1(), ":evergreen_tree: Spruce Wood"));

        for (Collection collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }


    //Returns fishing collection data.
    public static void getFishingCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Fishing Collection :video_game:");

        List<Collection> collections = new ArrayList<>();
        collections.add(new Collection(tiers.getClayBall(), exp.getClayBall(), ":fishing_pole_and_fish: Clay"));
        collections.add(new Collection(tiers.getRawFish2(), exp.getRawFish2(), ":fishing_pole_and_fish: Clown Fish"));
        collections.add(new Collection(tiers.getInkSack(), exp.getInkSack(), ":fishing_pole_and_fish: Ink Sack"));
        collections.add(new Collection(tiers.getWaterLily(), exp.getWaterLily(), ":fishing_pole_and_fish: Lily Pad"));
        collections.add(new Collection(tiers.getRawFish(), exp.getRawFish(), ":fishing_pole_and_fish: Raw Fish"));
        collections.add(new Collection(tiers.getRawFish1(), exp.getRawFish1(), ":fishing_pole_and_fish: Raw Salmon"));
        collections.add(new Collection(tiers.getPrismarineCrystals(), exp.getPrismarineCrystals(), ":fishing_pole_and_fish: Prismarine Crystal"));
        collections.add(new Collection(tiers.getPrismarineShard(), exp.getPrismarineShard(), ":fishing_pole_and_fish: Prismarine Shard"));
        collections.add(new Collection(tiers.getRawFish3(), exp.getRawFish3(), ":fishing_pole_and_fish: Puffer Fish"));
        collections.add(new Collection(tiers.getSponge(), exp.getSponge(), ":fishing_pole_and_fish: Sponge"));

        for (Collection collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }


    //Formats skills for the embed.
    public static String formatSkills(SkyblockSkill skyblockSkill) {

        int stringToPercent = (int) Math.round(Double.parseDouble(skyblockSkill.getProgress()) * 100);

        StringBuilder sb = new StringBuilder();

        if (skyblockSkill.getXpForNext() == 0) {
            sb.append("Skill Maxed.\n").append("Total Experience: ")
                    .append(addCommas(Double.parseDouble(skyblockSkill.getXp())))
                    .append("\n").append("100%\n").append(":green_square:".repeat(Math.max(0, 10)));
            return sb.toString();
        } else {
            sb.append("Experience to next level: ").append(addCommas(skyblockSkill.getXpCurrent())).append(" / ")
                    .append(addCommas(skyblockSkill.getXpForNext())).append("\n").append("Total Experience: ")
                    .append(addCommas(Double.parseDouble(skyblockSkill.getXp()))).append("\n");
            return sb + percentVisualizer(stringToPercent);
        }
    }


    //Converts a percentage to a visualized progress bar.
    public static String percentVisualizer(int stringToPercent) {

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


    //Cycles through a map to gather Slayer data.
    public static String unwrapSlayer(Slayer slayer) {

        StringBuilder sb = new StringBuilder();

        sb.append("Level: ").append(slayer.getClaimedLevels()).append("\n").append("Experience: ")
                .append(addCommas(slayer.getXp())).append("\n");

        slayer.getKillsTier().forEach( (level, kills) ->
                sb.append("Level: ").append(level).append(" - ").append("Kills: ").append(kills).append("\n"));

        return sb.toString();
    }


    //Adds comma separators to large numbers.
    public static String addCommas(double withoutCommas) {

        DecimalFormat decimalFormat = new DecimalFormat(",###");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(withoutCommas);
    }


    //Converts epoch time to date.
    public static String convertMillisToDate(long millis) {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = new Date(millis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return formatter.format(date);
    }


    //Sends a formatted error message.
    public void sendErrorMessage(GuildMessageReceivedEvent event, String errorType, String message, boolean footer) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle(":warning: Error :warning:");
        eb.addField(errorType, message, true);
        if (footer) {
            eb.setFooter("*Player may not have a Skyblock profile or their API settings are disabled.");
        }
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
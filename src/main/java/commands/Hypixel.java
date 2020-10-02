//Hypixel Bot: Sends Hypixel Skyblock player statistics to Discord channels.

package commands;

import commands.HypixelClasses.ComputationalClasses.AddCommas;
import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import commands.HypixelClasses.Collection;
import commands.HypixelClasses.ComputationalClasses.ErrorMessage;
import commands.HypixelClasses.ComputationalClasses.MillisToDate;
import commands.HypixelClasses.Minions;
import commands.HypixelClasses.Skills;
import commands.HypixelClasses.SlayerStats;
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
import zone.nora.slothpixel.skyblock.players.stats.auctions.SkyblockPlayerAuctions;
import zone.nora.slothpixel.skyblock.players.stats.kills.SkyblockPlayerKills;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Hypixel extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] messageSentArray = event.getMessage().getContentRaw().split(" ");

        if (!messageSentArray[0].equalsIgnoreCase("~hypixel")) {
            return;
        }

        if (messageSentArray.length == 1) {
            new ErrorMessage(event, "Invalid Arguments...",
                    "Usage: ~hypixel [collections/minions/guild/skills/slayer/stats]", false).sendErrorMessage();
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
                new SlayerStats(event, hypixel, messageSentArray).getSlayerStats();
                break;
            case "skills":
                new Skills(event, hypixel, messageSentArray).getSkills();
                break;
            case "minions":
                getMinions(event, hypixel, messageSentArray);
                break;
            case "collections":
                getCollections(event, hypixel, messageSentArray);
                break;
            default:
                new ErrorMessage(event, "Invalid Arguments...",
                        "Usage: ~hypixel [collections/minions/guild/stats/slayer/skills]", false)
                        .sendErrorMessage();
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
                    + " \nExperience: " + new AddCommas(guild.getExp()).addCommas(), true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
            event.getChannel().sendMessage(eb.build()).queue();
        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to gather Guild information." , false)
                    .sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    public void getPlayerStatistic(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {

        if (messageSentArray.length == 2) {
            new ErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n" +
                    "Usage: ~hypixel stats <player>", false).sendErrorMessage();
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
                String lastLoginDate = new MillisToDate(player.getLastLogin()).convertMillisToDate();
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
                eb.addField(":moneybag: Money:","Purse: $" + new AddCommas(skyblockPlayer.getCoinPurse()).addCommas(),
                        true);
            } else {
                eb.addField(":moneybag: Money:","Bank: $"
                        + new AddCommas(hypixel.getSkyblockProfile(playerName).getBanking().getBalance()).addCommas()
                        + "\nPurse: $" + new AddCommas(skyblockPlayer.getCoinPurse()).addCommas(), true);
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
                        + new AddCommas(skyblockPlayerAuctions.getGoldSpent()).addCommas()
                        + "\n Money Earned: $" + new AddCommas(skyblockPlayerAuctions.getGoldEarned()).addCommas()
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
                        + new AddCommas(skyblockPlayer.getStats().getTotalKills()).addCommas()
                        + "\nTotal Deaths: " + new AddCommas(skyblockPlayer.getStats().getTotalDeaths()).addCommas()
                        + "\nHighest Crit Damage: " + new AddCommas(skyblockPlayer.getStats().getHighestCriticalDamage()).addCommas(),
                        true);
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
                        + new AddCommas(skyblockPlayer.getStats().getItemsFished().getTotal()).addCommas()
                        + "\nNormal Items: " + new AddCommas(skyblockPlayer.getStats().getItemsFished().getNormal()).addCommas()
                        + "\nTreasures Fished: " + new AddCommas(skyblockPlayer.getStats().getItemsFished().getTreasure()).addCommas()
                        + "\nLarge Treasures Fished: " + new AddCommas(skyblockPlayer.getStats().getItemsFished().getLargeTreasure()).addCommas()
                        + "\nSea Creatures Killed: " + new AddCommas(totalSeaCreatureKills).addCommas(), true);
            } catch (Exception e) {
                eb.addField(":tropical_fish: Items Fished:", "Unable to retrieve fishing data.", true);
            }

            //Calls fairy souls collected.
            eb.addField(":woman_fairy_tone2: Fairy Souls:", skyblockPlayer.getFairySoulsCollected() + " / "
                    + "209", true);

            //Calls date first joined.
            eb.addField(":calendar: Player First Joined:", new MillisToDate(skyblockPlayer.getFirstJoin()).convertMillisToDate(),
                    true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
            event.getChannel().sendMessage(eb.build()).queue();

        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve player data.*", true).sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    public void getMinions(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
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
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve Minions data.*", true)
                    .sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    //Returns the collection stats of the player.
    public void getCollections(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
        if (messageSentArray.length == 2 || messageSentArray.length == 3) {
            new ErrorMessage(event, "Invalid Arguments...",
                    "Usage: ~hypixel collections <player> [combat/farming/fishing/foraging/mining]",true)
                    .sendErrorMessage();
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
                    new ErrorMessage(event, "Invalid Arguments...",
                            "Usage: ~hypixel collections <player> [combat/farming/fishing/foraging/mining]",
                            true).sendErrorMessage();
                    break;
            }
        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve Minions data.*", true).sendErrorMessage();
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
}
package fr.Hygon.TowerBow.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCursor;
import fr.Hygon.TowerBow.Main;
import fr.Hygon.Yokura.MongoUtils;
import fr.Hygon.Yokura.Yokura;
import net.kyori.adventure.text.Component;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerStatsManager implements Listener {
    private static final HashMap<UUID, Integer> playersKillStreak = new HashMap<>();

    private static final HashMap<UUID, StopWatch> playersTimeAlive = new HashMap<>();
    private static BukkitTask task = null;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        System.out.println("ENREGISTRÉ");
        StopWatch stopWatch = new StopWatch();
        playersTimeAlive.put(player.getUniqueId(), stopWatch);
        stopWatch.start();

        if(!MongoUtils.documentExists("towerbow", player.getUniqueId().toString())) {
            Document document = new Document("_id", player.getUniqueId().toString())
                    .append("kills", 0)
                    .append("deaths", 0)
                    .append("killstreak", 0)
                    .append("player_name", player.getName());
            Yokura.getMongoDatabase().getCollection("towerbow").insertOne(document);
        } else if(!MongoUtils.getString("towerbow", player.getUniqueId().toString(), "player_name").equals(player.getName())) {
            MongoUtils.updateValue("towerbow", player.getUniqueId().toString(), "player_name", player.getName());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        playersKillStreak.remove(player.getUniqueId());

        CustomScoreboard.setBestThreeKillStreaks(getBestThreeKillStreaks());
        Bukkit.getOnlinePlayers().forEach(TowerBowScoreboard::updateScoreboard);

        if(playersTimeAlive.get(player.getUniqueId()) != null) {
            playersTimeAlive.get(player.getUniqueId()).stop();
            playersTimeAlive.remove(player.getUniqueId());
        }
    }

    public static int getKillStreak(Player player) {
        if (playersKillStreak.containsKey(player.getUniqueId())) {
            return playersKillStreak.get(player.getUniqueId());
        } else {
            playersKillStreak.put(player.getUniqueId(), 0);
            return 0;
        }
    }

    public static void incrementKillStreak(Player player) {
        if (playersKillStreak.containsKey(player.getUniqueId())) {
            playersKillStreak.put(player.getUniqueId(), playersKillStreak.get(player.getUniqueId()) + 1);
        } else {
            playersKillStreak.put(player.getUniqueId(), 1);
        }

        player.setLevel(getKillStreak(player));

        TowerBowScoreboard.getScoreboard(player).setKillStreak(getKillStreak(player));
        CustomScoreboard.setBestThreeKillStreaks(getBestThreeKillStreaks());
        Bukkit.getOnlinePlayers().forEach(TowerBowScoreboard::updateScoreboard);

        if(getBestKillStreak(player) < getKillStreak(player)) {
            MongoUtils.increment("towerbow", player.getUniqueId().toString(), "killstreak", 1);
            CustomScoreboard.setBestThreeKillStreaks(getBestThreeKillStreaks());
            TowerBowScoreboard.getScoreboard(player).setTopKillStreak(getKillStreak(player));
            Bukkit.getOnlinePlayers().forEach(players -> TowerBowScoreboard.updateScoreboard(player));
        }

        MongoUtils.increment("towerbow", player.getUniqueId().toString(), "kills", 1);
    }

    public static void resetKillStreak(Player player) {
        playersKillStreak.put(player.getUniqueId(), 0);

        TowerBowScoreboard.getScoreboard(player).setKillStreak(getKillStreak(player));
        TowerBowScoreboard.updateScoreboard(player);

        CustomScoreboard.setBestThreeKillStreaks(getBestThreeKillStreaks());
        Bukkit.getOnlinePlayers().forEach(TowerBowScoreboard::updateScoreboard);
    }

    public static int getBestKillStreak(Player player) {
        int killStreak = MongoUtils.getInt("towerbow", player.getUniqueId().toString(), "killstreak");
        return killStreak > -1 ? killStreak : 0;
    }

    public static int getKills(Player player) {
        int kills = MongoUtils.getInt("towerbow", player.getUniqueId().toString(), "kills");
        return kills > -1 ? kills : 0;
    }

    public static ImmutablePair<String, Integer>[] getBestThreeKillStreaks() {
        ImmutablePair<String, Integer>[] bestThreeKillStreaks = new ImmutablePair[3];
        // https://stackoverflow.com/questions/62077736/how-to-get-the-3-highest-values-in-a-hashmap/62078310#62078310
        List<UUID> bestThreePlayersUUID = playersKillStreak.entrySet().stream().sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

        int arrayPos = 0;
        while(arrayPos != 3) {
            System.out.println(bestThreePlayersUUID.size() + " " + arrayPos);
            if(bestThreePlayersUUID.size() <= arrayPos) {
                bestThreeKillStreaks[arrayPos] = new ImmutablePair<>(null, 0);
            } else {
                UUID playerUUID = bestThreePlayersUUID.get(arrayPos);
                bestThreeKillStreaks[arrayPos] = new ImmutablePair<>(Bukkit.getPlayer(playerUUID).getName(), playersKillStreak.get(playerUUID));
            }
            arrayPos++;
        }

        return bestThreeKillStreaks;
    }

    public static int getDeaths(Player player) {
        int deaths = MongoUtils.getInt("towerbow", player.getUniqueId().toString(), "deaths");
        return deaths > -1 ? deaths : 0;
    }

    public static void runTimerTask() {
        if(task == null) {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(playersTimeAlive.get(player.getUniqueId()) != null) {
                            player.sendActionBar(Component.text("Dernière Mort §7» §e" + playersTimeAlive.get(player.getUniqueId()).getHumanHour()));
                        }
                    });
                }
            }.runTaskTimer(Main.getPlugin(), 0, 20);
        }
    }

    public static void resetTimer(Player player) {
        playersTimeAlive.get(player.getUniqueId()).stop();
        playersTimeAlive.get(player.getUniqueId()).start();
    }
}

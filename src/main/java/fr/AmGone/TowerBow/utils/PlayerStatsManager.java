package fr.AmGone.TowerBow.utils;

import fr.AmGone.TowerBow.Main;
import fr.AmGone.TowerBow.events.PlayerDamageManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    private static final HashMap<UUID, Integer> playersKills = new HashMap<>();
    private static final HashMap<UUID, Integer> playersDeaths = new HashMap<>();

    private static BukkitTask task = null;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        StopWatch stopWatch = new StopWatch();
        playersTimeAlive.put(player.getUniqueId(), stopWatch);
        playersKillStreak.put(player.getUniqueId(), 0);
        stopWatch.start();
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

        checkIfKillAndDeathExists(player);
        playersKills.put(player.getUniqueId(), playersKills.get(player.getUniqueId()) + 1);
        //MongoUtils.increment("towerbow", player.getUniqueId().toString(), "kills", 1);
    }

    public static void resetKillStreak(Player player) {
        playersKillStreak.put(player.getUniqueId(), 0);

        TowerBowScoreboard.getScoreboard(player).setKillStreak(getKillStreak(player));
        TowerBowScoreboard.updateScoreboard(player);

        CustomScoreboard.setBestThreeKillStreaks(getBestThreeKillStreaks());
        Bukkit.getOnlinePlayers().forEach(TowerBowScoreboard::updateScoreboard);
    }

    public static int getKills(Player player) {
        checkIfKillAndDeathExists(player);
        return playersKills.get(player.getUniqueId());
    }

    public static Pair<String, Integer>[] getBestThreeKillStreaks() {
        Pair<String, Integer>[] bestThreeKillStreaks = new Pair[3];
        // https://stackoverflow.com/questions/62077736/how-to-get-the-3-highest-values-in-a-hashmap/62078310#62078310
        List<UUID> bestThreePlayersUUID = playersKillStreak.entrySet().stream().sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());

        int arrayPos = 0;
        while(arrayPos != 3) {
            if(bestThreePlayersUUID.size() <= arrayPos) {
                bestThreeKillStreaks[arrayPos] = new Pair<>(null, 0);
            } else {
                UUID playerUUID = bestThreePlayersUUID.get(arrayPos);
                bestThreeKillStreaks[arrayPos] = new Pair<>(Bukkit.getPlayer(playerUUID).getName(), playersKillStreak.get(playerUUID));
            }
            arrayPos++;
        }

        return bestThreeKillStreaks;
    }

    public static void incrementDeath(Player player) {
        playersDeaths.put(player.getUniqueId(), getDeaths(player) + 1);
    }

    public static int getDeaths(Player player) {
        checkIfKillAndDeathExists(player);
        return playersDeaths.get(player.getUniqueId());
    }

    public static void checkIfKillAndDeathExists(Player player) {
        if(!playersKills.containsKey(player.getUniqueId())) {
            playersKills.put(player.getUniqueId(), 0);
        }

        if(!playersDeaths.containsKey(player.getUniqueId())) {
            playersDeaths.put(player.getUniqueId(), 0);
        }
    }

    public static void runTimerTask() {
        if(task == null) {
            task = new BukkitRunnable() {
                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        if(!PlayerDamageManager.isVulnerable(player)) {
                            player.sendActionBar(Component.text("Invincibilité")
                                    .append(Component.text(" » ").color(NamedTextColor.GRAY))
                                    .append(Component.text(StopWatch.getHumanTime(PlayerDamageManager.getPlayerInvincibleTime(player))).color(TextColor.color(0, 220, 60))));
                        } else if(playersTimeAlive.get(player.getUniqueId()) != null) {
                            player.sendActionBar(
                                    Component.text("Dernière Mort")
                                    .append(Component.text(" » ").color(NamedTextColor.GRAY))
                                    .append(Component.text(StopWatch.getHumanTime(playersTimeAlive.get(player.getUniqueId()).getElapsedTime())).color(NamedTextColor.YELLOW)));
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

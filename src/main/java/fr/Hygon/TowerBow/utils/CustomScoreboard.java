package fr.Hygon.TowerBow.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.tuple.ImmutablePair;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class CustomScoreboard {
    private static ImmutablePair<String, Integer>[] bestThreeKillStreaks = null;

    private Scoreboard scoreboard;
    private Objective objective;

    private final Player player;

    private int kills;
    private int deaths;
    private int killStreak;
    private int topKillStreak;

    public CustomScoreboard(Player player) {
        this.player = player;

        kills = PlayerStatsManager.getKills(player);
        deaths = PlayerStatsManager.getDeaths(player);
        killStreak = 0;
        topKillStreak = PlayerStatsManager.getBestKillStreak(player);

        updateScoreboard();
    }

    public static void setBestThreeKillStreaks(ImmutablePair<String, Integer>[] bestThreeKillStreaks) {
        CustomScoreboard.bestThreeKillStreaks = bestThreeKillStreaks;
    }

    public void addKill() {
        this.kills = this.kills + 1;
    }

    public void addDeaths() {
        this.deaths = this.deaths + 1;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    public void setTopKillStreak(int topKillStreak) {
        this.topKillStreak = topKillStreak;
    }

    public void updateScoreboard() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();

        objective = scoreboard.registerNewObjective("Hygon", "dummy", Component.text("•").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("Tower Bow").color(TextColor.color(240, 221, 19))
                .append(Component.text(" •").color(NamedTextColor.DARK_GRAY))));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score bar1 = objective.getScore("§8-----------");
        bar1.setScore(11);

        Score yourStats = objective.getScore("§8Your Stats:" + kills);
        yourStats.setScore(10);

        Score killsScore = objective.getScore("Kills §8» §a" + kills);
        killsScore.setScore(9);

        Score deathsScores = objective.getScore("Morts §8» §a" + deaths);
        deathsScores.setScore(8);

        Score killStreakScore = objective.getScore("Killstreak §8» §a" + killStreak);
        killStreakScore.setScore(7);

        Score topKillStreakScore = objective.getScore("Best Streak §8» §a" + topKillStreak);
        topKillStreakScore.setScore(6);

        Score empty1 = objective.getScore("§1");
        empty1.setScore(5);

        Score topKill = objective.getScore("§8Top Streaks:");
        topKill.setScore(4);

        if(bestThreeKillStreaks == null) {
            System.out.println("NULL");
            bestThreeKillStreaks = PlayerStatsManager.getBestThreeKillStreaks();
        }

        int scorePos = 3;
        int arrayPos = 0;
        while (arrayPos != 3) {
            Score bestKillStreakScore;
            if(bestThreeKillStreaks[arrayPos].getLeft() == null) {
                bestKillStreakScore = objective.getScore("§8• §4Aucun");
            } else {
                bestKillStreakScore = objective.getScore("§8• §f" + bestThreeKillStreaks[arrayPos].getLeft() + " §8» §e" + bestThreeKillStreaks[arrayPos].getRight());
            }
            bestKillStreakScore.setScore(scorePos);

            arrayPos++;
            scorePos--;
        }

        Score bar2 = objective.getScore("§8-----------");
        bar2.setScore(0);

        player.setScoreboard(scoreboard);
    }
}

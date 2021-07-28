package fr.Hygon.TowerBow.utils;

import net.kyori.adventure.text.Component;
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

        objective = scoreboard.registerNewObjective("Hygon", "dummy", Component.text("TowerBow"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score killsScore = objective.getScore("Kills: " + kills);
        killsScore.setScore(12);

        Score deathsScores = objective.getScore("Morts: " + deaths);
        deathsScores.setScore(11);

        Score empty1 = objective.getScore("§1");
        empty1.setScore(10);

        Score killStreakScore = objective.getScore("KillStreak: " + killStreak);
        killStreakScore.setScore(9);

        Score topKillStreakScore = objective.getScore("Best KillStreak: " + topKillStreak);
        topKillStreakScore.setScore(8);

        Score empty2 = objective.getScore("§2");
        empty2.setScore(7);

        if(bestThreeKillStreaks == null) {
            System.out.println("NULL");
            bestThreeKillStreaks = PlayerStatsManager.getBestThreeKillStreaks();
        }

        int scorePos = 6;
        int arrayPos = 0;
        while (arrayPos != 3) {
            Score bestKillStreakScore;
            if(bestThreeKillStreaks[arrayPos].getLeft() == null) {
                bestKillStreakScore = objective.getScore("• Aucun");
            } else {
                bestKillStreakScore = objective.getScore("• " + bestThreeKillStreaks[arrayPos].getLeft() + " : " + bestThreeKillStreaks[arrayPos].getRight());
            }
            bestKillStreakScore.setScore(scorePos);

            arrayPos++;
            scorePos--;
        }

        player.setScoreboard(scoreboard);
    }
}

package fr.Hygon.TowerBow;

import fr.Hygon.TowerBow.events.BlockPlace;
import fr.Hygon.TowerBow.events.PlayerDamageManager;
import fr.Hygon.TowerBow.events.OnJoin;
import fr.Hygon.TowerBow.events.PlayerUtilsEvents;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {
    private static Plugin plugin = null;

    @Override
    public void onEnable() {
        plugin = this;
        registerEvents();

        startDamageBelowY160Task();
        BlockPlace.startTask();
    }

    @Override
    public void onDisable() {

    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new OnJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageManager(), this);
        getServer().getPluginManager().registerEvents(new PlayerUtilsEvents(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);
    }

    public void startDamageBelowY160Task() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(PlayerDamageManager.isVulnerable(player)) {
                        if(player.getLocation().getY() <= 170 && player.getGameMode() == GameMode.SURVIVAL && player.getHealth() > 0) {
                            player.damage(2);
                            player.sendTitle("§cMontez vite!", "§7Vous êtes entrain de suffoquer.", 0, 20, 10);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    public static Plugin getPlugin() {
        return plugin;
    }
}

package fr.Hygon.TowerBow.events;

import fr.Hygon.TowerBow.Main;
import fr.Hygon.TowerBow.items.ItemsList;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerDamageManager implements Listener {
    private static final HashMap<UUID, Long> invinciblePlayers = new HashMap<>();
    private static final HashMap<UUID, Integer> playersKillStreak = new HashMap<>();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player deadPlayer) {
            Player killer = deadPlayer.getKiller();

            if (killer != null && killer != deadPlayer) {
                killer.getInventory().addItem(ItemsList.GAPPLE.getPreparedItemStack());

                killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 2, false, false, true));

                final Title.Times times = Title.Times.of(Duration.ofMillis(5000), Duration.ofMillis(15000), Duration.ofMillis(5000));
                final Title title = Title.title((Component.text("")), Component.text("KILL!").color(TextColor.color(88, 235, 52)));
                killer.showTitle(title);

            }

            int randomX = ThreadLocalRandom.current().nextInt(25, 175);
            int randomZ = ThreadLocalRandom.current().nextInt(25, 175);

            Firework spawnFirework = (Firework) deadPlayer.getWorld().spawnEntity(deadPlayer.getLocation(), EntityType.FIREWORK);
            FireworkMeta fireworkMeta = spawnFirework.getFireworkMeta();

            fireworkMeta.addEffect(FireworkEffect.builder().withColor(Color.YELLOW).flicker(true).build());
            spawnFirework.setFireworkMeta(fireworkMeta);

            new BukkitRunnable() {
                @Override
                public void run() {
                    spawnFirework.detonate();
                }
            }.runTaskLater(Main.getPlugin(), 10);

            invinciblePlayers.put(deadPlayer.getUniqueId(), System.currentTimeMillis());

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (isVulnerable(deadPlayer)) {
                        cancel();
                    }
                    deadPlayer.getWorld().spawnParticle(Particle.FLAME, deadPlayer.getLocation().getX(), (deadPlayer.getLocation().getY() + 0.5),
                            deadPlayer.getLocation().getZ(), 15, 0.25, 0.8, 0.25, 0.02);
                }
            }.runTaskTimer(Main.getPlugin(), 0, 10);

            new BukkitRunnable() {
                @Override
                public void run() {
                    deadPlayer.spigot().respawn();
                    deadPlayer.teleport(new Location(deadPlayer.getWorld(), randomX, 150, randomZ));
                    deadPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 2, false, false, true));

                    deadPlayer.setLevel(0);
                    deadPlayer.getInventory().addItem(ItemsList.GAPPLE.getPreparedItemStack());
                    deadPlayer.removePotionEffect(PotionEffectType.ABSORPTION);
                }
            }.runTaskLater(Main.getPlugin(), 1); //Il faut le delay d'un tick sinon le client peut avoir un bug graphique (death menu bugué)

            if (killer != null) {
                if (getKillStreak(killer) == 3 || getKillStreak(killer) == 5 || getKillStreak(killer) == 10 || getKillStreak(killer) == 15 || getKillStreak(killer) == 20 ||
                        getKillStreak(killer) == 25 || getKillStreak(killer) == 50) {
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.sendMessage(Component.text("» ").color(TextColor.color(150, 150, 150))
                                .append(Component.text(killer.getName()).color(TextColor.color(237, 133, 14)))
                                .append(Component.text(" a fait une série de ").color(TextColor.color(255, 255, 65)))
                                .append(Component.text(getKillStreak(killer)).color(TextColor.color(237, 133, 14)))
                                .append(Component.text(" kills!").color(TextColor.color(255, 255, 65))));
                    }
                }
            }

        }
    }

    public static void registerInvinciblePlayer(Player player) {
        invinciblePlayers.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public static boolean isVulnerable(Player player) {
        if (invinciblePlayers.containsKey(player.getUniqueId())) {
            return (System.currentTimeMillis() - invinciblePlayers.get(player.getUniqueId())) > 20000;
        } else {
            return true;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && !isVulnerable((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player && !isVulnerable((Player) damager)) {
            event.setCancelled(true);
        }

        if (event.getEntity() instanceof Player && !isVulnerable((Player) event.getEntity())) {
            event.setCancelled(true);
        }

        if (damager instanceof Firework) {
            event.setCancelled(true);
        }

        if (damager instanceof Projectile && event.getEntity() instanceof Player) {
            ProjectileSource shooter = ((Projectile) damager).getShooter();

            if (!(shooter instanceof Player)) {
                return;
            }

            if (!isVulnerable((Player) event.getEntity()) || !isVulnerable((Player) shooter)) {
                event.setCancelled(true);
            }
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

    private static void incrementKillStreak(Player player) {
        if (playersKillStreak.containsKey(player.getUniqueId())) {
            playersKillStreak.put(player.getUniqueId(), playersKillStreak.get(player.getUniqueId()) + 1);
        } else {
            playersKillStreak.put(player.getUniqueId(), 1);
        }

        player.setLevel(getKillStreak(player));
    }

    private static void resetKillStreak(Player player) {
        playersKillStreak.put(player.getUniqueId(), 0);
    }

}

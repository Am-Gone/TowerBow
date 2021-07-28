package fr.Hygon.TowerBow.events;

import fr.Hygon.TowerBow.Main;
import fr.Hygon.TowerBow.items.ItemsList;
import fr.Hygon.TowerBow.utils.PlayerStatsManager;
import fr.Hygon.TowerBow.utils.TowerBowScoreboard;
import fr.Hygon.Yokura.MongoUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import static fr.Hygon.TowerBow.utils.PlayerStatsManager.*;

public class PlayerDamageManager implements Listener {
    private static final HashMap<UUID, Long> invinciblePlayers = new HashMap<>();

    private static final DecimalFormat healthFormat = new DecimalFormat("#");

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player deadPlayer = event.getEntity();
        Player killer = deadPlayer.getKiller();

        if (killer != null) {
            event.deathMessage(Component.text("» ").color(TextColor.color(150, 150, 150))
                    .append(Component.text(deadPlayer.getName()).color(TextColor.color(255, 163, 33)))
                    .append(Component.text(" a été tué par ").color(TextColor.color(255, 255, 65)))
                    .append(Component.text(killer.getName()).color(TextColor.color(255, 163, 33)))
                    .append(Component.text(".").color(TextColor.color(255, 255, 65)))
                    .append(Component.text(" (" + healthFormat.format(killer.getHealth()) + "❤)").color(TextColor.color(155, 155, 155))));
        } else {
            event.deathMessage(Component.text("» ").color(TextColor.color(150, 150, 150))
                    .append(Component.text(deadPlayer.getName()).color(TextColor.color(255, 163, 33)))
                    .append(Component.text(" est mort.").color(TextColor.color(255, 255, 65))));
        }

        resetKillStreak(deadPlayer);
        PlayerStatsManager.resetTimer(deadPlayer);
        if (killer != null && killer != deadPlayer) {
            incrementKillStreak(killer);
            TowerBowScoreboard.getScoreboard(killer).addKill();
            TowerBowScoreboard.updateScoreboard(killer);

            killer.getInventory().addItem(ItemsList.GAPPLE.getPreparedItemStack());

            killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 2, false, false, true));

            final Title.Times times = Title.Times.of(Duration.ofMillis(100), Duration.ofMillis(750), Duration.ofMillis(100));
            final Title title = Title.title((Component.text("")), Component.text("KILL!").color(TextColor.color(88, 235, 52)).decoration(TextDecoration.BOLD, true), times);
            killer.showTitle(title);

            if (getKillStreak(killer) == 3 || getKillStreak(killer) == 5 || getKillStreak(killer) == 10 || getKillStreak(killer) == 15 || getKillStreak(killer) == 20 ||
                    getKillStreak(killer) == 25 || getKillStreak(killer) == 50) {
                for (Player players : Bukkit.getOnlinePlayers()) {
                    players.sendMessage(Component.text("» ").color(TextColor.color(150, 150, 150))
                            .append(Component.text(killer.getName()).color(TextColor.color(255, 163, 33)))
                            .append(Component.text(" a fait une série de ").color(TextColor.color(255, 255, 65)))
                            .append(Component.text(getKillStreak(killer)).color(TextColor.color(255, 163, 33)))
                            .append(Component.text(" kills!").color(TextColor.color(255, 255, 65))));
                }
            }
        }

        int randomX = ThreadLocalRandom.current().nextInt(25, 175 - 1);
        int randomZ = ThreadLocalRandom.current().nextInt(25, 175 - 1);

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
        TowerBowScoreboard.getScoreboard(deadPlayer).addDeaths();
        TowerBowScoreboard.updateScoreboard(deadPlayer);
        MongoUtils.increment("towerbow", deadPlayer.getUniqueId().toString(), "deaths", 1);

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
                deadPlayer.teleport(new Location(deadPlayer.getWorld(), randomX, 151, randomZ));
                deadPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 2, false, false, true));
                deadPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2, false, false, true));

                //TODO récupérer le temps pendant lequel tu es invincible

                deadPlayer.setLevel(0);

                deadPlayer.getInventory().clear();

                deadPlayer.getEquipment().setHelmet(ItemsList.IRON_HELMET.getPreparedItemStack());
                deadPlayer.getEquipment().setChestplate(ItemsList.IRON_CHEST.getPreparedItemStack());
                deadPlayer.getEquipment().setLeggings(ItemsList.IRON_LEGGINGS.getPreparedItemStack());
                deadPlayer.getEquipment().setBoots(ItemsList.IRON_BOOTS.getPreparedItemStack());

                deadPlayer.getInventory().setItem(0, ItemsList.PICKAXE.getPreparedItemStack());
                deadPlayer.getInventory().setItem(1, ItemsList.BOW.getPreparedItemStack());
                deadPlayer.getInventory().setItem(2, ItemsList.GAPPLE.getPreparedItemStack());
                deadPlayer.getInventory().setItem(9, new ItemStack(Material.ARROW));
                deadPlayer.getInventory().setItem(40, ItemsList.COBBLESTONE.getPreparedItemStack());

                deadPlayer.removePotionEffect(PotionEffectType.ABSORPTION);
            }
        }.runTaskLater(Main.getPlugin(), 1); //Il faut le delay d'un tick sinon le client peut avoir un bug graphique (death menu bugué)
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
}

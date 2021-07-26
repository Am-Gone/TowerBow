package fr.Hygon.TowerBow.events;

import fr.Hygon.TowerBow.Main;
import fr.Hygon.TowerBow.items.ItemsList;
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
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerDamageManager implements Listener {
    private static final HashMap<UUID, Long> invinciblePlayers = new HashMap<>();

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(event.getEntity() instanceof Player deadPlayer) {
            Player killer = deadPlayer.getKiller();

            if(killer != null) {
                killer.getInventory().addItem(ItemsList.GAPPLE.getPreparedItemStack());

                killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                killer.setLevel(killer.getLevel() + 1);
                killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 2, false, true, true));

            }

            int randomX = ThreadLocalRandom.current().nextInt(25, 175);
            int randomZ = ThreadLocalRandom.current().nextInt(25, 175);

            Firework spawnFirework = (Firework) deadPlayer.getWorld().spawnEntity(deadPlayer.getLocation(), EntityType.FIREWORK);
            FireworkMeta fireworkMeta = spawnFirework.getFireworkMeta();

            //fireworkMeta.setPower(2);
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
                    if(isVulnerable(deadPlayer)) {
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
                    deadPlayer.getInventory().setItem(2, ItemsList.GAPPLE.getPreparedItemStack());
                }
            }.runTaskLater(Main.getPlugin(), 1); //Il faut le delay d'un tick sinon le client peut avoir un bug graphique (death menu bugué)

        }
    }

    public static void registerInvinciblePlayer(Player player) {
        invinciblePlayers.put(player.getUniqueId(), System.currentTimeMillis());
    }
    public static boolean isVulnerable(Player player) {
        if(invinciblePlayers.containsKey(player.getUniqueId())) {
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
        if(damager instanceof Player && !isVulnerable((Player) damager)) {
            event.setCancelled(true);
        }

        if(event.getEntity() instanceof Player && !isVulnerable((Player) event.getEntity())) {
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

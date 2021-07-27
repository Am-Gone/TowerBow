package fr.Hygon.TowerBow.events;

import fr.Hygon.TowerBow.Main;
import fr.Hygon.TowerBow.items.ItemsList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.concurrent.ThreadLocalRandom;

public class OnJoin implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage("§7» §6" + player.getName() + "§e connected.");

        player.setFoodLevel(20);
        player.setHealth(20);
        player.setLevel(0);
        player.getInventory().clear();
        player.removePotionEffect(PotionEffectType.ABSORPTION);


        if(player.getEquipment() != null) {
            player.getEquipment().setHelmet(ItemsList.IRON_HELMET.getPreparedItemStack());
            player.getEquipment().setChestplate(ItemsList.IRON_CHEST.getPreparedItemStack());
            player.getEquipment().setLeggings(ItemsList.IRON_LEGGINGS.getPreparedItemStack());
            player.getEquipment().setBoots(ItemsList.IRON_BOOTS.getPreparedItemStack());

            player.getInventory().setItem(0, ItemsList.PICKAXE.getPreparedItemStack());
            player.getInventory().setItem(1, ItemsList.BOW.getPreparedItemStack());
            player.getInventory().setItem(2, ItemsList.GAPPLE.getPreparedItemStack());
            player.getInventory().setItem(9, new ItemStack(Material.ARROW));
            player.getInventory().setItem(40, ItemsList.COBBLESTONE.getPreparedItemStack());
        }

        int randomX = ThreadLocalRandom.current().nextInt(25, 175);
        int randomZ = ThreadLocalRandom.current().nextInt(25, 175);
        player.teleport(new Location(player.getWorld(), randomX, 150, randomZ));

        PlayerDamageManager.registerInvinciblePlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 2, false, false, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2, false, false, true));

        new BukkitRunnable() {
            @Override
            public void run() {
                if(PlayerDamageManager.isVulnerable(player)) {
                    cancel();
                }
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().getX(), (player.getLocation().getY() + 0.5),
                        player.getLocation().getZ(), 15, 0.25, 0.8, 0.25, 0.02);
            }
        }.runTaskTimer(Main.getPlugin(), 0, 10);
    }
}

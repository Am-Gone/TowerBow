package fr.Hygon.TowerBow.events;

import fr.Hygon.TowerBow.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerUtilsEvents implements Listener { // Une classe pour tous les petits events, ça évite de faire une classe par event (inutile)
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getItemInHand().setAmount(32);
                event.getPlayer().updateInventory(); //#setAmount ne fais des changements que du côté du serveur, il faut donc prévenir le joueur que
                                                // son inventaire a été mis à jour
            }
        }.runTaskLater(Main.getPlugin(), 1);
    }

    @EventHandler
    public void onFoodEvent(FoodLevelChangeEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block brokenBlock = event.getBlock();
        if (brokenBlock.getType() == Material.COBBLESTONE || brokenBlock.getType() == Material.MOSSY_COBBLESTONE) {
            event.setDropItems(false);
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("§8» §6" + player.getName() + "§e disconnected.");
    }
}

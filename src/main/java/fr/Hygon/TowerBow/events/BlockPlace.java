package fr.Hygon.TowerBow.events;

import fr.Hygon.TowerBow.Main;
import net.minecraft.core.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BlockPlace implements Listener {
    private static final HashMap<Long, int[]> blocks = new HashMap<>();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        int[] location = new int[3];
        location[0] = blockLocation.getBlockX();
        location[1] = blockLocation.getBlockY();
        location[2] = blockLocation.getBlockZ();

        blocks.put(System.currentTimeMillis() + 150000, location); //Le temps avant qu'on fasse dispawn le block
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        int[] location = new int[3];
        location[0] = blockLocation.getBlockX();
        location[1] = blockLocation.getBlockY();
        location[2] = blockLocation.getBlockZ();

        blocks.entrySet().removeIf(entry -> Arrays.equals(location, entry.getValue()));
    }

    public static void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<Long, int[]>> iter = blocks.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Long, int[]> entry = iter.next();

                    int[] coords = entry.getValue();
                    Block block = Bukkit.getWorld("world").getBlockAt(coords[0], coords[1], coords[2]);

                    if(entry.getKey() <= System.currentTimeMillis() + 5000) {
                        block.setType(Material.MOSSY_COBBLESTONE);
                        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_MOSS_PLACE, 1, 1);
                    }

                    if(entry.getKey() <= System.currentTimeMillis()) {
                        ((CraftWorld) block.getWorld()).getHandle().destroyBlock(new BlockPos(block.getX(), block.getY(), block.getZ()), false);
                        iter.remove();
                    }
                }
            }
        }.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}

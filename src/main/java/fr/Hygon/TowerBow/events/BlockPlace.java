package fr.Hygon.TowerBow.events;

import fr.Hygon.TowerBow.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private static HashMap<Long, int[]> blocks = new HashMap<>();

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Location blockLocation = event.getBlock().getLocation();
        int[] location = new int[3];
        location[0] = blockLocation.getBlockX();
        location[1] = blockLocation.getBlockY();
        location[2] = blockLocation.getBlockZ();

        blocks.put(System.currentTimeMillis() + 600000, location); //Le temps avant qu'on fasse dispawn le block
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        long startTime = System.currentTimeMillis();
        Location blockLocation = event.getBlock().getLocation();
        int[] location = new int[3];
        location[0] = blockLocation.getBlockX();
        location[1] = blockLocation.getBlockY();
        location[2] = blockLocation.getBlockZ();

        blocks.entrySet().removeIf(entry -> Arrays.equals(location, entry.getValue()));
        System.out.println("break: " + (System.currentTimeMillis() - startTime));
    }

    public static void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long startTime = System.currentTimeMillis();

                Iterator<Map.Entry<Long, int[]>> iter = blocks.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<Long, int[]> entry = iter.next();
                    if(entry.getKey() <= System.currentTimeMillis()) {
                        int[] coords = entry.getValue();
                        Location location = new Location(Bukkit.getWorld("world"), coords[0], coords[1], coords[2]);
                        location.getBlock().setType(Material.AIR);
                        iter.remove();
                    }
                }
                System.out.println("remover: " + (System.currentTimeMillis() - startTime));
            }
        }.runTaskTimer(Main.getPlugin(), 0, 1);
    }
}

package fr.Hygon.TowerBow.events;

import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.text.DecimalFormat;

public class OnHit implements Listener {
    DecimalFormat decimalFormat = new DecimalFormat("#.#");

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if(event.getEntity().getShooter() instanceof Player player && event.getHitEntity() != null) {

            double distance = player.getLocation().distance(event.getHitEntity().getLocation());
            if(distance >= 50) {
                player.sendMessage(Component.text("LONG SHOT! (" + decimalFormat.format(distance) + "m)"));
                addHealth(player, 2);
            }
        }
    }

    public static void addHealth(LivingEntity entity, double health) {
        entity.setHealth(Math.min(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), entity.getHealth() + health));
    }
}

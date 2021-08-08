package fr.AmGone.TowerBow.events;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
        if(event.getEntity().getShooter() instanceof Player && event.getHitEntity() != null) {
            Player player = (Player) event.getEntity().getShooter();
            double distance = player.getLocation().distance(event.getHitEntity().getLocation());
            if(distance >= 50) {
                for(Player players : Bukkit.getOnlinePlayers()) {
                    players.sendMessage(Component.text("» ").color(TextColor.color(NamedTextColor.GRAY))
                            .append(Component.text("LONG SHOT! ").color(TextColor.color(245, 143, 27)).decoration(TextDecoration.BOLD, true))
                            .append(Component.text(player.getName()).color(NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, false))
                            .append(Component.text(" a fait un long shot de ").color(NamedTextColor.GRAY)
                            .append(Component.text(decimalFormat.format(distance) + " blocks").color(TextColor.color(255, 66, 66)))
                            .append(Component.text(" !").color(NamedTextColor.GRAY))));
                }
                addHealth(player, 4);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
            }
        }
    }

    public static void addHealth(LivingEntity entity, double health) {
        entity.setHealth(Math.min(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), entity.getHealth() + health));
    }
}

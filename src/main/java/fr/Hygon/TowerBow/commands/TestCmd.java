package fr.Hygon.TowerBow.commands;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockBreakAckPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class TestCmd implements CommandExecutor {
    private static Location blockLocation = new Location(Bukkit.getWorld("world"), 86, 151, 129);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        ClientboundBlockDestructionPacket clientonsBlockDestructionPacket =
                new ClientboundBlockDestructionPacket(Bukkit.getOnlinePlayers().stream().findFirst().get().getEntityId(), new BlockPos(86, 151, 129),
                        Integer.parseInt(args[0]));

        for(Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().connection.send(clientonsBlockDestructionPacket);
        }
        return false;
    }
}

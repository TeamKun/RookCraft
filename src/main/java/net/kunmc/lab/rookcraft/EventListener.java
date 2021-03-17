package net.kunmc.lab.rookcraft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        RookCraft.getINSTANCE().playerJoinEvent(e.getPlayer());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        RookCraft.getINSTANCE().playerJoinEvent(e.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        RookCraft.getINSTANCE().playerMoveEvent(e.getPlayer(),e.getFrom(),e.getTo());
    }
}

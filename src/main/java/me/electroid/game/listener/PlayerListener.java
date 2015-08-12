package me.electroid.game.listener;

import me.electroid.game.GameLoader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by ElectroidFilms on 7/27/15.
 */
public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        if (GameLoader.get().hasGameRunning()) {
            GameLoader.get().getCurrentGame().addParticipant(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        GameLoader.get().getCurrentGame().removeParticipant(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onKick(PlayerKickEvent event) {
        GameLoader.get().getCurrentGame().removeParticipant(event.getPlayer());
    }

}

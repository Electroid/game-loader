package me.electroid.game;

import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import me.electroid.game.command.GameCommands;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockImage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;


/**
 * Created by ElectroidFilms on 6/15/15.
 */
public class GameLoader extends JavaPlugin {

    private static GameLoader instance;
    private Game currentGame;
    private CommandsManager commands;
    private CommandsManagerRegistration commandsRegistry;

    /**
     * Get the plugin instance statically.
     * @return The plugin instance.
     */
    public static GameLoader get() {
        return instance;
    }

    /**
     * Check if a game is currently running.
     * @return Whether a game is currently running.
     */
    public final boolean hasGameRunning() {
        return this.currentGame != null;
    }

    /**
     * Get the current game running.
     * @return The current game.
     */
    public final Game getCurrentGame() {
        return this.currentGame;
    }

    /**
     * Change the currently running game.
     * @param game The new game to run.
     */
    public void addGame(Game game) {
        endGame(true);
        currentGame = game;
        for (Player player : Bukkit.getOnlinePlayers()) {
            game.addParticipant(player);
        }
    }

    /**
     * End the current game.
     * @param unload Whether to unload the world.
     */
    public void endGame(boolean unload) {
        if (hasGameRunning()) {
            for (UUID id : currentGame.getParticipants()) {
                currentGame.removeParticipant(Bukkit.getPlayer(id));
            }
            if (unload) {
                Bukkit.unloadWorld(currentGame.getWorld(), false);
                currentGame = null;
            }
        }
    }

    /**
     * Replay the current game.
     */
    public void replayGame() {
        endGame(false);
        for (BlockImage image : currentGame.getCachedWorld()) {
            currentGame.getWorld().pasteBlocks(image);
        }
        addGame(currentGame);
    }

    @Override
    public void onEnable() {
        instance = this;
        this.currentGame = null;
        this.commands = new BukkitCommandsManager();
        this.commandsRegistry = new CommandsManagerRegistration(this, this.commands);
        this.commandsRegistry.register(GameCommands.class);
    }

    @Override
    public void onDisable() {
        instance = null;
        if (!hasGameRunning()) Bukkit.unloadWorld(currentGame.getWorld(), false);
        this.currentGame = null;
        this.commands = null;
        this.commandsRegistry = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + "You don't have permission.");
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + "Usage: " + e.getUsage());
        } catch (WrappedCommandException e) {
            sender.sendMessage(ChatColor.RED + "An unknown error has occurred. Please notify an administrator.");
            e.printStackTrace();
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return true;
    }

}

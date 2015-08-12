package me.electroid.game.command;

import com.sk89q.minecraft.util.commands.*;
import me.electroid.game.Game;
import me.electroid.game.GameLoader;
import me.electroid.game.util.ZipUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ElectroidFilms on 7/27/15.
 */
public class GameCommands {

    @Command(
            aliases = {"play", "create"},
            desc = "Plays a game given the specified URL.",
            max = -1,
            min = 1,
            anyFlags = true,
            flags = "c",
            usage = "<url>"
    )
    @CommandPermissions("game.play")
    @Console
    public static void play(final CommandContext arguments, final CommandSender sender) throws CommandException {
        if (arguments.hasFlag('c')) {
            sender.sendMessage(ChatColor.GRAY + "Attempting to load the game...");
            Bukkit.getScheduler().runTaskAsynchronously(GameLoader.get(), new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(arguments.getRemainingString(0));
                        File file = ZipUtils.unZip(url, Bukkit.getWorldContainer());
                        Game game = new Game(file);
                        file = null;
                        GameLoader.get().addGame(game);
                        sender.sendMessage(ChatColor.GREEN + "Game has been created.");
                    } catch (MalformedURLException e) {
                        sender.sendMessage(ChatColor.RED + "Unable to parse url.");
                        sender.sendMessage(ChatColor.GRAY + e.getMessage());
                    } catch (IOException e) {
                        sender.sendMessage(ChatColor.RED + "Unable to unzip the file from the url.");
                        sender.sendMessage(ChatColor.GRAY + e.getMessage());
                    } catch (Game.GameLoadException e) {
                        sender.sendMessage(ChatColor.RED + "Unable to load the game.");
                        sender.sendMessage(ChatColor.GRAY + e.getMessage());
                    }
                }
            });
        } else {
            sender.sendMessage(ChatColor.RED + "Please confirm you would like to download this world with the flag '-c'");
        }
    }

    @Command(
            aliases = {"replay"},
            desc = "Replay the current game.",
            max = 0,
            min = 0
    )
    @CommandPermissions("game.replay")
    @Console
    public static void replay(final CommandContext arguments, final CommandSender sender) throws CommandException {
        if (!GameLoader.get().hasGameRunning()) {
            sender.sendMessage(ChatColor.RED + "There is no game currently running!");
        } else {
            GameLoader.get().replayGame();
            sender.sendMessage(ChatColor.GREEN + "The current game has been reloaded.");
        }
    }

    @Command(
            aliases = {"end", "finish"},
            desc = "Ends the current game.",
            max = 0,
            min = 0
    )
    @CommandPermissions("game.end")
    @Console
    public static void end(final CommandContext arguments, final CommandSender sender) throws CommandException {
        if (!GameLoader.get().hasGameRunning()) {
            sender.sendMessage(ChatColor.RED + "There is no game currently running!");
        } else {
            GameLoader.get().endGame(true);
            sender.sendMessage(ChatColor.GREEN + "Current game has been ended.");
        }
    }

}

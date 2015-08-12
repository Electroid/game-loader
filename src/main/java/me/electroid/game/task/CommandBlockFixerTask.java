package me.electroid.game.task;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;

/**
 * Created by ElectroidFilms on 7/27/15.
 */
public class CommandBlockFixerTask implements Runnable {

    private final World world;

    /**
     * Make sure all command blocks execute the vanilla commands instead of bukkit ones.
     * @param world The world to execute this task in.
     */
    public CommandBlockFixerTask(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState tile : chunk.getTileEntities()) {
                if (tile instanceof CommandBlock) {
                    CommandBlock cmd = (CommandBlock) tile;
                    String command = cmd.getCommand();
                    String content = command.startsWith("/") ? command.replaceFirst("/", "") : command;
                    cmd.setCommand("/minecraft:" + content);
                }
            }
        }
    }
}

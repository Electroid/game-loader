package me.electroid.game;

import me.electroid.game.task.CommandBlockFixerTask;
import org.bukkit.*;
import org.bukkit.block.BlockImage;
import org.bukkit.block.RegionFactory;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.*;

/**
 * Created by ElectroidFilms on 7/27/15.
 */
public class Game {

    /** The name of the game. */
    private final String name;
    /** The world of the game. */
    private final World world;
    /** The map of participants to their original world name. */
    private final Map<UUID, Location> participants;
    /** The cached world in the form of block images. */
    private final Set<BlockImage> cachedWorld;

    /**
     * Creates a new Game.
     * @param file The file location of the game.
     */
    public Game(File file) throws GameLoadException {
        if (file == null) {
            throw new GameLoadException("cannot be null");
        } else if (!file.exists()) {
            throw new GameLoadException("does not exist");
        } else if (!file.canRead()) {
            throw new GameLoadException("is not readable");
        } else if (file.listFiles() == null) {
            throw new GameLoadException("is empty");
        } else {
            boolean region = false, level = false;
            for (File subFile : file.listFiles()) {
                if (subFile.isDirectory() || subFile.getName().equalsIgnoreCase("region")) region = true;
                if (subFile.getName().equalsIgnoreCase("level.dat")) level = true;
            }
            if (!level) throw new GameLoadException("does not contain a level.dat");
            if (!region) throw new GameLoadException("does not contain a region folder");
        }
        this.name = file.getName();
        this.participants = new HashMap<>();
        this.world = Bukkit.getServer().createWorld(new WorldCreator(file.getName()).generator(new ChunkGenerator() {
            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[Byte.MAX_VALUE];
            }
        }));
        // FIXME - CME when iterating through the chunks. Modify the chunk populators instead of running a task.
        //Bukkit.getScheduler().runTaskAsynchronously(GameLoader.get(), new CommandBlockFixerTask(world));
        this.cachedWorld = new HashSet<>();
    }

    /**
     * Get the name of this game.
     * @return The name of this game.
     */
    public final String getName() {
        return this.name;
    }

    /**
     * Get the world of this game.
     * @return The world of this game.
     */
    public final World getWorld() {
        return this.world;
    }

    /**
     * Get the cached world of this game.
     * @return The cached world of this game.
     */
    public final Set<BlockImage> getCachedWorld() {
        return this.cachedWorld;
    }

    /**
     * Get the set of participants for this game.
     * @return The set of participants for this game.
     */
    public final Set<UUID> getParticipants() {
        return this.participants.keySet();
    }

    /**
     * Check whether a player is participating in this game.
     * @param player
     * @return Whether this player is participating in this game.
     */
    public final boolean isParticipating(Player player) {
        return this.participants.containsKey(player.getUniqueId());
    }

    /**
     * Add a participant to this game.
     * @param player
     */
    public void addParticipant(Player player) {
        participants.put(player.getUniqueId(), player.getLocation());
        player.teleport(world.getSpawnLocation());
    }

    /**
     * Remove a participant from this game.
     * @param player
     */
    public void removeParticipant(Player player) {
        UUID id = player.getUniqueId();
        player.teleport(participants.get(id));
        participants.remove(id);
    }

    /**
     * Return a set of block images of a world.
     * @param world The world to cache in block images.
     * @return The cached world in block images.
     */
    private Set<BlockImage> cacheWorld(World world) {
        RegionFactory factory = Bukkit.getRegionFactory();
        Set<BlockImage> images = new HashSet<>();
        for (Chunk chunk : world.getLoadedChunks()) {
            images.add(world.copyBlocks(factory.cuboid(chunk.getBlock(0, 0, 0).getLocation(), chunk.getBlock(15, world.getMaxHeight() - 1, 15).getLocation()), true, true));
        }
        return images;
    }

    public class GameLoadException extends RuntimeException {
        public GameLoadException(String reason) {
            super("Game folder " + reason);
        }
    }

}

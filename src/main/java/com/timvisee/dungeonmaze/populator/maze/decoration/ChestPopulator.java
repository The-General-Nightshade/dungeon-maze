package com.timvisee.dungeonmaze.populator.maze.decoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.timvisee.dungeonmaze.Core;
import com.timvisee.dungeonmaze.loot.LootTableManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.timvisee.dungeonmaze.event.generation.GenerationChestEvent;
import com.timvisee.dungeonmaze.populator.maze.MazeRoomBlockPopulator;
import com.timvisee.dungeonmaze.populator.maze.MazeRoomBlockPopulatorArgs;
import com.timvisee.dungeonmaze.populator.maze.MazeStructureType;
import com.timvisee.dungeonmaze.util.ChestUtils;
import com.timvisee.dungeonmaze.util.MaterialUtils;

public class ChestPopulator extends MazeRoomBlockPopulator {

    /** General populator constants. */
	private static final int LAYER_MIN = 1;
	private static final int LAYER_MAX = 7;
	private static final float ROOM_CHANCE = .03f;

    // TODO: Implement this!
	public static final double CHANCE_CHEST_ADDITION_EACH_LEVEL = -0.333; // to 1

	@Override
	public void populateRoom(MazeRoomBlockPopulatorArgs args) {
        final Chunk chunk = args.getSourceChunk();
        final Random rand = args.getRandom();
        final int x = args.getRoomChunkX();
        final int z = args.getRoomChunkZ();
        final int chestX = x + rand.nextInt(6) + 1;
        final int chestY = args.getFloorY() + 1;
        final int chestZ = z + rand.nextInt(6) + 1;

        if(!(chunk.getBlock(chestX, chestY - 1, chestZ).getType() == Material.AIR)) {
            Block chestBlock = chunk.getBlock(chestX, chestY, chestZ);
            if(chestBlock.getType() == Material.AIR) {

                // Generate new inventory contents
                List<ItemStack> contents = generateChestContents(rand);
                setGeneratedBlock(chestBlock, Material.CHEST);

                // Call the chest generation event
                GenerationChestEvent event = new GenerationChestEvent(chestBlock, rand, contents, MazeStructureType.UNSTRUCTURE);
                Bukkit.getServer().getPluginManager().callEvent(event);

                // Do the event
                if(!event.isCancelled()) {
                    // Make sure the chest is still there, a developer could change the chest through the event!
                    if(event.getBlock().getType() != Material.CHEST)
                        return;

                    // Add the contents to the chest
                    ChestUtils.addItemsToChest(event.getBlock(), event.getContents(), !event.getAddContentsInOrder(), rand);
                } else {
                    // The event is cancelled
                    // Put the chest back to it's original state (air)
                    setGeneratedBlock(chestBlock, Material.AIR);
                }

            } else if (chestBlock.getType() == Material.CHEST) {
                // The follow is for rare case when the chest is generate before the plugin does the event
                Chest chest = (Chest) chestBlock.getState();
                if (chest.getInventory() != null) {
                    // Generate new inventory contents
                    List<ItemStack> contents = generateChestContents(rand);

                    // Call the chest generation event
                    GenerationChestEvent event = new GenerationChestEvent(chestBlock, rand, contents, MazeStructureType.UNSTRUCTURE);
                    Bukkit.getServer().getPluginManager().callEvent(event);

                    // Do the event
                    if(!event.isCancelled()) {
                        // Make sure the chest is still there, a developer could change the chest through the event!
                        if(event.getBlock().getType() != Material.CHEST)
                            return;

                        // Add the contents to the chest
                        ChestUtils.addItemsToChest(event.getBlock(), event.getContents(), !event.getAddContentsInOrder(), rand);
                    }
                }
            }
		}
	}

	private List<ItemStack> generateChestContents(Random random) {
		LootTableManager.getInstance().load();
		int minItems = Math.max(0, Core.getConfigHandler().lootMinItems);
		int maxItems = Math.max(minItems, Core.getConfigHandler().lootMaxItems);
		List<ItemStack> items = LootTableManager.getInstance().generateChestContents(random, minItems, maxItems);
		if(items.isEmpty()) {
			items = new ArrayList<>();
			if(random.nextInt(100) < 80)
				items.add(MaterialUtils.createItemStack(Material.TORCH, 4, (short) 0));
			if(random.nextInt(100) < 40)
				items.add(MaterialUtils.createItemStack(Material.TORCH, 8, (short) 0));
			if(random.nextInt(100) < 20)
				items.add(MaterialUtils.createItemStack(Material.TORCH, 12, (short) 0));
			if(random.nextInt(100) < 40)
				items.add(MaterialUtils.createItemStack(Material.APPLE, 1, (short) 0));
			if(random.nextInt(100) < 10)
				items.add(MaterialUtils.createItemStack(Material.ARROW, 16, (short) 0));
			if(random.nextInt(100) < 5)
				items.add(MaterialUtils.createItemStack(Material.ARROW, 24, (short) 0));
			if(random.nextInt(100) < 20)
				items.add(MaterialUtils.createItemStack(Material.DIAMOND, 1, (short) 0));
			if(random.nextInt(100) < 50)
				items.add(MaterialUtils.createItemStack(Material.IRON_INGOT, 1, (short) 0));
			if(random.nextInt(100) < 60)
				items.add(MaterialUtils.createItemStack(Material.GOLD_INGOT, 1, (short) 0));
		}
		return items;
	}

    @Override
    public float getRoomChance() {
        return ROOM_CHANCE;
    }
	
	/**
	 * Get the minimum layer
	 * @return Minimum layer
	 */
	@Override
	public int getMinimumLayer() {
		return LAYER_MIN;
	}
	
	/**
	 * Get the maximum layer
	 * @return Maximum layer
	 */
	@Override
	public int getMaximumLayer() {
		return LAYER_MAX;
	}
}

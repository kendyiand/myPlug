package com.eneskey.paper.myplug;

import com.eneskey.paper.myplug.MyPlug;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakEventHandler implements Listener {
    private final MyPlug plugin;

    public BlockBreakEventHandler(MyPlug plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        Location blockLocation = block.getLocation();

        // Проверяем, находимся ли в регионе "spawn"
        if (isInSpawnRegion(blockLocation)) {
            // Применяем логику только для региона "spawn"

            if (blockType == Material.COAL_ORE) {
                event.setCancelled(true);
                block.setType(Material.COBBLESTONE);
                ItemStack coal = new ItemStack(Material.COAL, 1);
                block.getWorld().dropItemNaturally(blockLocation, coal);

                Bukkit.getScheduler().runTaskLater(this, () -> {
                    if (block.getType() == Material.COBBLESTONE) {
                        block.setType(Material.COAL_ORE);
                    }
                }, 60);
            } else if (blockType == Material.COBBLESTONE) {
                event.setCancelled(true);
                block.setType(Material.BEDROCK);
                ItemStack cobblestone = new ItemStack(Material.COBBLESTONE, 1);
                block.getWorld().dropItemNaturally(blockLocation, cobblestone);

                Bukkit.getScheduler().runTaskLater(this, () -> {
                    if (block.getType() == Material.BEDROCK) {
                        block.setType(Material.COAL_ORE);
                    }
                }, 100);
            }
        }
        // Логика для других местоположений вне региона "spawn" может быть добавлена здесь
    }
    private boolean isInSpawnRegion(Location location) {
        if (worldGuardPlugin == null) {
            return false;
        }

        WorldGuard worldGuard = WorldGuard.getInstance();
        RegionContainer container = worldGuard.getPlatform().getRegionContainer();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());

        if (container == null) {
            return false;
        }

        RegionManager regions = container.get(world);
        if (regions == null) {
            return false;
        }

        ProtectedRegion spawnRegion = regions.getRegion("spawn");
        if (!(spawnRegion instanceof ProtectedCuboidRegion)) {
            return false;
        }

        ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) spawnRegion;

        BlockVector3 blockVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
        return cuboidRegion.contains(blockVector);
    }
}

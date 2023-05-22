package com.eneskey.paper.myplug;

import com.eneskey.paper.myplug.handler.NewEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class MyPlug extends JavaPlugin implements Listener {
    private WorldGuardPlugin worldGuard;
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

    }


    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();

        if (blockType == Material.COAL_ORE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.COBBLESTONE);
            ItemStack coal = new ItemStack(Material.COAL, 1);
            Location blockLocation = event.getBlock().getLocation();
            event.getBlock().getWorld().dropItemNaturally(blockLocation, coal);

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (block.getType() == Material.COBBLESTONE) {
                        block.setType(Material.COAL_ORE);
                    }
                }
            };
            task.runTaskLater(this, 20);
        }
        else if (blockType == Material.COBBLESTONE) {
            event.setCancelled(true);
            event.getBlock().setType(Material.BEDROCK);
            ItemStack cobblestone = new ItemStack(Material.COBBLESTONE, 1);
            Location blockLocation = event.getBlock().getLocation();
            event.getBlock().getWorld().dropItemNaturally(blockLocation, cobblestone);

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (block.getType() == Material.BEDROCK) {
                        block.setType(Material.COAL_ORE);
                    }
                }
            };
            task.runTaskLater(this, 40);
        }
    }
    private boolean isInSpawnRegion(Location location) {
        if (worldGuard == null) {
            return false;
        }

        World world = location.getWorld();
        ApplicableRegionSet regionSet = worldGuard.getRegionManager(world).getApplicableRegions(location);

        for (ProtectedRegion region : regionSet) {
            if (region.getId().equalsIgnoreCase("spawn")) {
                return true;
            }
        }

        return false;
    }
}

package com.eneskey.paper.myplug;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class MyPlug extends JavaPlugin implements Listener {
    private WorldGuardPlugin worldGuardPlugin;
    @Override
    public void onEnable() {
        if (!setupWorldGuard()) {
            getLogger().warning("WorldGuard not found. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean setupWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
        if (plugin instanceof WorldGuardPlugin) {
            worldGuardPlugin = (WorldGuardPlugin) plugin;
            return true;
        }
        return false;
    }


    @EventHandler
    public void handleBlockBreakEvent(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material blockType = block.getType();
        Location blockLocation = block.getLocation();

        if (isInSpawnRegion(blockLocation)) {

            if (blockType == Material.COAL_ORE || blockType == Material.COBBLESTONE) {
                event.setCancelled(true);

                if (blockType == Material.COAL_ORE) {
                    block.setType(Material.COBBLESTONE);
                    ItemStack coal = new ItemStack(Material.COAL, 1);
                    block.getWorld().dropItemNaturally(blockLocation, coal);

                    Bukkit.getScheduler().runTaskLater(this, () -> {
                        if (block.getType() == Material.COBBLESTONE) {
                            block.setType(Material.COAL_ORE);
                        }
                    }, 60);
                } else {
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
        }
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

        // Загрузка списка регионов из файла в ресурсах
        List<String> regionNames = loadRegionNames();

        for (String regionName : regionNames) {
            ProtectedRegion region = regions.getRegion(regionName);
            if (region instanceof ProtectedCuboidRegion) {
                ProtectedCuboidRegion cuboidRegion = (ProtectedCuboidRegion) region;

                BlockVector3 blockVector = BlockVector3.at(location.getX(), location.getY(), location.getZ());
                if (cuboidRegion.contains(blockVector)) {
                    return true;
                }
            }
        }

        return false;
    }

    private List<String> loadRegionNames() {
        List<String> regionNames = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream("/regions.yml")) {
            if (inputStream != null) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream));
                regionNames = config.getStringList("regions");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return regionNames;
    }
}

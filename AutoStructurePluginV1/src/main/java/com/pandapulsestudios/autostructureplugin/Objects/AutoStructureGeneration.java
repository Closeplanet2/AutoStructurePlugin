package com.pandapulsestudios.autostructureplugin.Objects;

import com.pandapulsestudios.autostructureplugin.API.CustomSchematicAPI;
import com.pandapulsestudios.autostructureplugin.API.PlayerWorldEditAPI;
import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.BukkitRunnable.GenerationRunnable;
import com.pandapulsestudios.autostructureplugin.BukkitRunnable.PreGenerationRunnable;
import com.pandapulsestudios.autostructureplugin.Enum.CustomBlockFace;
import com.pandapulsestudios.autostructureplugin.Enum.Message;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Messages;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Settings;
import com.pandapulsestudios.autostructureplugin.PulseJson.CustomSchematic;
import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore._External.WorldEdit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AutoStructureGeneration {

    public static boolean StartAutoStructureGeneration(Player player, Region region, String associationKey){
        if(AutoStructurePlugin.IsAutoStructurePreGeneration()) return false;
        var settings = Settings.ReturnStatic();
        if(AutoStructurePlugin.AutoStructureGeneration.size() >= settings.maximumGenerationsRunning) return false;
        var autoStructure = AutoStructurePlugin.AutoStructureGeneration.getOrDefault(player.getUniqueId(), null);
        if(autoStructure != null) StopAutoStructureGeneration(player);
        autoStructure = new AutoStructureGeneration(player, region, associationKey);
        AutoStructurePlugin.AutoStructureGeneration.put(player.getUniqueId(), autoStructure);
        return true;
    }

    public static void ToggleAutoStructureGeneration(Player player, boolean state){
        if(AutoStructurePlugin.IsAutoStructurePreGeneration()) return;
        var autoStructure = AutoStructurePlugin.AutoStructureGeneration.getOrDefault(player.getUniqueId(), null);
        if(autoStructure == null) return;
        autoStructure.running = state;
        AutoStructurePlugin.AutoStructureGeneration.put(player.getUniqueId(), autoStructure);
    }

    public static void StopAutoStructureGeneration(Player player){
        if(AutoStructurePlugin.IsAutoStructurePreGeneration()) return;
        var autoStructure = AutoStructurePlugin.AutoStructureGeneration.getOrDefault(player.getUniqueId(), null);
        if(autoStructure == null) return;
        for(var blockVector : autoStructure.masterList.keySet()){
            PlayerWorldEditAPI.GetBlockFromBlockVector(autoStructure.region, blockVector).setType(autoStructure.masterList.get(blockVector));
        }
        for(var runnable : autoStructure.generationRunnables) runnable.cancel();
        AutoStructurePlugin.AutoStructureGeneration.remove(player.getUniqueId());
    }

    public ArrayList<GenerationRunnable> generationRunnables = new ArrayList<>();
    public ArrayList<BlockVector3> blockVector3Workload = new ArrayList<>();
    public ArrayList<BlockVector3> blockVector3Visited = new ArrayList<>();
    public HashMap<BlockVector3, Material> masterList = new HashMap<>();
    private final Player player;
    private final Region region;
    public final int totalSize;
    public boolean running = false;

    public AutoStructureGeneration(Player player, Region region, String associationKey) {
        this.player = player;
        this.region = region;
        totalSize = region.getHeight() * region.getLength() * region.getWidth();
        var settings = Settings.ReturnStatic();
        for (int i = 0; i < settings.threadCount; i++) {
           generationRunnables.add(new GenerationRunnable(this, region, associationKey));
        }
        for(var runnable : generationRunnables) runnable.runTaskTimer(AutoStructurePlugin.AutoStructurePlugin, settings.preGenerationStartDelay, settings.preGenerationRunnableDelay);
    }



    public void CheckComplete(){
        var percentage = ((double) blockVector3Visited.size() / totalSize) * 100;
        Messages.ReturnStatic().SendMessageToPlayer(Message.PlayerGenProcess, player, blockVector3Visited.size(), totalSize, percentage);
        if(blockVector3Visited.size() < totalSize) return;
        StopAutoStructureGeneration(player);
    }
}

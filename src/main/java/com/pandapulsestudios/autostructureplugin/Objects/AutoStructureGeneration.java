package com.pandapulsestudios.autostructureplugin.Objects;

import com.pandapulsestudios.autostructureplugin.API.PlayerWorldEditAPI;
import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.BukkitRunnable.GenerationRunnable;
import com.pandapulsestudios.autostructureplugin.BukkitRunnable.PreGenerationRunnable;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Settings;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class AutoStructureGeneration {
    public static boolean StartGeneration(Player player, Region region, String associationKey){
        if(AutoStructurePlugin.IsAutoStructurePreGeneration()) return false;
        var settings = Settings.ReturnStatic();
        if(AutoStructurePlugin.AutoStructureGenerations.size() >= settings.maximumGenerationsRunning) return false;
        if(AutoStructurePlugin.AutoStructureGenerations.containsKey(player.getUniqueId())) StopGeneration(player);
        AutoStructurePlugin.AutoStructureGenerations.put(player.getUniqueId(), new AutoStructureGeneration(player, region, associationKey));
        return true;
    }

    public static void ToggleGeneration(Player player, boolean state){
        if(AutoStructurePlugin.IsAutoStructurePreGeneration()) return;
        var autoStructure = AutoStructurePlugin.AutoStructureGenerations.getOrDefault(player.getUniqueId(), null);
        if(autoStructure == null) return;
        autoStructure.running = state;
        AutoStructurePlugin.AutoStructureGenerations.put(player.getUniqueId(), autoStructure);
    }

    public static void StopGeneration(Player player){
        if(AutoStructurePlugin.IsAutoStructurePreGeneration()) return;
        var autoStructure = AutoStructurePlugin.AutoStructureGenerations.getOrDefault(player.getUniqueId(), null);
        if(autoStructure != null) autoStructure.Cancel();
    }

    public static void PlaceGeneration(Player player){
        if(AutoStructurePlugin.IsAutoStructurePreGeneration()) return;
        var autoStructure = AutoStructurePlugin.AutoStructureGenerations.getOrDefault(player.getUniqueId(), null);
        if(autoStructure == null) return;
        for(var blockVector : autoStructure.blockVector3Visited.keySet()){
            var block = PlayerWorldEditAPI.GetBlockFromBlockVector(autoStructure.region, blockVector);
            block.setType(autoStructure.blockVector3Visited.get(blockVector));
        }
    }

    public HashMap<BlockVector3, Material> blockVector3Visited = new HashMap<>();
    public ArrayList<BlockVector3> blockVector3Workload = new ArrayList<>();
    private final ArrayList<GenerationRunnable> threadGenerationRunnable = new ArrayList<>();
    private final Player player;
    private final Region region;
    public final int totalSize;
    public boolean running = false;

    public AutoStructureGeneration(Player player, Region region, String associationKey){
        this.player = player;
        this.region = region;
        this.totalSize = region.getHeight() * region.getLength() * region.getWidth();

        var subDividedList = AutoStructurePlugin.SubDivideCustomSchematics(associationKey);
        var settings = Settings.ReturnStatic();
        var plugin = AutoStructurePlugin.AutoStructurePlugin;
        var start = settings.preGenerationStartDelay;
        var delay = settings.preGenerationRunnableDelay;

        for(var i = 0; i < settings.threadCount; i++){
            threadGenerationRunnable.add(new GenerationRunnable(this, player, region, i, subDividedList));
            threadGenerationRunnable.get(i).runTaskTimerAsynchronously(plugin, start, delay);
        }

        running = true;
    }

    public void Cancel(){
        for(var thread : threadGenerationRunnable) thread.cancel();
    }
}

package com.pandapulsestudios.autostructureplugin.Objects;

import com.pandapulsestudios.autostructureplugin.API.CustomSchematicAPI;
import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.BukkitRunnable.PreGenerationRunnable;
import com.pandapulsestudios.autostructureplugin.Enum.Message;
import com.pandapulsestudios.autostructureplugin.Enum.Permission;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Messages;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

public class AutoStructurePreGeneration {

    public static void StartPreGeneration(){
        if(AutoStructurePlugin.AutoStructurePreGeneration != null) StopPreGeneration();
        AutoStructurePlugin.AutoStructurePreGeneration = new AutoStructurePreGeneration();
        AutoStructurePlugin.AutoStructurePreGeneration.running = true;
    }

    public static void TogglePreGeneration(boolean state){
        if(AutoStructurePlugin.AutoStructurePreGeneration == null) return;
        AutoStructurePlugin.AutoStructurePreGeneration.running = state;
    }

    public static void StopPreGeneration(){
        if(AutoStructurePlugin.AutoStructurePreGeneration == null) return;
        for(var thread : AutoStructurePlugin.AutoStructurePreGeneration.threadGenerationRunnable) thread.cancel();
        AutoStructurePlugin.AutoStructurePreGeneration = null;
    }
    private final ArrayList<PreGenerationRunnable> threadGenerationRunnable = new ArrayList<>();
    private boolean running = false;

    public AutoStructurePreGeneration(){
        var settings = Settings.ReturnStatic();
        var workload = new HashMap<String, Boolean>();
        for(var fileName : CustomSchematicAPI.ReturnAllSchematicNames()) workload.put(fileName, false);
        for(var i = 0; i < settings.threadCount; i++){
            threadGenerationRunnable.add(new PreGenerationRunnable(this, workload));
        }
        for(var thread : threadGenerationRunnable){
            thread.runTaskTimerAsynchronously(AutoStructurePlugin.AutoStructurePlugin, settings.preGenerationStartDelay, settings.preGenerationRunnableDelay);
        }
    }

    public boolean isRunning(){
        return running;
    }
}

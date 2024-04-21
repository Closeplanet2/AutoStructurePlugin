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

    private final HashMap<String, Boolean> workload = new HashMap<>();
    private final ArrayList<PreGenerationRunnable> threadGenerationRunnable = new ArrayList<>();
    private boolean running = false;

    public AutoStructurePreGeneration(){
        var settings = Settings.ReturnStatic();
        for(var fileName : CustomSchematicAPI.ReturnAllSchematicNames("")) workload.put(fileName, false);
        for(var i = 0; i < settings.threadCount; i++) threadGenerationRunnable.add(new PreGenerationRunnable(this));
        for(var thread : threadGenerationRunnable){
            thread.runTaskTimer(AutoStructurePlugin.AutoStructurePlugin, settings.preGenerationStartDelay, settings.preGenerationRunnableDelay);
        }
    }

    public boolean isRunning(){
        return running;
    }

    public String ThreadRequestNextWorkload(){
        for(var workloadKey : workload.keySet()){
            if(!workload.get(workloadKey)){
                workload.put(workloadKey, true);
                return workloadKey;
            }
        }
        return null;
    }

    public int WorkLoadComplete(){
        var count = 0;
        for(var workloadKey : workload.keySet()) if(workload.get(workloadKey)) count += 1;
        return count;
    }

    public void ThreadFinished(PreGenerationRunnable preGenerationRunnable){
        preGenerationRunnable.cancel();
        for(var thread : threadGenerationRunnable) if(!thread.isCancelled()) return;
        Messages.ReturnStatic().SendMessageToPerm(Message.AdminPreTrainComplete, Permission.ADMIN_MESSAGES);
        StopPreGeneration();
    }

    public void SendProcessPercentageToAdmin(){
        var workLoadComplete = WorkLoadComplete();
        var totalWorkload = workload.size();
        var percentageComplete = ((double) workLoadComplete / totalWorkload) * 100;
        Messages.ReturnStatic().SendMessageToPerm(Message.AdminPreTrainProcess, Permission.ADMIN_MESSAGES, workLoadComplete, totalWorkload, percentageComplete);
    }
}

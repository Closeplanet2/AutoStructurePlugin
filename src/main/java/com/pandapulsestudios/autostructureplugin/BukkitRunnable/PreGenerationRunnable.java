package com.pandapulsestudios.autostructureplugin.BukkitRunnable;

import com.pandapulsestudios.autostructureplugin.Enum.Message;
import com.pandapulsestudios.autostructureplugin.Enum.Permission;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructurePreGeneration;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Messages;
import com.pandapulsestudios.autostructureplugin.PulseJSON.CustomSchematic;
import com.pandapulsestudios.pulsecore._External.WorldEdit.WorldEditAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class PreGenerationRunnable extends BukkitRunnable {

    private final AutoStructurePreGeneration autoStructurePreGeneration;
    private final HashMap<String, Boolean> workload;

    public PreGenerationRunnable(AutoStructurePreGeneration autoStructurePreGeneration, HashMap<String, Boolean> workload){
        this.autoStructurePreGeneration = autoStructurePreGeneration;
        this.workload = workload;
    }

    @Override
    public void run() {
        synchronized (workload) {
            if(!autoStructurePreGeneration.isRunning()) return;
            var nextWorkload = ThreadRequestNextWorkload();

            if(nextWorkload == null){
                AutoStructurePreGeneration.StopPreGeneration();
                return;
            }

            SendProcessPercentageToAdmin();
            new CustomSchematic(nextWorkload, nextWorkload, true);
        }
    }

    private String ThreadRequestNextWorkload(){
        for(var workloadKey : workload.keySet()){
            if(!workload.get(workloadKey)){
                workload.put(workloadKey, true);
                return workloadKey;
            }
        }
        return null;
    }

    public void SendProcessPercentageToAdmin(){
        var workLoadComplete = WorkLoadComplete();
        var totalWorkload = workload.size();
        var percentageComplete = ((double) workLoadComplete / totalWorkload) * 100;
        Messages.ReturnStatic().SendMessageToPerm(Message.AdminPreTrainProcess, Permission.ADMIN_MESSAGES, workLoadComplete, totalWorkload, percentageComplete);
    }

    public int WorkLoadComplete(){
        var count = 0;
        for(var workloadKey : workload.keySet()) if(workload.get(workloadKey)) count += 1;
        return count;
    }
}

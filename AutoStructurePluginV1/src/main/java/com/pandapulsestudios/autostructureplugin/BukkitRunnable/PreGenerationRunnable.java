package com.pandapulsestudios.autostructureplugin.BukkitRunnable;

import com.pandapulsestudios.autostructureplugin.API.CustomSchematicAPI;
import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructurePreGeneration;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Settings;
import com.pandapulsestudios.autostructureplugin.PulseJson.CustomSchematic;
import com.pandapulsestudios.pulsecore._External.WorldEdit.WorldEditAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PreGenerationRunnable extends BukkitRunnable {

    private final AutoStructurePreGeneration autoStructurePreGeneration;

    public PreGenerationRunnable(AutoStructurePreGeneration autoStructurePreGeneration){
        this.autoStructurePreGeneration = autoStructurePreGeneration;
    }

    @Override
    public void run() {
        if(!autoStructurePreGeneration.isRunning()) return;
        var nextWorkload = autoStructurePreGeneration.ThreadRequestNextWorkload();
        if(nextWorkload == null){
            autoStructurePreGeneration.ThreadFinished(this);
            return;
        }
        autoStructurePreGeneration.SendProcessPercentageToAdmin();
        var customSchematic = new CustomSchematic(nextWorkload);
        var clipboard = WorldEditAPI.LoadSchematic(nextWorkload);
        customSchematic.SerializeClipboard(clipboard);
        customSchematic.SaveJSON(false);
    }
}

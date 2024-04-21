package com.pandapulsestudios.autostructureplugin.PulseConfig;

import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interface.StorageComment;
import com.pandapulsestudios.pulsecore.Java.PulseAutoRegister;
import org.bukkit.plugin.java.JavaPlugin;

@PulseAutoRegister
public class Settings implements PulseConfig {
    public static Settings ReturnStatic(){ return (Settings) StorageAPI.ReturnStatic(Settings.class, StorageType.CONFIG, false); }

    @Override
    public JavaPlugin mainClass() { return AutoStructurePlugin.AutoStructurePlugin; }

    @StorageComment("Total Number Of Threads!")
    public int threadCount = 10;
    @StorageComment("Time before PreGenerationRunnable runs!")
    public Long preGenerationStartDelay = 0L;
    @StorageComment("Time between PreGenerationRunnable runs!")
    public Long preGenerationRunnableDelay = 1L;
    @StorageComment("File path for the parent schematic folder! The system will deep dive the folders within!")
    public String schematicFolder = "plugins\\WorldEdit\\schematics";
    @StorageComment("Maximum generations running on the server!")
    public int maximumGenerationsRunning = 5;
}

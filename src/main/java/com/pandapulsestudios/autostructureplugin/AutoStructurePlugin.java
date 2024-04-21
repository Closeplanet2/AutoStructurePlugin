package com.pandapulsestudios.autostructureplugin;

import com.pandapulsestudios.autostructureplugin.API.CustomSchematicAPI;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructureGeneration;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructurePreGeneration;
import com.pandapulsestudios.autostructureplugin.PulseJSON.CustomSchematic;
import com.pandapulsestudios.autostructureplugin.VariableTest.MenuVariableTest;
import com.pandapulsestudios.autostructureplugin.VariableTest.MessageVariableTest;
import com.pandapulsestudios.autostructureplugin.VariableTest.PermissionVariableTest;
import com.pandapulsestudios.pulsecommands.PulseCommands;
import com.pandapulsestudios.pulseconfig.API.JSONAPI;
import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulsecore.FileSystem.DirAPI;
import com.pandapulsestudios.pulsecore.Java.ClassAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public final class AutoStructurePlugin extends JavaPlugin {
    public static AutoStructurePlugin AutoStructurePlugin;
    public static AutoStructurePreGeneration AutoStructurePreGeneration;
    public static HashMap<String, CustomSchematic> customSchematics = new HashMap<>();
    public static HashMap<UUID, AutoStructureGeneration> AutoStructureGenerations = new HashMap<>();

    public static boolean IsAutoStructurePreGeneration(){ return AutoStructurePreGeneration != null; }

    @Override
    public void onEnable() {
        AutoStructurePlugin = this;
        ClassAPI.RegisterPulseVariableTest(new MenuVariableTest());
        ClassAPI.RegisterPulseVariableTest(new MessageVariableTest());
        ClassAPI.RegisterPulseVariableTest(new PermissionVariableTest());
        StorageAPI.RegisterStatic(this, false);
        ClassAPI.RegisterClasses(this);
        PulseCommands.RegisterRaw(this);
        customSchematics = CustomSchematicAPI.ReturnAllSchematics(false);
        DirAPI.CreateDirectory(new File(JSONAPI.ReturnBinaryPath(new CustomSchematic(""))));
    }

    @Override
    public void onDisable() {
        for(var autoStructure : AutoStructureGenerations.values()) autoStructure.Cancel();
    }

    public static HashMap<String, CustomSchematic> SubDivideCustomSchematics(String associationKey){
        if(associationKey.isEmpty()) return customSchematics;
        var data = new HashMap<String, CustomSchematic>();
        for(var name : customSchematics.keySet()) if(name.contains(associationKey)) data.put(name, customSchematics.get(name));
        return data;
    }


}

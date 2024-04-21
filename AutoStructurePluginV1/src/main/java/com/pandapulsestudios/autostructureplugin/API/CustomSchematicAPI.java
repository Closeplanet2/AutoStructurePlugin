package com.pandapulsestudios.autostructureplugin.API;

import com.pandapulsestudios.autostructureplugin.PulseConfig.Settings;
import com.pandapulsestudios.autostructureplugin.PulseJson.CustomSchematic;
import com.pandapulsestudios.pulseconfig.API.JSONAPI;
import com.pandapulsestudios.pulsecore.FileSystem.DirAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomSchematicAPI {
    public static HashMap<String, CustomSchematic> ReturnAllSchematics(boolean debug){
        var rawData = JSONAPI.ReturnAllConfigDocuments(new CustomSchematic(""), debug);
        var serialData = new HashMap<String, CustomSchematic>();
        for(var rawKey : rawData.keySet()) serialData.put(rawKey, (CustomSchematic) rawData.get(rawKey));
        return serialData;
    }

    public static List<String> ReturnAllSchematicNames(String associationKey){
        var data = new ArrayList<String>();
        var settings = Settings.ReturnStatic();
        for(var file : DirAPI.ReturnAllFilesFromDirectory(new File(settings.schematicFolder), true)){
            if(file.getName().contains(".schem")) data.add(file.getName());
        }
        return data;
    }
}

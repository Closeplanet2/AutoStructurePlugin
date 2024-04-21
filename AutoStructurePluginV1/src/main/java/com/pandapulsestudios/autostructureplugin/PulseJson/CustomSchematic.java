package com.pandapulsestudios.autostructureplugin.PulseJson;

import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.CustomVariable.CustomVector;
import com.pandapulsestudios.pulseconfig.Interface.DontSave;
import com.pandapulsestudios.pulseconfig.Interface.PulseJSON;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;

public class CustomSchematic implements PulseJSON {
    @Override
    public JavaPlugin mainClass() { return AutoStructurePlugin.AutoStructurePlugin; }

    @Override
    public String documentID() { return documentID; }

    @Override
    public boolean useSubFolder() { return true; }

    @DontSave
    public String documentID;
    @DontSave
    public Clipboard clipboard;
    public CustomVector min;
    public CustomVector max;
    public CustomVector dimension;
    public SaveableArrayList<CustomSchematicBlock> customSchematicBlocks = new SaveableArrayList<>(CustomSchematicBlock.class);

    public CustomSchematic(String documentID){
        this.documentID = documentID;
    }

    public void SerializeClipboard(Clipboard clipboard){
        min = new CustomVector(clipboard.getMinimumPoint());
        max = new CustomVector(clipboard.getMaximumPoint());
        dimension = new CustomVector(clipboard.getDimensions());
        this.clipboard = clipboard;

        for(var x = min.blockVector3.getX(); x < max.blockVector3.getX(); x++){
            for(var y = min.blockVector3.getY(); y < max.blockVector3.getY(); y++){
                for(var z = min.blockVector3.getZ(); z < max.blockVector3.getZ(); z++){
                    var blockVector3 = BlockVector3.at(x, y, z);
                    var blockState = ReturnBlockAt(blockVector3);
                    customSchematicBlocks.add(new CustomSchematicBlock(this, blockVector3, blockState));
                }
            }
        }
    }

    public BlockState ReturnBlockAt(BlockVector3 blockVector3){
        return clipboard.getBlock(blockVector3);
    }
}

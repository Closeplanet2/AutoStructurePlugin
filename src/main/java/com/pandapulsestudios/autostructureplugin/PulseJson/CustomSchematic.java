package com.pandapulsestudios.autostructureplugin.PulseJSON;

import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.CustomVariable.CustomVector;
import com.pandapulsestudios.autostructureplugin.Enum.CustomBlockFace;
import com.pandapulsestudios.autostructureplugin.PulseCLass.CustomSchematicBlock;
import com.pandapulsestudios.pulseconfig.Interface.DontSave;
import com.pandapulsestudios.pulseconfig.Interface.PulseJSON;
import com.pandapulsestudios.pulseconfig.Objects.SaveableArrayList;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulsecore._External.WorldEdit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;
import java.util.Map;

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
    public CustomVector min = new CustomVector(BlockVector3.ONE);
    public CustomVector max = new CustomVector(BlockVector3.ONE);
    public SaveableArrayList<CustomSchematicBlock> customSchematicBlocks = new SaveableArrayList<>(CustomSchematicBlock.class);

    public CustomSchematic(String documentID){
        this.documentID = documentID;
    }

    public CustomSchematic(String documentID, String nextWorkload, boolean tensorflow){
        this.documentID = documentID;
        this.clipboard = WorldEditAPI.LoadSchematic(nextWorkload);
        min = new CustomVector(clipboard.getMinimumPoint());
        max = new CustomVector(clipboard.getMaximumPoint());
        for(var x = min.blockVector3.getX(); x < max.blockVector3.getX(); x++){
            for(var y = min.blockVector3.getY(); y < max.blockVector3.getY(); y++){
                for(var z = min.blockVector3.getZ(); z < max.blockVector3.getZ(); z++){
                    var blockVector3 = BlockVector3.at(x, y, z);
                    var blockState = clipboard.getBlock(blockVector3);
                    var blockMaterial = BukkitAdapter.adapt(blockState).getMaterial();
                    customSchematicBlocks.add(new CustomSchematicBlock(this, blockVector3, blockMaterial));
                }
            }
        }
        SaveJSON(false);
    }

    public HashMap<CustomBlockFace, Material> GenerateNMapFromStorage(BlockVector3 blockVector3){
        var data = new HashMap<CustomBlockFace, Material>();
        for(var blockFace : CustomBlockFace.values()){
            var newBlockVector3 = blockFace.ReturnWithOffset(blockVector3);
            var customSchematicBlock = ReturnBlockAtBlockVector(newBlockVector3);
            data.put(blockFace, customSchematicBlock == null ? null : customSchematicBlock.material);
        }
        return data;
    }

    public CustomSchematicBlock ReturnBlockAtBlockVector(BlockVector3 blockVector3){
        for(var customSchematicBlock : customSchematicBlocks.ReturnArrayList()){
            if(customSchematicBlock.customVector.blockVector3.equals(blockVector3)) return customSchematicBlock;
        }
        return null;
    }

    public BlockVector3 Distance(){
        return BlockVector3.at(
                max.blockVector3.getX() - min.blockVector3.getX(),
                max.blockVector3.getY() - min.blockVector3.getY(),
                max.blockVector3.getZ() - min.blockVector3.getZ()
        );
    }
}

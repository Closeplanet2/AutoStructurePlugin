package com.pandapulsestudios.autostructureplugin.PulseCLass;

import com.pandapulsestudios.autostructureplugin.CustomVariable.CustomVector;
import com.pandapulsestudios.autostructureplugin.Enum.CustomBlockFace;
import com.pandapulsestudios.autostructureplugin.PulseJSON.CustomSchematic;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class CustomSchematicBlock implements PulseClass {
    public SaveableHashmap<CustomBlockFace, CustomSchematicBlockNeighbour> blockFaceNeighbours = new SaveableHashmap<>(CustomBlockFace.class, CustomSchematicBlockNeighbour.class);
    public Material material;
    public CustomVector customVector;


    public CustomSchematicBlock(){}
    public CustomSchematicBlock(CustomSchematic customSchematic, BlockVector3 blockVector3, Material material){
        this.material = material;
        customVector = new CustomVector(blockVector3);
        for(var blockFace : CustomBlockFace.values()){
            var newBlockVector3 = blockFace.ReturnWithOffset(blockVector3);
            var newBlockState = customSchematic.clipboard.getBlock(newBlockVector3);
            var distanceToExtent = blockFace.ReturnDistance(newBlockVector3, customSchematic.clipboard);
            blockFaceNeighbours.put(blockFace, new CustomSchematicBlockNeighbour(newBlockState, distanceToExtent));
        }
    }




}

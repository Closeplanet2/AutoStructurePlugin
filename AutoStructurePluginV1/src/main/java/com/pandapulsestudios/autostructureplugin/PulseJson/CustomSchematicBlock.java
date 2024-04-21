package com.pandapulsestudios.autostructureplugin.PulseJson;

import com.pandapulsestudios.autostructureplugin.CustomVariable.CustomVector;
import com.pandapulsestudios.autostructureplugin.Enum.CustomBlockFace;
import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class CustomSchematicBlock implements PulseClass {

    public CustomVector customVector;
    public Material material;
    public SaveableHashmap<CustomBlockFace, CustomSchematicBlockNeighbour> blockFaceNeighbours = new SaveableHashmap<>(CustomBlockFace.class, CustomSchematicBlockNeighbour.class);

    public CustomSchematicBlock(){}
    public CustomSchematicBlock(CustomSchematic customSchematic, BlockVector3 blockVector3, BlockState block){
        this.customVector = new CustomVector(blockVector3);
        material = BukkitAdapter.adapt(block).getMaterial();

        for(var blockFace : CustomBlockFace.values()){
            var newBlockVector3 = blockFace.ReturnWithOffset(blockVector3);
            var newBlockState = customSchematic.ReturnBlockAt(newBlockVector3);
            var distanceToExtent = blockFace.ReturnDistance(newBlockVector3, customSchematic.min.blockVector3, customSchematic.max.blockVector3);
            blockFaceNeighbours.put(blockFace, new CustomSchematicBlockNeighbour(newBlockState, distanceToExtent));
        }
    }
}

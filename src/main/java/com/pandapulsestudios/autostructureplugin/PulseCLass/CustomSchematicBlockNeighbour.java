package com.pandapulsestudios.autostructureplugin.PulseCLass;

import com.pandapulsestudios.pulseconfig.Interface.PulseClass;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class CustomSchematicBlockNeighbour implements PulseClass {
    public Material material;
    public double distanceToExtent;

    public CustomSchematicBlockNeighbour(){}
    public CustomSchematicBlockNeighbour(BlockState block, double distanceToExtent){
        material = BukkitAdapter.adapt(block).getMaterial();
        this.distanceToExtent = distanceToExtent;
    }
}

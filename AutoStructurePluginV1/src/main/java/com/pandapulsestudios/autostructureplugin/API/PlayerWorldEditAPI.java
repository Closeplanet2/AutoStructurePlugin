package com.pandapulsestudios.autostructureplugin.API;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PlayerWorldEditAPI {
    public static Region GetPlayerLocalSession(Player player){
        var actor = BukkitAdapter.adapt(player);
        var manager = WorldEdit.getInstance().getSessionManager();
        var localSession = manager.get(actor);
        var selectionWorld = localSession.getSelectionWorld();
        try {
            return localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException e) {
            return null;
        }
    }

    public static BlockVector3 ReturnMiddleLocationOffBlockVectors(Region region){
        var max = region.getMaximumPoint();
        var min = region.getMinimumPoint();
        return BlockVector3.at(((double) min.getX() + max.getX()) / 2, ((double) min.getY() + max.getY()) / 2, ((double) min.getZ() + max.getZ()) / 2);
    }

    public static Block GetBlockFromBlockVector(Region region, BlockVector3 blockVector3){
        return new Location(BukkitAdapter.adapt(region.getWorld()), blockVector3.getX(), blockVector3.getY(), blockVector3.getZ()).getBlock();
    }


}

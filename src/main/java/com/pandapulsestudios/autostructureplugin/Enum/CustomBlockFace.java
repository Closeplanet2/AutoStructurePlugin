package com.pandapulsestudios.autostructureplugin.Enum;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.ChatColor;

public enum CustomBlockFace {
    UP(BlockVector3.at(0, 1, 0), Extent.MaxY, ChatColor.BLUE),
    Down(BlockVector3.at(0, -1, 0), Extent.MinY, ChatColor.YELLOW),
    North(BlockVector3.at(0, 0, 1), Extent.MaxZ, ChatColor.GREEN),
    South(BlockVector3.at(0, 0, -1), Extent.MinZ, ChatColor.RED),
    East(BlockVector3.at(1, 0, 0), Extent.MaxX, ChatColor.DARK_PURPLE),
    West(BlockVector3.at(-1, 0, 0), Extent.MinX, ChatColor.LIGHT_PURPLE);

    public final BlockVector3 offset;
    public final Extent extent;
    public final ChatColor chatColor;

    CustomBlockFace(BlockVector3 offset, Extent extent, ChatColor chatColor){
        this.offset = offset;
        this.extent = extent;
        this.chatColor = chatColor;
    }

    public BlockVector3 ReturnWithOffset(BlockVector3 blockVector3){
        return blockVector3.add(offset);
    }

    public double ReturnDistance(BlockVector3 pos, Clipboard clipboard){
        return ReturnDistance(pos, clipboard.getMinimumPoint(), clipboard.getMaximumPoint());
    }

    public double ReturnDistance(BlockVector3 pos, Region region){
        return ReturnDistance(pos, region.getMinimumPoint(), region.getMaximumPoint());
    }

    public double ReturnDistance(BlockVector3 pos, BlockVector3 min, BlockVector3 max){
        BlockVector3 extentPos = null;
        if(extent == Extent.MaxY) extentPos = BlockVector3.at(pos.getX(), max.getY(), pos.getZ());
        else if(extent == Extent.MinY) extentPos = BlockVector3.at(pos.getX(), min.getY(), pos.getZ());
        else if(extent == Extent.MaxX) extentPos = BlockVector3.at(max.getX(), pos.getY(), pos.getZ());
        else if(extent == Extent.MinX) extentPos = BlockVector3.at(min.getX(), pos.getY(), pos.getZ());
        else if(extent == Extent.MaxZ) extentPos = BlockVector3.at(pos.getX(), pos.getY(), max.getZ());
        else extentPos = BlockVector3.at(pos.getX(), pos.getY(), min.getZ());
        return extentPos.distance(pos);
    }
}

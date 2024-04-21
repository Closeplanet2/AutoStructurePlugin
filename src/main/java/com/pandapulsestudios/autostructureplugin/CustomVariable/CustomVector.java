package com.pandapulsestudios.autostructureplugin.CustomVariable;

import com.pandapulsestudios.pulseconfig.Interface.CustomVariable;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;

import java.util.HashMap;
import java.util.Vector;

public class CustomVector implements CustomVariable{

    public BlockVector3 blockVector3;

    @Override
    public SaveableHashmap<Object, Object> SerializeData() {
        var data = new SaveableHashmap<>(String.class, Integer.class);
        data.put("x", blockVector3.getX());
        data.put("y", blockVector3.getY());
        data.put("z", blockVector3.getZ());
        return data;
    }

    @Override
    public void DeSerializeData(HashMap<Object, Object> hashMap) {
        blockVector3 = BlockVector3.at(
                (long) hashMap.get("x"),
                (long) hashMap.get("y"),
                (long) hashMap.get("z")
        );
    }

    public CustomVector(BlockVector3 blockVector3){
        this.blockVector3 = blockVector3;
    }

    public float[] toArray() {
        return new float[]{blockVector3.getX(), blockVector3.getY(), blockVector3.getZ()};
    }
}

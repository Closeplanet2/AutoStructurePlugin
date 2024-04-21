package com.pandapulsestudios.autostructureplugin.BukkitRunnable;

import com.pandapulsestudios.autostructureplugin.API.PlayerWorldEditAPI;
import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.Enum.CustomBlockFace;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructureGeneration;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructurePreGeneration;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GenerationRunnable extends BukkitRunnable {

    private final AutoStructureGeneration autoStructureGeneration;
    private final Region region;
    private final String associationKey;

    public GenerationRunnable(AutoStructureGeneration autoStructureGeneration, Region region, String associationKey){
        this.autoStructureGeneration = autoStructureGeneration;
        this.region = region;
        this.associationKey = associationKey;
        var middlePoint = PlayerWorldEditAPI.ReturnMiddleLocationOffBlockVectors(region);
        VisitBlockVector(middlePoint, Material.AIR, Material.TORCH);
    }

    @Override
    public void run() {
        if(autoStructureGeneration.running) return;
        if(autoStructureGeneration.blockVector3Workload.isEmpty()) return;
        var workLoad = autoStructureGeneration.blockVector3Workload.remove(0);
        VisitBlockVector(workLoad);
        autoStructureGeneration.CheckComplete();
    }

    private void VisitBlockVector(BlockVector3 blockVector3, Material... ignoreMaterials){
        if(isWithinSquareBounds(blockVector3, region.getMinimumPoint(), region.getMaximumPoint())) {
            for(var blockFace : CustomBlockFace.values()){
                var blockVector = GenBlockNeighbour(blockFace, blockVector3, ignoreMaterials);
                if(blockVector != null){
                    autoStructureGeneration.blockVector3Visited.add(blockVector);
                    autoStructureGeneration.blockVector3Workload.add(blockVector);
                }
                autoStructureGeneration.CheckComplete();
            }
        }
    }

    private BlockVector3 GenBlockNeighbour(CustomBlockFace customBlockFace, BlockVector3 blockVector3, Material... ignoreMaterials){
        var neighbourOffset = customBlockFace.ReturnWithOffset(blockVector3);
        if (autoStructureGeneration.blockVector3Visited.contains(neighbourOffset)) return null;

        var occurrenceScore = new HashMap<Material, Double>();
        var distanceScore = new HashMap<Material, Double>();
        var combinedScores = new HashMap<Material, Double>();

        for (var customSchematicName : AutoStructurePlugin.customSchematics.keySet()) {
            if (!this.associationKey.isEmpty() && !customSchematicName.contains(this.associationKey)) continue;
            var customSchematic = AutoStructurePlugin.customSchematics.get(customSchematicName);
            for (var customSchematicBlock : customSchematic.customSchematicBlocks.ReturnArrayList()) {
                var nMap = GenerateNMap(blockVector3);
                for (var nMapBlockFace : nMap.keySet()) {
                    var neighbour = customSchematicBlock.blockFaceNeighbours.get(nMapBlockFace);
                    if (nMap.get(nMapBlockFace) == neighbour.material || nMap.get(nMapBlockFace) == null) {
                        var neighbourBlock = customSchematicBlock.blockFaceNeighbours.get(customBlockFace);
                        occurrenceScore.put(neighbourBlock.material, occurrenceScore.getOrDefault(neighbourBlock.material, 0.0) + 1);
                        var neighbourExtentDistance = neighbourBlock.distanceToExtent;
                        var ourExtentDistance = customBlockFace.ReturnDistance(blockVector3, region);
                        var newDistanceScore = ourExtentDistance - neighbourExtentDistance;
                        distanceScore.put(neighbourBlock.material, distanceScore.getOrDefault(neighbourBlock.material, 0.0) - newDistanceScore);
                    }
                }
            }
        }

        occurrenceScore.forEach((material, score) -> combinedScores.merge(material, score, Double::sum));
        distanceScore.forEach((material, score) -> combinedScores.merge(material, score, Double::sum));
        for(var mat : ignoreMaterials) combinedScores.remove(mat);
        var sortedHashMap = sortByValue(combinedScores);
        autoStructureGeneration.masterList.put(neighbourOffset, getRandomMaterial(sortedHashMap));
        return neighbourOffset;
    }

    private LinkedHashMap<Material, Double> sortByValue(HashMap<Material, Double> map, Material... ignoreMaterials) {
        List<Map.Entry<Material, Double>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Material, Double>>() {
            public int compare(Map.Entry<Material, Double> o1, Map.Entry<Material, Double> o2) {
                if (Arrays.asList(ignoreMaterials).contains(o1.getKey())) {
                    return 1; // Move ignored materials to the end
                } else if (Arrays.asList(ignoreMaterials).contains(o2.getKey())) {
                    return -1; // Move non-ignored materials to the front
                }
                return o2.getValue().compareTo(o1.getValue()); // Sort non-ignored materials by value in descending order
            }
        });

        LinkedHashMap<Material, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Material, Double> entry : list) sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }

    private HashMap<CustomBlockFace, Material> GenerateNMap(BlockVector3 blockVector3){
        var data = new HashMap<CustomBlockFace, Material>();
        for(var blockFace : CustomBlockFace.values()){
            var nPosition = blockFace.ReturnWithOffset(blockVector3);
            var visited = autoStructureGeneration.blockVector3Visited.contains(nPosition);
            data.put(blockFace, visited ? PlayerWorldEditAPI.GetBlockFromBlockVector(region, nPosition).getType() : null);
        }
        return data;
    }

    private static Material getRandomMaterial(Map<Material, Double> weights) {
        double totalWeight = 0.0;
        for (double weight : weights.values()) totalWeight += weight;
        double randomValue = new Random().nextDouble() * totalWeight;
        for (Map.Entry<Material, Double> entry : weights.entrySet()) {
            randomValue -= entry.getValue();
            if (randomValue <= 0.0) return entry.getKey();
        }
        return weights.keySet().iterator().next();
    }

    private boolean isWithinSquareBounds(BlockVector3 pos, BlockVector3 minPoint, BlockVector3 maxPoint) {
        int minX = Math.min(minPoint.getX(), maxPoint.getX());
        int maxX = Math.max(minPoint.getX(), maxPoint.getX());
        int minY = Math.min(minPoint.getY(), maxPoint.getY());
        int maxY = Math.max(minPoint.getY(), maxPoint.getY());
        int minZ = Math.min(minPoint.getZ(), maxPoint.getZ());
        int maxZ = Math.max(minPoint.getZ(), maxPoint.getZ());
        return pos.getX() >= minX && pos.getX() <= maxX && pos.getY() >= minY && pos.getY() <= maxY && pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }
}

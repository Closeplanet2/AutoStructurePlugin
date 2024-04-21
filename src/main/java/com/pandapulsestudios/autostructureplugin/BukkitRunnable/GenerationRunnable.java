package com.pandapulsestudios.autostructureplugin.BukkitRunnable;

import com.pandapulsestudios.autostructureplugin.API.PlayerWorldEditAPI;
import com.pandapulsestudios.autostructureplugin.Enum.CustomBlockFace;
import com.pandapulsestudios.autostructureplugin.Enum.Message;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructureGeneration;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructurePreGeneration;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Messages;
import com.pandapulsestudios.autostructureplugin.PulseJSON.CustomSchematic;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class GenerationRunnable extends BukkitRunnable {
    private final AutoStructureGeneration autoStructureGeneration;
    private final HashMap<String, CustomSchematic> customSchematics;
    private final Region region;
    private final Player player;
    private final int threadID;

    public GenerationRunnable(AutoStructureGeneration autoStructureGeneration, Player player, Region region, int threadID, HashMap<String, CustomSchematic> customSchematics){
        this.autoStructureGeneration = autoStructureGeneration;
        this.customSchematics = customSchematics;
        this.region = region;
        this.player = player;
        this.threadID = threadID;
        var middlePoint = PlayerWorldEditAPI.ReturnMiddleLocationOffBlockVectors(region);
        VisitBlockVector(middlePoint, autoStructureGeneration.blockVector3Visited);
    }

    @Override
    public void run() {
//        synchronized (autoStructureGeneration) {
//            if(!autoStructureGeneration.running) return;
//            if(autoStructureGeneration.blockVector3Workload.isEmpty()) return;
//            var workload = autoStructureGeneration.blockVector3Workload.remove(0);
//            VisitBlockVector(workload);
//            CheckComplete();
//        }
        cancel();
    }


    private void VisitBlockVector(BlockVector3 blockVector3, HashMap<BlockVector3, Material> blockVector3Visited){
        if(blockVector3Visited.containsKey(blockVector3) || !isWithinArea(blockVector3, region.getMinimumPoint(), region.getMaximumPoint())) return;

        var occurrenceScore = new HashMap<CustomBlockFace, HashMap<Material, Double>>();
        var distanceScore = new HashMap<CustomBlockFace, HashMap<Material, Double>>();

        var visitedNMap = GenerateNMapFromVisited(blockVector3, blockVector3Visited);
        for(var customSchematic : customSchematics.values()){
            for(var customBlock : customSchematic.customSchematicBlocks.ReturnArrayList()) {
                var customBlockNMap = customSchematic.GenerateNMapFromStorage(customBlock.customVector.blockVector3);
                if(DoNMapsMatch(visitedNMap, customBlockNMap)){
                    for(var blockFace : CustomBlockFace.values()){
                        var customBlockMaterial = customBlockNMap.get(blockFace);
                        if(customBlockMaterial == null) continue;
                        var neighbourBlock = customSchematic.ReturnBlockAtBlockVector(blockFace.ReturnWithOffset(blockVector3));

                        var occurrenceScoreBlockFace = occurrenceScore.getOrDefault(blockFace, new HashMap<Material, Double>());
                        var occurrenceScoreMaterial = occurrenceScoreBlockFace.getOrDefault(customBlockMaterial, 0.0);
                        var newScore = occurrenceScoreMaterial + (customBlockMaterial.equals(Material.AIR) ? 1.0 : 10);
                        occurrenceScoreBlockFace.put(customBlockMaterial, newScore);
                        occurrenceScore.put(blockFace, occurrenceScoreBlockFace);

                        var distanceScoreBlockFace = distanceScore.getOrDefault(blockFace, new HashMap<Material, Double>());
                        var distanceScoreMaterial = distanceScoreBlockFace.getOrDefault(customBlockMaterial, 0.0);

                        var neighbourExtentDistance = neighbourBlock.blockFaceNeighbours.get(blockFace).distanceToExtent;
                        var ourExtentDistance = blockFace.ReturnDistance(blockVector3, region);
                        var positionScore = distanceScoreMaterial -(ourExtentDistance - neighbourExtentDistance);

                        distanceScoreBlockFace.put(customBlockMaterial, positionScore);
                        distanceScore.put(blockFace, distanceScoreBlockFace);
                    }
                }
            }
        }

        for(var blockFace : CustomBlockFace.values()){
            var occurrenceScores = occurrenceScore.get(blockFace);
            var distanceScores = distanceScore.get(blockFace);
            var combinedScores = new HashMap<Material, Double>();

            occurrenceScores.forEach((material, score) -> combinedScores.merge(material, score, Double::sum));
            distanceScores.forEach((material, score) -> combinedScores.merge(material, score, Double::sum));
            Bukkit.getConsoleSender().sendMessage(blockFace.chatColor + combinedScores.toString());
        }
    }

    private HashMap<CustomBlockFace, Material> GenerateNMapFromVisited(BlockVector3 blockVector3, HashMap<BlockVector3, Material> blockVector3Visited){
        var data = new HashMap<CustomBlockFace, Material>();
        for(var blockFace : CustomBlockFace.values()){
            var newBlockVector3 = blockFace.ReturnWithOffset(blockVector3);
            var liveMaterial = blockVector3Visited.getOrDefault(newBlockVector3, null);
            data.put(blockFace, liveMaterial);
        }
        return data;
    }

    private boolean DoNMapsMatch(HashMap<CustomBlockFace, Material> visitedNMap, HashMap<CustomBlockFace, Material> customBlockNMap){
        for(var blockFace : CustomBlockFace.values()){
            if(visitedNMap.get(blockFace) == null || customBlockNMap.get(blockFace) == null) continue;
            if(!visitedNMap.get(blockFace).equals(customBlockNMap.get(blockFace))) return false;
        }
        return true;
    }





    private void CheckComplete(){
        var amountVisited = autoStructureGeneration.blockVector3Visited.size();
        var totalSize = autoStructureGeneration.totalSize;
        var percentage = ((double)  amountVisited / totalSize) * 100;
        Messages.ReturnStatic().SendMessageToPlayer(Message.PlayerGenProcess, player, amountVisited, totalSize, percentage);
        if(percentage > 99 && threadID == 0) AutoStructureGeneration.StopGeneration(player);
    }

    private static LinkedHashMap<Material, Double> sortHashMapByValues(Map<Material, Double> map) {
        List<Map.Entry<Material, Double>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Material, Double>>() {
            public int compare(Map.Entry<Material, Double> o1, Map.Entry<Material, Double> o2){
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        LinkedHashMap<Material, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Material, Double> entry : list) sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }

    private static Material getRandomMaterialWeighted(LinkedHashMap<Material, Double> materialMap) {
        double totalWeight = materialMap.values().stream().mapToDouble(Double::doubleValue).sum();

        LinkedHashMap<Material, Double> cumulativeWeights = new LinkedHashMap<>();
        double cumulativeWeight = 0.0;
        for (Map.Entry<Material, Double> entry : materialMap.entrySet()) {
            cumulativeWeight += entry.getValue() / totalWeight;
            cumulativeWeights.put(entry.getKey(), cumulativeWeight);
        }

        double randomValue = Math.random();

        for (Map.Entry<Material, Double> entry : cumulativeWeights.entrySet()) {
            if (randomValue <= entry.getValue()) {
                return entry.getKey();
            }
        }

        return materialMap.keySet().iterator().next();
    }

    private boolean isWithinArea(BlockVector3 pos, BlockVector3 minPoint, BlockVector3 maxPoint) {
        return pos.getX() >= minPoint.getX() && pos.getX() <= maxPoint.getX() &&
                pos.getY() >= minPoint.getY() && pos.getY() <= maxPoint.getY() &&
                pos.getZ() >= minPoint.getZ() && pos.getZ() <= maxPoint.getZ();
    }
}

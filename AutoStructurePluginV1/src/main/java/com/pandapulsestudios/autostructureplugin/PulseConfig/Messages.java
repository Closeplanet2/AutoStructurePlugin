package com.pandapulsestudios.autostructureplugin.PulseConfig;

import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.Enum.Message;
import com.pandapulsestudios.autostructureplugin.Enum.Permission;
import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulseconfig.Interface.PulseConfig;
import com.pandapulsestudios.pulseconfig.Interface.StorageComment;
import com.pandapulsestudios.pulseconfig.Objects.SaveableHashmap;
import com.pandapulsestudios.pulsecore.Chat.ChatAPI;
import com.pandapulsestudios.pulsecore.Chat.MessageType;
import com.pandapulsestudios.pulsecore.Java.PulseAutoRegister;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@PulseAutoRegister
public class Messages implements PulseConfig {
    public static Messages ReturnStatic(){ return (Messages) StorageAPI.ReturnStatic(Messages.class, StorageType.CONFIG, false); }

    @Override
    public JavaPlugin mainClass() { return AutoStructurePlugin.AutoStructurePlugin; }

    @Override
    public void FirstLoadConfig() {
        for(var message : Message.values()){
            if(!messages.containsKey(message)) messages.put(message, message.message);
        }
    }

    @Override
    public void AfterLoadConfig() {
        for(var message : Message.values()){
            if(!messages.containsKey(message)) messages.put(message, message.message);
        }
        StorageAPI.Save(this, debugConfig);
    }

    @Override
    public void BeforeSaveConfig() {
        for(var message : Message.values()){
            if(!messages.containsKey(message)) messages.put(message, message.message);
        }
    }

    @StorageComment("Messages to be displayed on the server! Leave blank and message wont be sent! Remove to reset to default!")
    public SaveableHashmap<Message, String> messages = new SaveableHashmap<>(Message.class, String.class);
    @StorageComment("Display debugs in the console logs for changes in this config!")
    public boolean debugConfig = false;

    public void SendMessageToBroadcast(Message message, Object... objects){
        var storedMessage = messages.getOrDefault(message, message.message);
        if(storedMessage.isEmpty()) return;
        ChatAPI.chatBuilder().messageType(MessageType.Broadcast).SendMessage(String.format(storedMessage, objects));
    }

    public void SendMessageToWorld(Message message, String world, Object... objects){
        var storedMessage = messages.getOrDefault(message, message.message);
        if(storedMessage.isEmpty()) return;
        ChatAPI.chatBuilder().messageType(MessageType.WORLD).permWorldRegionData(world).SendMessage(String.format(storedMessage, objects));
    }

    public void SendMessageToPerm(Message message, Permission permission, Object... objects){
        var storedMessage = messages.getOrDefault(message, message.message);
        if(storedMessage.isEmpty()) return;
        ChatAPI.chatBuilder().messageType(MessageType.PERM).permWorldRegionData(permission.permission).SendMessage(String.format(storedMessage, objects));
    }

    public void SendMessageToPlayer(Message message, Player player, Object... objects){
        if(player == null || message == null) return;
        var storedMessage = messages.getOrDefault(message, message.message);
        if(storedMessage.isEmpty()) return;
        ChatAPI.chatBuilder().messageType(MessageType.Player).playerToo(player).SendMessage(String.format(storedMessage, objects));
    }

    public void SendMessageToConsole(Message message, Object... objects){
        if(message == null) return;
        var storedMessage = messages.getOrDefault(message, message.message);
        if(storedMessage.isEmpty()) return;
        ChatAPI.chatBuilder().SendMessage(String.format(storedMessage, objects));
    }
}

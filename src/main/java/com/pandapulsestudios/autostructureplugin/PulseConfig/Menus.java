package com.pandapulsestudios.autostructureplugin.PulseConfig;

import com.pandapulsestudios.autostructureplugin.AutoStructurePlugin;
import com.pandapulsestudios.autostructureplugin.Enum.Menu;
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
public class Menus implements PulseConfig {
    public static Menus ReturnStatic(){ return (Menus) StorageAPI.ReturnStatic(Menus.class, StorageType.CONFIG, false); }

    @Override
    public JavaPlugin mainClass() { return AutoStructurePlugin.AutoStructurePlugin; }

    @Override
    public void FirstLoadConfig() {
        for(var menu : Menu.values()){
            if(!menus.containsKey(menu)) menus.put(menu, menu.menu);
        }
    }

    @Override
    public void AfterLoadConfig() {
        for(var menu : Menu.values()){
            if(!menus.containsKey(menu)) menus.put(menu, menu.menu);
        }
        StorageAPI.Save(this, debugConfig);
    }

    @Override
    public void BeforeSaveConfig() {
        for(var menu : Menu.values()){
            if(!menus.containsKey(menu)) menus.put(menu, menu.menu);
        }
    }

    @StorageComment("Stored Menus On The Server!")
    public SaveableHashmap<Menu, String[]> menus = new SaveableHashmap<>( Menu.class, String[].class);
    @StorageComment("Do you send player error message if doesn't have permission?")
    public boolean sendPlayerErrorMessage = true;
    @StorageComment("Display debugs in the console logs for changes in this config!")
    public boolean debugConfig = false;

    public void DisplayMenuToPlayer(Menu menu, Player player){
        if(player == null || menu == null) return;
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(menu.permission, player, sendPlayerErrorMessage)) return;
        for(var line : menus.getOrDefault(menu, menu.menu)) ChatAPI.chatBuilder().messageType(MessageType.Player).playerToo(player).SendMessage(line);
    }
}

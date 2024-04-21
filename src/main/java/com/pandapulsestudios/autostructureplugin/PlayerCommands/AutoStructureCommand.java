package com.pandapulsestudios.autostructureplugin.PlayerCommands;

import com.pandapulsestudios.autostructureplugin.API.PlayerWorldEditAPI;
import com.pandapulsestudios.autostructureplugin.Enum.Menu;
import com.pandapulsestudios.autostructureplugin.Enum.Message;
import com.pandapulsestudios.autostructureplugin.Enum.Permission;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructureGeneration;
import com.pandapulsestudios.autostructureplugin.Objects.AutoStructurePreGeneration;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Menus;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Messages;
import com.pandapulsestudios.autostructureplugin.PulseConfig.Permissions;
import com.pandapulsestudios.pulsecommands.Interface.PCAutoTab;
import com.pandapulsestudios.pulsecommands.Interface.PCMethod;
import com.pandapulsestudios.pulsecommands.Interface.PCSignature;
import com.pandapulsestudios.pulsecommands.PlayerCommand;
import com.pandapulsestudios.pulseconfig.API.StorageAPI;
import com.pandapulsestudios.pulseconfig.Enum.StorageType;
import com.pandapulsestudios.pulsecore.Java.PulseAutoRegister;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

@PulseAutoRegister
public class AutoStructureCommand extends PlayerCommand {
    public AutoStructureCommand() {
        super("autostruct", true, "astruct");
    }

    @Override
    public void NoMethodFound(CommandSender commandSender, String s, String[] strings) {
        Menus.ReturnStatic().DisplayMenuToPlayer(Menu.HELP_MENU, (Player) commandSender);
    }

    @PCMethod
    @PCSignature({"configs", "reset"})
    public void ResetConfigs(CraftPlayer admin){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.RESET_CONFIGS, admin, true)) return;
        StorageAPI.ResetStatic(Menus.class, StorageType.CONFIG, false, Menus.ReturnStatic().debugConfig);
        StorageAPI.ResetStatic(Messages.class, StorageType.CONFIG, false, Messages.ReturnStatic().debugConfig);
        StorageAPI.ResetStatic(Permissions.class, StorageType.CONFIG, false, Permissions.ReturnStatic().debugConfig);
        Messages.ReturnStatic().SendMessageToPlayer(Message.PlayerResetConfig, admin);
    }

    @PCMethod
    @PCSignature({"configs", "reload"})
    public void ReloadConfigs(CraftPlayer admin){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.RELOAD_CONFIGS, admin, true)) return;
        StorageAPI.ReloadStatic(Menus.class, StorageType.CONFIG, false, Menus.ReturnStatic().debugConfig);
        StorageAPI.ReloadStatic(Messages.class, StorageType.CONFIG, false, Messages.ReturnStatic().debugConfig);
        StorageAPI.ReloadStatic(Permissions.class, StorageType.CONFIG, false, Permissions.ReturnStatic().debugConfig);
        Messages.ReturnStatic().SendMessageToPlayer(Message.PlayerReloadedConfig, admin);
    }

    @PCMethod
    @PCSignature({"pretrain", "start"})
    public void PreTrainDataStart(CraftPlayer admin){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.PRE_TRAIN_COMMAND, admin, true)) return;
        AutoStructurePreGeneration.StartPreGeneration();
        Messages.ReturnStatic().SendMessageToPerm(Message.AdminPreTrainStarted, Permission.ADMIN_MESSAGES);
    }

    @PCMethod
    @PCSignature({"pretrain", "toggle"})
    @PCAutoTab(pos = 1)
    public void PreTrainDataToggle(CraftPlayer admin, boolean state){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.PRE_TRAIN_COMMAND, admin, true)) return;
        AutoStructurePreGeneration.TogglePreGeneration(state);
        Messages.ReturnStatic().SendMessageToPerm(state ? Message.AdminPreTrainStarted : Message.AdminPreTrainPaused, Permission.ADMIN_MESSAGES);
    }

    @PCMethod
    @PCSignature({"pretrain", "stop"})
    public void PreTrainDataStop(CraftPlayer admin){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.PRE_TRAIN_COMMAND, admin, true)) return;
        AutoStructurePreGeneration.StopPreGeneration();
        Messages.ReturnStatic().SendMessageToPerm(Message.AdminPreTrainStopped, Permission.ADMIN_MESSAGES);
    }

    @PCMethod
    @PCSignature({"gen", "start"})
    public void GenDataStart(CraftPlayer admin){
        GenDataStart(admin, "");
    }

    @PCMethod
    @PCSignature({"gen", "start"})
    public void GenDataStart(CraftPlayer admin, String associationKey){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.GEN_COMMAND, admin, true)) return;

        var playerRegion = PlayerWorldEditAPI.GetPlayerLocalSession(admin);
        if(playerRegion == null){
            Messages.ReturnStatic().SendMessageToPlayer(Message.PlayerMustHaveWorldEditSelection, admin);
            return;
        }

        var startState = AutoStructureGeneration.StartGeneration(admin, playerRegion, associationKey);
        var storedMessage = startState ? Message.PlayerGenStarted : Message.PlayerGenCantStart;
        Messages.ReturnStatic().SendMessageToPlayer(storedMessage, admin);
    }

    @PCMethod
    @PCSignature({"gen", "toggle"})
    public void GenDataToggle(CraftPlayer admin, boolean state){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.GEN_COMMAND, admin, true)) return;
        AutoStructureGeneration.ToggleGeneration(admin, state);
        var storedMessage = state ? Message.PlayerGenStarted : Message.PlayerGenPaused;
        Messages.ReturnStatic().SendMessageToPlayer(storedMessage, admin);
    }

    @PCMethod
    @PCSignature({"gen", "stop"})
    public void GenDataStop(CraftPlayer admin){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.GEN_COMMAND, admin, true)) return;
        AutoStructureGeneration.StopGeneration(admin);
        Messages.ReturnStatic().SendMessageToPlayer(Message.PlayerGenStopped, admin);
    }

    @PCMethod
    @PCSignature({"gen", "place"})
    public void GenDataPlace(CraftPlayer admin){
        if(!Permissions.ReturnStatic().DoesPlayerHavePermission(Permission.GEN_COMMAND, admin, true)) return;
        AutoStructureGeneration.PlaceGeneration(admin);
    }

}

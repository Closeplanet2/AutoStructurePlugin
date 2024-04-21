package com.pandapulsestudios.autostructureplugin.Enum;

public enum Permission {
    MENU_HELP("AutoStructure.MENU_HELP", "#fa3448You do not have the permissions to view this menu!"),
    ADMIN_MESSAGES("AutoStructure.ADMIN_MESSAGES", ""),
    RELOAD_CONFIGS("AutoStructure.RELOAD_CONFIGS", "#fa3448You do not have the permissions to use this command!"),
    RESET_CONFIGS("AutoStructure.RESET_CONFIGS", "#fa3448You do not have the permissions to use this command!"),
    PRE_TRAIN_COMMAND("AutoStructure.PRE_TRAIN_COMMAND", "#fa3448You do not have the permissions to use this command!"),
    GEN_COMMAND("AutoStructure.GEN_COMMAND", "#fa3448You do not have the permissions to use this command!");

    public final String permission;
    public final String error;
    Permission(String permission, String error){
        this.permission = permission;
        this.error = error;
    }
}

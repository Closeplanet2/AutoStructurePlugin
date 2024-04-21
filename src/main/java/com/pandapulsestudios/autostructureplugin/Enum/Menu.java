package com.pandapulsestudios.autostructureplugin.Enum;

public enum Menu {
    HELP_MENU(Permission.MENU_HELP,"#85FE9E[#D89B03AUTO STRUCTURE#85FE9E]",
            "#85FE9E/autostruct configs reload -> #03D86EReload The Configs!",
            "#85FE9E/autostruct configs restart -> #03D86ERestart The Configs!",
            "#85FE9E/autostruct pretrain Start-> #03D86EPre train the data!",
            "#85FE9E/autostruct pretrain Pause-> #03D86EPause training the data!",
            "#85FE9E/autostruct pretrain Stop-> #03D86EStop training the data!",
            "#85FE9E/autostruct gen start <Key>-> #03D86EStart generation using all schematics with <key> in name!",
            "#85FE9E/autostruct gen pause -> #03D86EPause generation",
            "#85FE9E/autostruct gen stop -> #03D86EStop generation");

    public final String[] menu;
    public final Permission permission;
    Menu(Permission permission, String... menu) {
        this.permission = permission;
        this.menu = menu;
    }
}

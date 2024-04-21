package com.pandapulsestudios.autostructureplugin.Enum;

public enum Message {
    ConsoleEnabledSystem("#7fff36System has been enabled!"),
    ConsoleDisableSystem("#fa3448System has been disabled!"),
    PlayerReloadedConfig("#7fff36You have reloaded the configs!"),
    PlayerResetConfig("#7fff36You have reset the configs!"),
    AdminPreTrainStarted("#7fff36Pre train data started!"),
    PlayerGenStarted("#fa3448Gen structure has started!"),
    PlayerGenPaused("#fa3448Gen structure has been paused!"),
    PlayerGenStopped("#fa3448Gen structure has stopped!"),
    PlayerGenComplete("#7fff36Gen structure has finished!"),
    PlayerGenProcess("#fa3448Gen Process: (#5eb4ff%d#fa3448/#5eb4ff%d#fa3448) #5eb4ff%f#fa3448 Percent"),
    PlayerGenCantStart("#fa3448Gen structure cant be started at this time!"),
    PlayerMustHaveWorldEditSelection("#fa3448You must have a world edit selection to gen a structure!"),
    AdminPreTrainPaused("#fa3448Pre train data paused!"),
    AdminPreTrainStopped("#fa3448Pre Train has been stopped!"),
    AdminPreTrainComplete("#7fff36Pre train data has finished!"),
    AdminPreTrainProcess("#fa3448Pre Train Process: (#5eb4ff%d#fa3448/#5eb4ff%d#fa3448) #5eb4ff%f#fa3448 Percent");

    public final String message;
    Message(String message){
        this.message = message;
    }
}

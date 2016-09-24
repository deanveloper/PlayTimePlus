package com.deanveloper.playtimeplus.commands.playtimeplus;

/**
 * @author Dean
 */
public interface SubCommandExecutor {
    String[] NO_ALIASES = new String[]{};

    void execute(SubCommandCall call);
    String getName();
    String[] getAliases();
    boolean canConsoleExecute();
    String getDesc();
    String getUsage();
    String getPermission();
}

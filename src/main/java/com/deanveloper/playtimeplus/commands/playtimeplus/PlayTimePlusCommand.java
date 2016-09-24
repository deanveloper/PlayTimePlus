package com.deanveloper.playtimeplus.commands.playtimeplus;

import com.deanveloper.playtimeplus.commands.playtimeplus.subcommand.OtherSubCmd;
import com.deanveloper.playtimeplus.commands.playtimeplus.subcommand.SelfSubCmd;
import com.deanveloper.playtimeplus.commands.playtimeplus.subcommand.TopSubCmd;

/**
 * @author Dean B
 */
public class PlayTimePlusCommand extends SubCommandAble {
    public PlayTimePlusCommand() {
        super(
                new SelfSubCmd(),
                new TopSubCmd(),
                new OtherSubCmd()
        );
    }
}

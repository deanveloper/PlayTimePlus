package com.deanveloper.playtimeplus.commands.playtime;

import com.deanveloper.playtimeplus.commands.playtime.subcommand.OtherSubCmd;
import com.deanveloper.playtimeplus.commands.playtime.subcommand.QuerySubCmd;
import com.deanveloper.playtimeplus.commands.playtime.subcommand.SelfSubCmd;
import com.deanveloper.playtimeplus.commands.playtime.subcommand.TopSubCmd;

/**
 * @author Dean B
 */
public class PlayTimeCommand extends SubCommandAble {
    public PlayTimeCommand() {
        super(
                new SelfSubCmd(),
                new TopSubCmd(),
                new OtherSubCmd(),
                new QuerySubCmd()
        );
    }
}

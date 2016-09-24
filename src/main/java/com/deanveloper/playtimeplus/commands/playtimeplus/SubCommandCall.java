package com.deanveloper.playtimeplus.commands.playtimeplus;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author Dean
 */
public final class SubCommandCall {
    private static final String[] EMPTY = new String[] {};
    private final CommandSender sender;
    private final Command command;
    private final String label;
    private final String[] args;

    public SubCommandCall(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        this.command = command;
        this.label = label;
        if(args == null) {
            this.args = EMPTY;
        } else {
            this.args = Arrays.copyOfRange(args, 1, args.length);
        }
    }

    public Player getPlayer() {
        return (Player) sender;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Command getCommand() {
        return command;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }

    public void sendBack(String format, Object... args) {
        sender.sendMessage("§a[PlayTimePlus] §r" + String.format(format, args));
    }
}

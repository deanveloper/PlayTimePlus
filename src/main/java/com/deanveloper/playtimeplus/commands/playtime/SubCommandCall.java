package com.deanveloper.playtimeplus.commands.playtime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Dean
 */
public final class SubCommandCall {
	private static final String[] EMPTY = new String[]{};
	private final CommandSender sender;
	private final Command command;
	private final String label;
	private final String[] args;

	public SubCommandCall(CommandSender sender, Command command, String label, String[] args) {
		this.sender = sender;
		this.command = command;
		this.label = label;
		if (args == null) {
			this.args = EMPTY;
		} else {
			// do not include first arg
			this.args = new String[args.length - 1];

			for (int i = 1; i < args.length; i++) {
				this.args[i - 1] = args[i].toLowerCase();
			}
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
		sender.sendMessage(String.format(format, args));
	}
}

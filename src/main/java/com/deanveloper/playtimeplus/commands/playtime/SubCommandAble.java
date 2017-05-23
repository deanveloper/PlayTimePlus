package com.deanveloper.playtimeplus.commands.playtime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.deanveloper.playtimeplus.util.Utils;

import java.util.*;

/**
 * @author Dean
 */
public class SubCommandAble implements CommandExecutor {
    private Set<SubCommandExecutor> subCommands = new HashSet<>();
    private Map<String, SubCommandExecutor> subCommandMap = new HashMap<>();

    public SubCommandAble(SubCommandExecutor... subCmds) {
        for (SubCommandExecutor subCmd : subCmds) {
            subCommandMap.put(subCmd.getName(), subCmd);
            subCommands.add(subCmd);

            for (String alias : subCmd.getAliases()) {
                subCommandMap.put(alias, subCmd);
            }
        }
    }


    @Override
    public final boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sendUsages(sender, label);
        } else {
            SubCommandExecutor subCmd = subCommandMap.get(args[0]);
            if (subCmd == null) {
                sendUsages(sender, label);
            } else {
                try {
                    if (sender.hasPermission(subCmd.getPermission())) {
                        subCmd.execute(new SubCommandCall(sender, cmd, args[0], args));
                    } else {
                        sender.sendMessage("§cYou don't have permission to do that!");
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    sender.sendMessage("§aUsage: /" + label + " " + args[0].toLowerCase() + " §d" + subCmd.getUsage());
                } catch (Exception e) {
                    sender.sendMessage(
                            Utils.configMessage("messages.cmd.playtime.error",
                                    sender.getName(),
                                    "",
                                    "",
                                    "",
                                    e.getMessage()
                            )
                    );
                }
            }
        }
        return true;
    }

    private void sendUsages(CommandSender sender, String label) {
        StringJoiner join = new StringJoiner("\n");
        subCommands.stream()
                .filter(subCmd -> sender.hasPermission(subCmd.getPermission()))
                .filter(subCmd -> sender instanceof Player || subCmd.canConsoleExecute())
                .forEach(subCmd ->
                        join.add(String.format(
                                "§b/%s %s §d%s §e%s",
                                label,
                                subCmd.getName(),
                                subCmd.getUsage(),
                                subCmd.getDesc()
                        ))
                );

        sender.sendMessage("§6Usage: " + label + " <command> [args...]");
        sender.sendMessage(join.toString());
    }
}

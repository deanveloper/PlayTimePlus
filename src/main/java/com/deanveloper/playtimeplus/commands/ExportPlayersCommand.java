package com.deanveloper.playtimeplus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.deanveloper.playtimeplus.PlayTimePlus;
import com.deanveloper.playtimeplus.exporter.CsvExporter;
import com.deanveloper.playtimeplus.exporter.Exporter;
import com.deanveloper.playtimeplus.exporter.JsonExporter;
import com.deanveloper.playtimeplus.exporter.PlainTextExporter;
import com.deanveloper.playtimeplus.storage.StorageMethod;
import com.deanveloper.playtimeplus.storage.TimeEntry;
import com.deanveloper.playtimeplus.util.ConfigVar;
import com.deanveloper.playtimeplus.util.Utils;
import com.deanveloper.playtimeplus.util.query.QueryException;
import com.deanveloper.playtimeplus.util.query.QueryUtil;

import java.util.*;

/**
 * Command to export players
 *
 * @author Dean B
 */
public class ExportPlayersCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length > 0) {
			args[0] = args[0].toUpperCase();

			try {
				Exporter exporter = FileType.valueOf(args[0]).getExporter();

				Map<UUID, NavigableSet<TimeEntry>> players;
				if (args.length == 1) {
					players = new HashMap<>(PlayTimePlus.getManager().getMap());
				} else {
					players = QueryUtil.query(Arrays.copyOfRange(args, 1, args.length));
				}

				exporter.export(players);

				sender.sendMessage(
						Utils.configMessage(
								"messages.cmd.exportplayers.success",
								new ConfigVar("sender", sender.getName())
						)
				);
			} catch (IllegalArgumentException e) {
				// use ConvertStorage command stuff here since it should be the same no matter what
				sender.sendMessage(
						Utils.configMessage(
								"messages.cmd.convertstorage.error.header",
								new ConfigVar("sender", sender.getName())
						)
				);
				for (StorageMethod type : StorageMethod.values()) {
					sender.sendMessage(
							Utils.configMessage(
									"messages.cmd.convertstorage.error.eachMethod",
									new ConfigVar("sender", sender.getName()),
									new ConfigVar("type", type.name()),
									new ConfigVar("desc", type.getDesc())
							)
					);
				}
			} catch (QueryException e) {
				sender.sendMessage(Utils.configMessage(
						"messages.cmd.exportplayers.error",
						sender.getName(),
						"",
						"",
						"",
						e.getMessage()
				));
			}

			return true;
		}
		return false;
	}

	enum FileType {
		JSON("Exports to a parsable JavaScript object file", new JsonExporter()),
		TXT("A nice, human-readable file", new PlainTextExporter()),
		CSV("Exports to a spreadsheet-parsable file", new CsvExporter());

		private String desc;
		private Exporter exp;

		FileType(String desc, Exporter exp) {
			this.desc = desc;
			this.exp = exp;
		}

		public String getDesc() {
			return desc;
		}

		public Exporter getExporter() {
			return exp;
		}
	}
}

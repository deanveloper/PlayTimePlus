package com.deanveloper.playtime.commands;

import com.deanveloper.playtime.PlayTime;
import com.deanveloper.playtime.exporter.CsvExporter;
import com.deanveloper.playtime.exporter.Exporter;
import com.deanveloper.playtime.exporter.JsonExporter;
import com.deanveloper.playtime.exporter.PlainTextExporter;
import com.deanveloper.playtime.storage.Storage;
import com.deanveloper.playtime.util.QuickSort;
import com.deanveloper.playtime.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

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
                Exporter exp = FileType.valueOf(args[0]).getExporter();

                int length = Bukkit.getOfflinePlayers().length;
                List<String> names = new ArrayList<>(length);
                List<UUID> ids = new ArrayList<>(length);
                List<Integer> times = new ArrayList<>(length);

                QuickSort quickSorter = new QuickSort();
                //populate lists
                for (Entry<UUID, Storage.PlayerEntry> entry : PlayTime.getPlayerDb().getPlayers().entrySet()) {
                    UUID id = entry.getKey();
                    String name = Utils.getName(id);
                    Storage.PlayerEntry player = entry.getValue();

                    names.add(name);
                    ids.add(id);
                    times.add(player.getTimes());
                }

                //sort by time
                quickSorter.sort(names, ids, times);

                exp.export(names, ids, times);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§6File Types");
                for (FileType type : FileType.values()) {
                    sender.sendMessage("§d" + type.name() + " §b- " + "§a" + type.getDesc());
                }
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

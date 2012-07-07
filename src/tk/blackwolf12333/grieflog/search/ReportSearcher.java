package tk.blackwolf12333.grieflog.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import tk.blackwolf12333.grieflog.GriefLog;

public class ReportSearcher extends Searcher {

	public ReportSearcher() {
		files.add(GriefLog.reportFile);
	}
	
	@Override
	public String searchText(String arg0) {
		return searchFile(arg0);
	}

	@Override
	public String searchText(String arg0, String arg1) {
		return searchFile(arg0, arg1);
	}

	@Override
	public String searchText(String arg0, String arg1, String arg2) {
		return searchFile(arg0, arg1, arg2);
	}

	@Override
	public String searchPos(int x, int y, int z) {
		String xyz = x + ", " + y + ", " + z;
		return searchFile(xyz);
	}

	@Override
	public void readReportFile(File file, CommandSender sender) {
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			String line = "";

			sender.sendMessage(ChatColor.RED + "+++++++++ReportStart+++++++++");
			while ((line = br.readLine()) != null) {
				sender.sendMessage(line);
			}
			sender.sendMessage(ChatColor.RED + "++++++++++ReportEnd+++++++++");

			br.close();
			fileReader.close();

		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "No Reports have been found!");
		}
	}
}
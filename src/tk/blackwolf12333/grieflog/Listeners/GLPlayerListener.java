package tk.blackwolf12333.grieflog.Listeners;

import java.io.File;
import java.net.InetAddress;
import java.util.logging.Logger;

//import org.bukkit.Chunk;
import org.bukkit.GameMode;
//import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.player.PlayerTeleportEvent;

import tk.blackwolf12333.grieflog.GriefLog;
import tk.blackwolf12333.grieflog.GriefLogger;
import tk.blackwolf12333.grieflog.utils.FileUtils;
import tk.blackwolf12333.grieflog.utils.Time;
//import tk.blackwolf12333.grieflog.commands.GLTool; *i might gonna use this once

public class GLPlayerListener implements Listener{
	
	Logger log = Logger.getLogger("Minecraft");
	GriefLog gl;
	Time t = new Time();
	FileUtils fu = new FileUtils();
	GriefLogger logger;
	
	public GLPlayerListener(GriefLog plugin) {
		gl = plugin;
		logger = new GriefLogger(plugin);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) 
	{
		if(gl.getConfig().getBoolean("ChangeGameMode"))
		{
			Player player = event.getPlayer();
			String p = player.getName();
			GameMode gm = event.getNewGameMode();
			int gameM = gm.getValue();
			World world = player.getWorld();
			String playerWorld = world.getName();		
			
			String data = t.now() + " [GAMEMODE_CHANGE] " + p + " New Gamemode: " + gameM + " Where: " + playerWorld + System.getProperty("line.separator");
			logger.Log(data);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		
		if(gl.getConfig().getBoolean("ChangeWorld"))
		{
			Player player = event.getPlayer();
			String playerName = player.getName();
			World where = event.getFrom();
			String from = where.getName();
			
			String data = t.now() + " [WORLD_CHANGE] Who: " + playerName + " From: " + from + System.getProperty("line.separator");
			logger.Log(data);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		
		if(gl.getConfig().getBoolean("DoCommand"))
		{
			String cmd = event.getMessage();
			String namePlayer = event.getPlayer().getName();
				
			String data = t.now() + " [PLAYER_COMMAND] Who: " + namePlayer + " Command: " + cmd + System.getProperty("line.separator");
			logger.Log(data);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		Player p = event.getPlayer();
		// check if the player is op, if so, tell him/her, if there are new reports
		if(p.isOp())
		{
			if(GriefLog.reportFile.exists())
			{
				event.getPlayer().sendMessage("There Are New Player Reports!\n");
				event.getPlayer().sendMessage("Type /glog read to read the reports");
			}
			else
			{
				// if not do nothing
			}
		}
		
		if(gl.getConfig().getBoolean("PlayerJoin"))
		{
			InetAddress address = event.getPlayer().getAddress().getAddress();
			int gm = event.getPlayer().getGameMode().getValue();
			String name = event.getPlayer().getName();
			String worldName = event.getPlayer().getWorld().getName();
			
			String data = t.now() + " [PLAYER_LOGIN] " + name + " On: " + address.getHostAddress() + " With GameMode: " + gm + " In: " + worldName + System.getProperty("line.separator");
			logger.Log(data);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action a = event.getAction();
		Player p = event.getPlayer();
		// check if the player left clicked a block
		if(a == Action.LEFT_CLICK_BLOCK)
		{
			// check if the item in hand of the player == the selection tool specified in the config file
			if(p.getItemInHand().getTypeId() == gl.getConfig().getInt("SelectionTool"))
			{
				Block b = event.getClickedBlock();
				
				int x = b.getX();
				int y = b.getY();
				int z = b.getZ();
				
				// search through the log files, if there isn't a logs/ directory search in the current file
				File file = new File("logs/");
				String[] list = file.list();
				if(list == null)
				{
					fu.searchText(x + ", " + y + ", " + z, GriefLog.file, p);
					event.setCancelled(true);
					return;
				}
				else
				{
					for(int i = 0; i < list.length; i++)
					{
						// if there are other files, search them, if it is found, 
						// break the loop so that you don't search the other files for nothing
						if(fu.searchText(x + ", " + y + ", " + z, new File("logs" + File.separator + list[i]), p))
						{
							break;
						}
					}
				}
				event.setCancelled(true);
			}
		}
	}
}

package tk.blackwolf12333.grieflog.utils.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import tk.blackwolf12333.grieflog.GriefLog;

public class ConfigHandler {

	public static GriefLog plugin;
	public static File configFile;
	public static File friendsFile;
	public static FileConfiguration config;
	public static FileConfiguration friendsConfig;
	
	public static ConfigValues values = new ConfigValues();
	
	public ConfigHandler(GriefLog plugin) {
		ConfigHandler.plugin = plugin;
	}
	
	public static void setupGriefLogConfig() {
		configFile = new File(plugin.getDataFolder(), "config.yml");
	    config = new YamlConfiguration();
	    
	    if(!configFile.exists()) {
	    	configFile.getParentFile().mkdirs();
	    	try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        copy(plugin.getResource("config.yml"), configFile);
	        loadConfig();
	    } else {
	    	checkForChangesAndLoad();
	    }
	}
	
	public static void setupFriendsConfig() {
		friendsFile = new File(plugin.getDataFolder(), "friends.yml");
	    friendsConfig = new YamlConfiguration();

	    if(!friendsFile.exists()) {
	    	friendsFile.getParentFile().mkdirs();
	        copy(plugin.getResource("friends.yml"), friendsFile);
	        loadFriendsConfig();
	    } else {
	    	loadFriendsConfig();
	    }
	}
	
	private static void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void saveConfig() {
	    try {
	    	GriefLog.log.info("Saving configuration file.");
	        config.save(configFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void saveFriendsConfig() {
	    try {
	        friendsConfig.save(friendsFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public static void loadConfig() {
	    try {
	        config.load(configFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (InvalidConfigurationException e) {
	    	
	    }
	}
	
	public static void loadFriendsConfig() {
	    try {
	        friendsConfig.load(friendsFile);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static void reloadConfig() {
		loadConfig();
	}
	
	public static void reloadFriendsConfig() {
		loadFriendsConfig();
	}
	
	private static void checkForChangesAndLoad() {
		try {
			loadConfig();
			Map<String, Object> oldConf = config.getValues(true);
			
			FileConfiguration newconfig = new YamlConfiguration();
			newconfig.load(plugin.getResource("config.yml"));
			Map<String, Object> newConf = newconfig.getValues(true);
			
			if(newConf.size() > oldConf.size()) {
				configFile.delete();
				
				createNewConfigFileAndLoad();
				List<String> contents = readFileAndPutContentsInList();
				for(Iterator<String> it = oldConf.keySet().iterator(); it.hasNext();) {
		        	String next = it.next();
		        	
		        	String newNode = next + ": " + oldConf.get(next);
		        	for(int i = 0; i < contents.size(); i++) {
		        		String line = contents.get(i);
		        		if(line.contains(next)) {
		        			contents.set(i, newNode);
		    			} else {
		    				continue;
		    			}
		        	}
		    		
		        }
				writeListToFileAndLoad(contents);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// ignore this exception
		}
	}
	
	private static List<String> readFileAndPutContentsInList() throws IOException, FileNotFoundException {
		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		String line = null;
		List<String> contents = new ArrayList<String>();
		while((line = reader.readLine()) != null) {
			contents.add(line);
		}
		reader.close();
		
		return contents;
	}
	
	private static void writeListToFileAndLoad(List<String> list) throws IOException, InvalidConfigurationException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
		for(int i = 0; i < list.size(); i++) {
			bw.append(list.get(i));
			bw.newLine();
		}
		bw.close();
		loadConfig();
	}
	
	private static void createNewConfigFileAndLoad() {
		try {
			configFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        copy(plugin.getResource("config.yml"), configFile);
        loadConfig();
	}
	
	public static List<String> getFriends(String player) {
		if(friendsConfig.getStringList(player) == null) {
			return new ArrayList<String>();
		}
		return friendsConfig.getStringList(player);
	}
	
	public static boolean isOnFriendsList(String player, Player friend) {
		if(friend.isOp()) {
			return true;
		} else if(friend.getName().equalsIgnoreCase(player)) {
			return true;
		} else {
			if(ConfigHandler.getFriends(player) != null) {
				return ConfigHandler.getFriends(player).contains(friend.getName());
			}
			return false;
		}
	}
}

package dev.bjarne.playerMilk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class main extends JavaPlugin implements Listener {

	public eventHandler eH;
	private int playersMilked;
	private int serviceId = 20828;
	public boolean enableEffect;

	public void onEnable() {
		// creating plugin dir
		File Dir1 = new File("plugins" + File.separator + "PlayerMilk");
		if (!Dir1.exists())
			Dir1.mkdir();

		// create the config.yml
		File configFile = new File("plugins" + File.separator + "PlayerMilk" + File.separator + "config.yml");
		if (!configFile.exists()) {
			copy(getResource("config.yml"), configFile);
		}

		// check, if effect should be given to player after drinking the milk
		enableEffect = getConfig().getBoolean("effects.enabled");

		eH = new eventHandler(enableEffect);
		Bukkit.getPluginManager().registerEvents(eH, this);
		eH.setMain(this);

		playersMilked = 0; // number of player milk events

		Metrics metrics = new Metrics(this, serviceId);

		// the metrics custom line chart
		metrics.addCustomChart(new Metrics.SingleLineChart("players_milked", new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				return playersMilked;
			}
		}));

		Bukkit.getConsoleSender().sendMessage("[PlayerMilk]" + ChatColor.LIGHT_PURPLE + "Player" + ChatColor.BLUE
				+ "Milk" + ChatColor.RESET + "V1.1 has been enabled");
	}

	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("[PlayerMilk]" + ChatColor.LIGHT_PURPLE + "Player" + ChatColor.BLUE
				+ "Milk" + ChatColor.RESET + "V1.1 has been disabled");
	}

	/**
	 * Method to transform Minecraft color codes
	 * 
	 * @param s
	 * @return
	 */
	public static String makeColors(String s) {
		String replaced = s.replaceAll("&0", "" + ChatColor.BLACK).replaceAll("&1", "" + ChatColor.DARK_BLUE)
				.replaceAll("&2", "" + ChatColor.DARK_GREEN).replaceAll("&3", "" + ChatColor.DARK_AQUA)
				.replaceAll("&4", "" + ChatColor.DARK_RED).replaceAll("&5", "" + ChatColor.DARK_PURPLE)
				.replaceAll("&6", "" + ChatColor.GOLD).replaceAll("&7", "" + ChatColor.GRAY)
				.replaceAll("&8", "" + ChatColor.DARK_GRAY).replaceAll("&9", "" + ChatColor.BLUE)
				.replaceAll("&a", "" + ChatColor.GREEN).replaceAll("&b", "" + ChatColor.AQUA)
				.replaceAll("&c", "" + ChatColor.RED).replaceAll("&d", "" + ChatColor.LIGHT_PURPLE)
				.replaceAll("&e", "" + ChatColor.YELLOW).replaceAll("&f", "" + ChatColor.WHITE)
				.replaceAll("&r", "" + ChatColor.RESET).replaceAll("&l", "" + ChatColor.BOLD)
				.replaceAll("&o", "" + ChatColor.ITALIC).replaceAll("&k", "" + ChatColor.MAGIC)
				.replaceAll("&m", "" + ChatColor.STRIKETHROUGH).replaceAll("&n", "" + ChatColor.UNDERLINE)
				.replaceAll("\\\\", " ");
		return replaced;
	}

	/**
	 * copies a File from an inputStream
	 * 
	 * @param in
	 * @param file
	 */
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void incrPlayersMilked() {
		playersMilked++;
	}
}

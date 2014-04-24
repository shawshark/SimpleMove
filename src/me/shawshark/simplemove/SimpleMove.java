package me.shawshark.simplemove;

import lilypad.client.connect.api.Connect;
import lilypad.client.connect.api.request.RequestException;
import lilypad.client.connect.api.request.impl.RedirectRequest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class SimpleMove extends JavaPlugin implements Listener {
	
	public Connect connect;
	public String server = "backup";
	public BukkitTask task;
	public int timer = 864000;
	
	public void onEnable() {
	    connect = getServersManager().getRegistration(Connect.class).getProvider();
	    Bukkit.getPluginManager().registerEvents(this, this);
	    timerTask();
	}
	
	public void onDisable() {
		complete();
	}
	
	public void complete() {
		try {
			
			for ( Player player : Bukkit.getOnlinePlayers() ) {
				if(player != null) {
					String name = player.getName();
					send(server, name);
				}
			}
			
			task.cancel();
			connect = null;
			server = null;
			task = null;
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public ServicesManager getServersManager() {
		return Bukkit.getServer().getServicesManager();
	} 
	
	@EventHandler
	public void oncommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(event.getMessage().startsWith("/stop")) {
			if(player.hasPermission("bukkit.server.stop")) {
				complete();
			} else {
				player.sendMessage(ChatColor.GREEN + "[Warning] You don't have permissions for this command!");
			}
			event.setCancelled(true);
		}
	}
	
	public void send(String server, String player) {
		try {
			connect.request(new RedirectRequest(server, player));
		} catch (IndexOutOfBoundsException | RequestException e1) {
			e1.printStackTrace();
		}
	}
	
	public void timerTask() {
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				countDown();
			}
		}.runTaskLater(this, timer);
	}
	
	int time = 10;
	public void countDown() {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				if(time == 0) {
					cancel();
					
					Bukkit.shutdown();
					
					return;
				}
				
				String message = ChatColor.GREEN + "[Warning] Server restarting in " + time + " second(s)!";
				Bukkit.broadcastMessage(message);
				
				time --;
				
				
			}
		}.runTaskTimer(this, 20, 20);
	}
}

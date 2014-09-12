package Bammerbom.PreGenerator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	Plugin plugin;
	public void onEnable(){
		plugin = this;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Enabled PreGenerator!");
	}
	public void onDisable(){
		cancel();
	}
	Thread thread;
	public void cancel(){ running = false; }
	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args){
		if(!sender.hasPermission("pregenerator.use") && !sender.isOp()){
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permissions to use PreGenerator! (pregenerator.use)");
			return true;
		}
		if(args.length < 5){
			if(running && args.length > 0 && (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("cancel"))){
				if(thread != null){
					cancel();
				}
				
				log(ChatColor.DARK_RED + "Task cancelled succesfull. Progress saved.");
				return true;
			}
			sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + "/pregenerate <World> <StartX> <StartZ> <EndX> <EndZ>");
			sender.sendMessage(ChatColor.DARK_RED + "Usage: " + ChatColor.RED + "/pregenerate stop/cancel");
			return true;
		}
		//
		  thread = new Thread(){
			    public void run(){
			    	final World world = Bukkit.getWorld(args[0]);
					if(world == null){
						sender.sendMessage(ChatColor.DARK_RED + "World not found: " + ChatColor.RED + args[0]);
						return;
					}
					Integer startx = null;
					Integer startz = null;
					Integer endx = null;
					Integer endz = null;
					try{
						startx = Integer.valueOf(args[1]);
			            startz = Integer.valueOf(args[2]);
			            endx = Integer.valueOf(args[3]);
			            endz = Integer.valueOf(args[4]);
					}catch(Exception ex){
						sender.sendMessage(ChatColor.DARK_RED + "Coordinates not correct.");
						return;
					}
					if(startx == null || startz == null || endx == null || endz == null){
						sender.sendMessage(ChatColor.DARK_RED + "Coordinates not correct.");
						return;
					}
					if(isRunning()){
						sender.sendMessage(ChatColor.DARK_RED + "PreGenerator is already generating land. Wait for this task before starting a new one.");
						return;
					}
					running = true;
					Long start = System.currentTimeMillis();
					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "PreGenerator started generating land!");
					sender.sendMessage(ChatColor.DARK_PURPLE + "Check console for details.");
					try{
					world.save();
					}catch(Exception ex){
					}
					Cuboid region = new Cuboid(plugin, world, startx, 3, startz, endx, 3, endz);
					region.generateChunks();
					Long duration = System.currentTimeMillis() - start;
					Long seconds = duration / 1000;
					Long minutes = seconds / 60;
					Long hours = minutes / 60;
					Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Generating done! (" + duration + "ms, =" + seconds + "s, =" + minutes + "m, =" + hours + "h.)");
					running = false;
			    }
		  };
		thread.start();
		
		//
		
		return true;
	}
	static Boolean running = false;
	public static boolean isRunning(){
		return running;
	}
	public static void log(String str){
		Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "[" + ChatColor.AQUA + "PG" + ChatColor.BLUE + "] " + ChatColor.YELLOW + str);
	}
	static Integer max = -1;
}
package xyz.sethome;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class SetHomeXYZ extends JavaPlugin{

    private File homesFile = new File(this.getDataFolder(), "Homes.yml");
    private File prevFile = new File(this.getDataFolder(),"Prev.yml");
    private YamlConfiguration homes;
    private YamlConfiguration prevs;

    private String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "Home" + ChatColor.WHITE + "] ";

    public SetHomeXYZ() {
        this.homes = YamlConfiguration.loadConfiguration(this.homesFile);
        this.prevs = YamlConfiguration.loadConfiguration(this.prevFile);
    }

    public void onDisable() {
        this.saveFiles();
    }

    private boolean setHome(Player player){
        double x, y, z;
        float yaw, pitch;
        if (!player.hasPermission("xyz.sethome.sethome")) {
            player.sendMessage(prefix + ChatColor.RED + "You need permission to perform this command!");
            return false;
        } else {
            x = player.getLocation().getX();
            y = player.getLocation().getY();
            z = player.getLocation().getZ();
            yaw = player.getLocation().getYaw();
            pitch = player.getLocation().getPitch();
            String worldName = player.getWorld().getName();
            homes.set("Homes." + player.getUniqueId().toString() + ".X", x);
            homes.set("Homes." + player.getUniqueId().toString() + ".Y", y);
            homes.set("Homes." + player.getUniqueId().toString() + ".Z", z);
            homes.set("Homes." + player.getUniqueId().toString() + ".Yaw", yaw);
            homes.set("Homes." + player.getUniqueId().toString() + ".Pitch", pitch);
            homes.set("Homes." + player.getUniqueId().toString() + ".World", worldName);
            saveFiles();
            player.sendMessage(prefix + ChatColor.GRAY + "Your home has been set!");
            return false;
        }
    }

    private boolean goHome(Player player){
        double x, y, z;
        float yaw, pitch;
        if (!player.hasPermission("xyz.sethome.home")) {
            player.sendMessage(prefix + ChatColor.RED + "You need permission to perform this command!");
            return false;
        } else {
            // Save the current position to the previous position file
            x = player.getLocation().getX();
            y = player.getLocation().getY();
            z = player.getLocation().getZ();
            yaw = player.getLocation().getYaw();
            pitch = player.getLocation().getPitch();
            String worldName = player.getWorld().getName();
            this.prevs.set("Prevs." + player.getUniqueId().toString() + ".X", x);
            this.prevs.set("Prevs." + player.getUniqueId().toString() + ".Y", y);
            this.prevs.set("Prevs." + player.getUniqueId().toString() + ".Z", z);
            this.prevs.set("Prevs." + player.getUniqueId().toString() + ".Yaw", yaw);
            this.prevs.set("Prevs." + player.getUniqueId().toString() + ".Pitch", pitch);
            this.prevs.set("Prevs." + player.getUniqueId().toString() + ".World", worldName);
            this.saveFiles();

            // Move player home
            x = this.homes.getDouble("Homes." + player.getUniqueId().toString() + ".X");
            y = this.homes.getDouble("Homes." + player.getUniqueId().toString() + ".Y");
            z = this.homes.getDouble("Homes." + player.getUniqueId().toString() + ".Z");
            yaw = (float) this.homes.getLong("Homes." + player.getUniqueId().toString() + ".Yaw");
            pitch = (float) this.homes.getLong("Homes." + player.getUniqueId().toString() + ".Pitch");
            World world = Bukkit.getWorld(this.homes.getString("Homes." + player.getUniqueId().toString() + ".World"));
            Location home = new Location(world, x, y, z, yaw, pitch);
            player.teleport(home);
            player.sendMessage(prefix + ChatColor.GRAY + "Welcome home!");
            return false;
        }
    }

    private boolean goBack(Player player){
        double x, y, z;
        float yaw, pitch;
        if(!player.hasPermission("xyz.sethome.goback")){
            player.sendMessage(prefix + ChatColor.RED + "You need permission to perform this command!");
            return false;
        } else {
            x = this.prevs.getDouble("Prevs." + player.getUniqueId().toString() + ".X");
            y = this.prevs.getDouble("Prevs." + player.getUniqueId().toString() + ".Y");
            z = this.prevs.getDouble("Prevs." + player.getUniqueId().toString() + ".Z");
            yaw = (float) this.prevs.getLong("Prevs." + player.getUniqueId().toString() + ".Yaw");
            pitch = (float) this.prevs.getLong("Prevs." + player.getUniqueId().toString() + ".Pitch");
            World world = Bukkit.getWorld(this.prevs.getString("Prevs." + player.getUniqueId().toString() + ".World"));
            Location home = new Location(world, x, y, z, yaw, pitch);
            player.teleport(home);
            player.sendMessage(prefix + ChatColor.GRAY + "Moved you back!");
            return false;
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        if(command.getName().equalsIgnoreCase("sethome")) return setHome(player);
        if (command.getName().equalsIgnoreCase("home")) return goHome(player);
        if(command.getName().equalsIgnoreCase("goback")) return goBack(player);
        return false;
    }

    private void saveFiles() {
        try {
            this.homes.save(this.homesFile);
            this.prevs.save(this.prevFile);
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }


}

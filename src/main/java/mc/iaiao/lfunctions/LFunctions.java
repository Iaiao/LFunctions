package mc.iaiao.lfunctions;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LFunctions extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        try {
            Field field = getServer().getClass().getDeclaredField("commandMap");
            boolean wasAccessible = field.isAccessible();
            field.setAccessible(true);
            CommandMap commandMap = (CommandMap) field.get(getServer());
            for (String commandName : getConfig().getConfigurationSection("commands").getKeys(false)) {
                List<String> commands = getConfig().getStringList("commands." + commandName);
                Command command = new Command(commandName) {
                    @Override
                    public boolean execute(CommandSender sender, String s, String[] args) {
                        if (!testPermission(sender)) {
                            return true;
                        }
                        executeCommands(commands, sender, args);
                        return false;
                    }
                };
                command.setPermission("lfunctions.function." + commandName);
                commandMap.register(commandName, command);
            }
            field.setAccessible(wasAccessible);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (getConfig().contains("events.join") && event.getPlayer().hasPermission("lfunctions.event.join")) {
            getServer().dispatchCommand(getServer().getConsoleSender(), getConfig().getString("events.join") + " " + event.getPlayer().getName() + " " + event.getPlayer().getDisplayName());
        }
    }

    public void executeCommands(List<String> commands, CommandSender sender, String... args) {
        int i = 0;
        for (String command : commands) {
            i++;
            CommandSender consoleSender = Bukkit.getConsoleSender();
            if (command.startsWith("#")) {
                consoleSender = new RedirectedSender(sender);
                command = command.substring("#".length());
            }
            if (command.startsWith("!!sleep ")) {
                int seconds = Integer.parseInt(command.substring("!!sleep ".length()));
                List<String> cmds = commands.subList(i, commands.size());
                getServer().getScheduler().scheduleSyncDelayedTask(LFunctions.this, () -> {
                    executeCommands(cmds, sender, args);
                }, 20L * seconds);
                return;
            }
            CommandSender commandSender = sender;
            if (command.startsWith("[SERVER] ")) {
                commandSender = consoleSender;
                command = command.substring("[SERVER] ".length());
            }
            List<String> arguments = new ArrayList<>();
            boolean quotes = false;
            String argument = "";
            for (String s : args) {
                if (!quotes && s.startsWith("\"\"\"")) {
                    quotes = true;
                    s = s.substring("\"\"\"".length());
                }
                if (quotes) {
                    if (s.endsWith("\"\"\"")) {
                        argument += s.substring(0, s.length() - "\"\"\"".length());
                        arguments.add(argument);
                    } else {
                        argument += s + " ";
                    }
                } else {
                    arguments.add(s);
                }
            }
            for (int arg = 0; arg < arguments.size(); arg++) {
                command = command.replaceAll("%arg" + (arg + 1), arguments.get(arg));
            }
            getServer().dispatchCommand(commandSender, command);
        }
    }

    private static class RedirectedSender implements RemoteConsoleCommandSender {

        CommandSender target;

        RedirectedSender(CommandSender target) {
            this.target = target;
        }

        @Override
        public void sendMessage(String s) {
            target.sendMessage(s);
        }

        @Override
        public void sendMessage(String[] strings) {
            target.sendMessage(strings);
        }

        @Override
        public void sendMessage(UUID uuid, String s) {
            target.sendMessage(uuid, s);
        }

        @Override
        public void sendMessage(UUID uuid, String[] strings) {
            target.sendMessage(uuid, strings);
        }

        @Override
        public Server getServer() {
            return Bukkit.getServer();
        }

        @Override
        public String getName() {
            return "LFunctions";
        }

        @Override
        public Spigot spigot() {
            return new Spigot() {
                @Override
                public void sendMessage(BaseComponent component) {
                    target.spigot().sendMessage(component);
                }

                @Override
                public void sendMessage(BaseComponent... components) {
                    target.spigot().sendMessage(components);
                }

                @Override
                public void sendMessage(UUID sender, BaseComponent component) {
                    target.spigot().sendMessage(sender, component);
                }

                @Override
                public void sendMessage(UUID sender, BaseComponent... components) {
                    target.spigot().sendMessage(sender, components);
                }
            };
        }

        @Override
        public boolean isPermissionSet(String s) {
            return getServer().getConsoleSender().isPermissionSet(s);
        }

        @Override
        public boolean isPermissionSet(Permission permission) {
            return getServer().getConsoleSender().isPermissionSet(permission);
        }

        @Override
        public boolean hasPermission(String s) {
            return getServer().getConsoleSender().hasPermission(s);
        }

        @Override
        public boolean hasPermission(Permission permission) {
            return getServer().getConsoleSender().hasPermission(permission);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b) {
            return getServer().getConsoleSender().addAttachment(plugin, s, b);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin) {
            return getServer().getConsoleSender().addAttachment(plugin);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i) {
            return getServer().getConsoleSender().addAttachment(plugin, s, b, i);
        }

        @Override
        public PermissionAttachment addAttachment(Plugin plugin, int i) {
            return getServer().getConsoleSender().addAttachment(plugin, i);
        }

        @Override
        public void removeAttachment(PermissionAttachment permissionAttachment) {
            getServer().getConsoleSender().removeAttachment(permissionAttachment);
        }

        @Override
        public void recalculatePermissions() {
            getServer().getConsoleSender().recalculatePermissions();
        }

        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            return getServer().getConsoleSender().getEffectivePermissions();
        }

        @Override
        public boolean isOp() {
            return getServer().getConsoleSender().isOp();
        }

        @Override
        public void setOp(boolean b) {
            getServer().getConsoleSender().setOp(b);
        }
    }
}

package net.kunmc.lab.rookcraft;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandListener implements TabExecutor {
    private ArrayList<String> speeds = new ArrayList<>();
    public CommandListener() {
        Bukkit.getPluginCommand("rook").setExecutor(this);
        Bukkit.getPluginCommand("rook").setTabCompleter(this);
        for(double i = 0.1; i < 10; i += 0.1) {
            String s = String.valueOf(i);
            if(s.length() >= 3) {
                s = s.substring(0,3);
            }
            speeds.add(s);
        }
    }
    @Override
    public boolean onCommand(CommandSender s, Command c,String l,String[] a) {
        if(c.getName().equals("rook")) {
            if(a.length >= 1) {
                if (a[0].equals("set")) {
                    if (a.length >= 2) {
                        if (a[1].equals("speed")) {
                            if (a.length >= 3) {
                                try {
                                    double speed = Double.parseDouble(a[2]);
                                    if (speed <= 0 || 10 <= speed) {
                                        s.sendMessage("§c設定可能な倍率は 0<n<10 です");
                                        return true;
                                    }
                                    RookCraft.getINSTANCE().setSpeed(speed);
                                    s.sendMessage("§a速度を" + speed + "にしました");
                                    return true;
                                } catch (Exception ignored) {
                                    s.sendMessage("§c倍率を入力してください");
                                    s.sendMessage("§c設定可能な倍率は 0<n<10 です");
                                    return true;
                                }
                            }
                            s.sendMessage("§c倍率を入力してください");
                            s.sendMessage("§c設定可能な倍率は 0<n<10 です");
                            return true;
                        }
                        s.sendMessage("§c引数が足りません");
                        s.sendMessage("§c/rook set speed 倍率(0<n<10)");
                        return true;
                    }
                    s.sendMessage("§c引数が足りません");
                    s.sendMessage("§c/rook set speed 倍率(0<n<10)");
                    return true;
                }
                if(a[0].equals("show")) {
                    if(a.length >= 2) {
                        if(a[1].equals("speed")) {
                            s.sendMessage("§aspeed: " + RookCraft.getINSTANCE().getSpeed());
                            return true;
                        }
                    }
                    s.sendMessage("§c引数が足りません");
                    s.sendMessage("§c/rook show speed");
                    return true;
                }
            }
            s.sendMessage("§c引数が足りません");
            s.sendMessage("§c/rook set speed 倍率(0<n<10)");
            s.sendMessage("§c/rook show speed");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s,Command c,String l,String[] a) {
        if(c.getName().equals("rook")) {
            if(a.length == 1) {
                return Stream.of("set","show").filter(e -> e.startsWith(a[0])).collect(Collectors.toList());
            }
            if(a.length == 2 && (a[0].equals("set") || a[0].equals("show"))) {
                return Stream.of("speed").filter(e -> e.startsWith(a[1])).collect(Collectors.toList());
            }
            if(a.length == 3 && a[0].equals("set") && a[1].equals("speed")) {
                return speeds.stream().filter(e -> e.startsWith(a[2])).collect(Collectors.toList());
            }
        }
        return null;
    }
}

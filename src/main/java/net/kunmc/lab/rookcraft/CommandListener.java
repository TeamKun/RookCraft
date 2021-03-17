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
    private final ArrayList<String> speeds = new ArrayList<>();
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
        if(!c.getName().equals("rook")) {
            return true;
        }
        if(a.length < 1) {
            s.sendMessage("§c引数が足りません");
            s.sendMessage("§c/rook set 速度(0<n<10)");
            s.sendMessage("§c/rook show");
            s.sendMessage("§c/rook on");
            s.sendMessage("§c/rook off");
            return true;
        }
        if(a[0].equals("set")) {
            if(a.length < 2) {
                s.sendMessage("§c速度を入力してください");
                s.sendMessage("§c設定可能な速度は 0<n<10 です");
                return true;
            }
            try {
                double speed = Double.parseDouble(a[1]);
                if(speed <= 0 || 10 <= speed) {
                    s.sendMessage("§c設定可能な速度は 0<n<10 です");
                    return true;
                }
                RookCraft.getINSTANCE().setSpeed(speed);
                s.sendMessage("§a速度を" + speed + "にしました");
                return true;
            } catch (Exception e) {
                s.sendMessage("§c速度を入力してください");
                s.sendMessage("§c設定可能な速度は 0<n<10 です");
                return true;
            }
        }
        if(a[0].equals("show")) {
            s.sendMessage("§a速度は " + RookCraft.getINSTANCE().getSpeed() + " です");
            return true;
        }
        if(a[0].equals("on")) {
            if(RookCraft.getINSTANCE().isEnable()) {
                s.sendMessage("§aプラグインはすでに有効です");
                return true;
            }
            RookCraft.getINSTANCE().setEnable(true);
            s.sendMessage("§aプラグインを有効にしました");
            return true;
        }
        if(a[0].equals("off")) {
            if(!RookCraft.getINSTANCE().isEnable()) {
                s.sendMessage("§aプラグインはすでに無効です");
                return true;
            }
            RookCraft.getINSTANCE().setEnable(false);
            s.sendMessage("§aプラグインを無効にしました");
            return true;
        }
        s.sendMessage("§c引数が足りません");
        s.sendMessage("§c/rook set 速度(0<n<10)");
        s.sendMessage("§c/rook show");
        s.sendMessage("§c/rook on");
        s.sendMessage("§c/rook off");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s,Command c,String l,String[] a) {
        if(c.getName().equals("rook")) {
            if(a.length == 1) {
                return Stream.of("set","show","on","off").filter(e -> e.startsWith(a[0])).collect(Collectors.toList());
            }
            if(a.length == 2 && a[0].equals("set")) {
                return speeds.stream().filter(e -> e.startsWith(a[1])).collect(Collectors.toList());
            }
        }
        return null;
    }
}

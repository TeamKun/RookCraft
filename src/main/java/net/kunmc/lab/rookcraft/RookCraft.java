package net.kunmc.lab.rookcraft;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public final class RookCraft extends JavaPlugin {
    private static RookCraft INSTANCE;
    private final HashMap<UUID,PlayerInfo> infoMap = new HashMap<>();

    private double speed = 0.5;

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public static RookCraft getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        putInfoMap();
        new CommandListener();
    }

    private void putInfoMap() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                getServer().getOnlinePlayers().stream()
                        .filter(p -> !infoMap.containsKey(p.getUniqueId()))
                        .forEach(p -> put(p));
            }
        },0L,2L);
    }

    public void playerJoinEvent(Player p) {
        put(p);
    }

    private void put(Player p) {
        infoMap.put(p.getUniqueId(),new PlayerInfo(p.getLocation().getX(),p.getLocation().getZ()));
        setCool(infoMap.get(p.getUniqueId()));
    }

    private void setCool(PlayerInfo pInfo) {
        pInfo.setCool(true);
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                pInfo.setCool(false);
            }
        },20L);
    }

    public void playerMoveEvent(Player p) {
        if(isNotValid(p)){return;}
        PlayerInfo pInfo = infoMap.get(p.getUniqueId());
        if(pInfo.isCool()){
            reset(pInfo,p);
        }
        if (pInfo.isMoving()) {
            if (isExistBlock(p, pInfo)) {
                setCool(pInfo);
                reset(pInfo,p);
                return;
            }
            addVelocity(p, pInfo);
                return;
        }
        changePos(p, pInfo, p.getLocation().getX(), p.getLocation().getZ());
    }

    private boolean isExistBlock(Player p, PlayerInfo pInfo) {
        return p.getWorld().rayTraceBlocks(p.getLocation(), pInfo.getDir(), 1) != null;
    }

    private void reset(PlayerInfo pInfo, Player p) {
        pInfo.setMoving(false);
        pInfo.setPosX(p.getLocation().getX());
        pInfo.setPosZ(p.getLocation().getZ());
    }

    private boolean isNotValid(Player p) {
        if(p.getGameMode() == GameMode.SPECTATOR) {return true;}
        if(p.isDead() || !p.isValid()){return true;}
        return !infoMap.containsKey(p.getUniqueId());
    }

    private void changePos(Player p, PlayerInfo pInfo, double posX,double posZ) {
        int diffX = (int) posX - (int) pInfo.getPosX();
        int diffZ = (int) posZ - (int) pInfo.getPosZ();
        if(diffX == 0 && diffZ == 0) {
            return;
        }
        pInfo.setPosX(posX);
        pInfo.setPosZ(posZ);
        if(diffX != 0) {
            pInfo.setDir(new Vector(diffX,0,0));
        } else {
            pInfo.setDir(new Vector(0,0,diffZ));
        }
        pInfo.setMoving(true);
        addVelocity(p, pInfo);
    }

    private void addVelocity(Player p, PlayerInfo pInfo) {
        if ((pInfo.getDir().getX()) != 0.0) {
            int diffZ = (int) p.getLocation().getZ() - (int) pInfo.getPosZ();
            if (diffZ != 0) {
                p.setVelocity(p.getVelocity().add((new Vector(0,0,diffZ * -1)).normalize().multiply(0.1)));
            }
        }
        if ((pInfo.getDir().getZ()) != 0.0) {
            int diffX = (int) p.getLocation().getX() - (int) pInfo.getPosX();
            if (diffX != 0) {
                p.setVelocity(p.getVelocity().add((new Vector(diffX * -1,0,0)).normalize().multiply(0.1)));
            }
        }
        p.setVelocity(p.getVelocity().add(pInfo.getDir().normalize().multiply(speed)));
    }
}

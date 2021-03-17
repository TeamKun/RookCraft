package net.kunmc.lab.rookcraft;

import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public final class RookCraft extends JavaPlugin {
    private static RookCraft INSTANCE;
    public static RookCraft getINSTANCE() {
        return INSTANCE;
    }

    private final HashMap<UUID,PlayerInfo> infoMap = new HashMap<>();
    private double speed = 0.5;
    private boolean isEnable = true;

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setEnable(boolean isEnable) {
        this.isEnable = isEnable;
        if(isEnable) {
            getServer().getOnlinePlayers()
                    .forEach(this::put);
        }
    }

    public boolean isEnable() {
        return isEnable;
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
        setCool(infoMap.get(p.getUniqueId()), true);
    }

    private void setCool(PlayerInfo pInfo, boolean isJoin) {
        pInfo.setCool(true);
        long delay = isJoin ? 20L : 2L;
        getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                pInfo.setCool(false);
            }
        },delay);
    }

    public void playerMoveEvent(Player p, Location from, Location to) {
        if(!isEnable) { return; }
        if (isNotValid(p)) {
            return;
        }
        PlayerInfo pInfo = infoMap.get(p.getUniqueId());
        if (pInfo.isCool()) {
            resetPInfo(pInfo, p);
            return;
        }
        if (pInfo.isMoving()) {
            if (isExistBlock(p,pInfo) && isStop(from,to)) {
                setCool(pInfo,false);
                resetPInfo(pInfo, p);
                return;
            }
            addVelocity(p, pInfo);
            return;
        }
        changePos(p, pInfo, p.getLocation().getX(), p.getLocation().getZ());
    }

    private boolean isExistBlock(Player p, PlayerInfo pInfo) {
        RayTraceResult result1;
        result1 = p.getWorld().rayTraceBlocks(p.getLocation(), pInfo.getDir(), 1, FluidCollisionMode.NEVER,true);
        RayTraceResult result2;
        result2 = p.getWorld().rayTraceBlocks(p.getLocation().add(0,1,0),pInfo.getDir(),1, FluidCollisionMode.NEVER, true);
        return result1 != null || result2 != null;
    }

    private boolean isStop(Location from, Location to) {
        return from.distance(to) <= 0.1;
    }

    private void resetPInfo(PlayerInfo pInfo, Player p) {
        pInfo.setMoving(false);
        pInfo.setPosX(p.getLocation().getX());
        pInfo.setPosZ(p.getLocation().getZ());
    }

    private boolean isNotValid(Player p) {
        if(p.getGameMode() == GameMode.SPECTATOR || p.getGameMode() == GameMode.CREATIVE) {return true;}
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

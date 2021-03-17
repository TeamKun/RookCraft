package net.kunmc.lab.rookcraft;

import org.bukkit.util.Vector;

public class PlayerInfo {
    private boolean isMoving;
    private double posX;
    private double posZ;
    private Vector dir;
    private boolean isCool;

    public PlayerInfo(double posX,double posZ) {
        this.posX = posX;
        this.posZ = posZ;
        isMoving = false;
        isCool = true;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public Vector getDir() {
        return dir;
    }

    public void setDir(Vector dir) {
        this.dir = dir;
    }

    public boolean isCool() {
        return isCool;
    }

    public void setCool(boolean cool) {
        isCool = cool;
    }
}

package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.CustomCrates;
import org.bukkit.Bukkit;

public class DebugUtils
{
    CustomCrates cc;
    boolean forceDebug = true;

    public DebugUtils(CustomCrates cc)
    {
        this.cc = cc;
    }

    public boolean beta = false;

    public void log(String s)
    {
        boolean debug = forceDebug || Boolean.valueOf(cc.getSettings().getConfigValues().get("debug").toString());
        if (debug)
        {
            Bukkit.getLogger().info("[DEBUG] " + s);
        }
    }

    public void log(String s, Class<?> clas)
    {
        boolean debug = forceDebug || Boolean.valueOf(cc.getSettings().getConfigValues().get("debug").toString());
        if (debug)
        {
            Bukkit.getLogger().info("[DEBUG] " + clas.getName() + "." + s);
        }
    }
}

package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Bukkit;

public class DebugUtils {
    public static final boolean ENABLE_CACHING = true;
    public static final boolean LOG_CACHED_INFO = false;
    public static final boolean OUTPUT_AVERAGE_TICK = false;

    public static final boolean OUTPUT_FILE_SAVE_TIME = false;
    public static final boolean OUTPUT_PLAYER_DATA_LOAD_TIME = false;

    public static final boolean OUTPUT_AVERAGE_ANIMATION_TICK = false;

    SpecializedCrates cc;
    boolean forceDebug = false;

    String[] sort = new String[]
            {
                    ""
            };

    public DebugUtils(SpecializedCrates cc) {
        this.cc = cc;
    }

    public void log(String s) {
        boolean debug = forceDebug || Boolean.parseBoolean(cc.getSettings().getConfigValues().get("debug").toString());
        if (debug) {
            Bukkit.getLogger().info("[DEBUG] " + s);
        }
    }

    public void log(String s, Class<?> clas) {
        log(s, clas, false);
    }

    public void log(String s, Class<?> clas, boolean dumpStack) {
        log(s, clas, dumpStack, false);
    }

    public void log(String s, Class<?> clas, boolean dumpStack, boolean toChat) {
        try {
            boolean debug = forceDebug || Boolean.parseBoolean(cc.getSettings().getConfigValues().get("debug").toString());

            String msg = clas.getName() + "." + s;

            boolean found = sort.length == 0;
            for (String toSort : sort) {
                if (msg.startsWith(toSort)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                if (debug) {
                    if (toChat)
                        Bukkit.broadcastMessage("[DEBUG] " + msg);
                    else
                        Bukkit.getLogger().info("[DEBUG] " + msg);
                }
                if (dumpStack) {
                    dumpStack();
                }
            }
        } catch (Exception exc) {

        }
    }

    public void dumpStack() {
        boolean debug = forceDebug || Boolean.parseBoolean(cc.getSettings().getConfigValues().get("debug").toString());
        if (debug)
            Thread.dumpStack();
    }
}

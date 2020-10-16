package me.ztowne13.customcrates.players;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.DisplayPage;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.players.data.*;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.ReflectionUtilities;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    static Map<UUID, PlayerManager> pManagers = new HashMap<>();

    SpecializedCrates cc;

    Player p;

    DataHandler dh;
    PlayerDataManager pdm;

    long lastClickedCrateTime = 0;

    PlacedCrate lastOpenedPlacedCrate = null;
    Crate openCrate = null;
    Location lastOpenCrate = null;
    AnimationDataHolder currentAnimation;
    boolean isInCratesClaimMenu = false;
    boolean inRewardMenu = false;
    DisplayPage lastPage;
    // This is to allow the anti-dupe inventory reopen/close feature but prevent it when opening the next page of an inv
    long nextPageInventoryCloseGrace = 0;
    boolean deleteCrate = false;
    boolean useVirtualCrate = false;
    boolean confirming = false;
    BukkitTask confirmingTask = null;
    long cmdCooldown = 0;
    String lastCooldown = "NONE";
    private IGCMenu openMenu = null;
    private IGCMenu lastOpenMenu = null;

    public PlayerManager(SpecializedCrates cc, Player p) {
        this.cc = cc;
        this.p = p;
        this.dh = getSpecifiedDataHandler();
        this.pdm = new PlayerDataManager(this);

        getPdm().setDh(getDh());
        getPdm().loadAllInformation();
        getpManagers().put(p.getUniqueId(), this);
    }

    public static PlayerManager get(SpecializedCrates cc, Player p) {
        cc.getDu().log("PlayerManager.get() - CALL (contains: " + getpManagers().containsKey(p.getUniqueId()) + ")", PlayerManager.class);
        return getpManagers().containsKey(p.getUniqueId()) ? getpManagers().get(p.getUniqueId()) : new PlayerManager(cc, p);
    }

    public static void clearLoaded() {
        getpManagers().clear();
        setpManagers(new HashMap<>());
    }

    public static Map<UUID, PlayerManager> getpManagers() {
        return pManagers;
    }

    public static void setpManagers(Map<UUID, PlayerManager> pManagers) {
        PlayerManager.pManagers = pManagers;
    }

    public void remove(int delay) {
        cc.getDu().log("PlayerManager.remove() - CALL", getClass());

        if (isInCrateAnimation()) {
            getCurrentAnimation().setFastTrack(true, true);
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable() {
            @Override
            public void run() {
                getpManagers().remove(getP().getUniqueId());
                cc.getDu().log("PlayerManager.remove() - Removed", getClass());
            }
        }, delay);

        ReflectionUtilities.cachedHandles.remove(getP());
    }

    public DataHandler getSpecifiedDataHandler() {
        try {
            StorageType st = StorageType.valueOf(
                    ChatUtils.stripFromWhitespace(SettingsValue.STORE_DATA.getValue(getCc()).toString().toUpperCase()));
            switch (st) {
                case MYSQL:
                    Utils.addToInfoLog(cc, "Storage Type", "MYSQL");
                    return new SQLDataHandler(this);
                case FLATFILE:
                    Utils.addToInfoLog(cc, "Storage Type", "FLATFILE");
                    return new FlatFileDataHandler(this);
                case PLAYERFILES:
                    Utils.addToInfoLog(cc, "Storage Type", "PLAYERFILES");
                    return new IndividualFileDataHandler(this);
                default:
                    ChatUtils.log(new String[]{"store-data value in the config.YML is not a valid storage type.",
                            "  It must be: MYSQL, FLATFILE, PLAYERFILES"});
                    Utils.addToInfoLog(cc, "StorageType", "FLATFILE");
                    return new FlatFileDataHandler(this);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            ChatUtils.log(new String[]{"store-data value in the config.YML is not a valid storage type.",
                    "  It must be: MYSQL, FLATFILE, PLAYERFILES"});
        }
        return null;
    }

    public boolean isConfirming() {
        return confirming;
    }

    public void setConfirming(final boolean confirming) {
        this.confirming = confirming;
        if (confirming) {
            confirmingTask = Bukkit.getScheduler().runTaskLater(cc, () -> setConfirming(false), 20L * (int) SettingsValue.CONFIRM_TIMEOUT.getValue(cc));
        } else {
            if (confirmingTask != null) {
                confirmingTask.cancel();
                confirmingTask = null;
            }
        }
    }

    public boolean isInCrate() {
        return openCrate != null;
    }

    public boolean isDeleteCrate() {
        return deleteCrate;
    }

    public void setDeleteCrate(boolean b) {
        this.deleteCrate = b;
    }

    public void openCrate(Crate crate) {
        openCrate = crate;
    }

    public void closeCrate() {
        openCrate = null;
        useVirtualCrate = false;
    }

    public Crate getOpenCrate() {
        return openCrate;
    }

    public boolean isInRewardMenu() {
        return inRewardMenu;
    }

    public void setInRewardMenu(boolean inRewardMenu) {
        this.inRewardMenu = inRewardMenu;
    }

    public Player getP() {
        return p;
    }

    public void setP(Player p) {
        this.p = p;
    }

    public SpecializedCrates getCc() {
        return cc;
    }

    public void setCc(SpecializedCrates cc) {
        this.cc = cc;
    }

    public DataHandler getDh() {
        return dh;
    }

    public PlayerDataManager getPdm() {
        return pdm;
    }

    public void setPdm(PlayerDataManager pdm) {
        this.pdm = pdm;
    }

    public long getCmdCooldown() {
        return cmdCooldown;
    }

    public void setCmdCooldown(long cmdCooldown) {
        this.cmdCooldown = cmdCooldown;
    }

    public String getLastCooldown() {
        return lastCooldown;
    }

    public void setLastCooldown(String lastCooldown) {
        this.lastCooldown = lastCooldown;
    }

    public IGCMenu getOpenMenu() {
        return openMenu;
    }

    public void setOpenMenu(IGCMenu openMenu) {
        this.openMenu = openMenu;
        if (openMenu != null) {
            this.lastOpenMenu = openMenu;
        }
    }

    public boolean isInOpenMenu() {
        return this.openMenu != null;
    }

    public IGCMenu getLastOpenMenu() {
        return lastOpenMenu;
    }

    public Location getLastOpenCrate() {
        return lastOpenCrate;
    }

    public void setLastOpenCrate(Location lastOpenCrate) {
        this.lastOpenCrate = lastOpenCrate;
    }

    public boolean isUseVirtualCrate() {
        return useVirtualCrate;
    }

    public void setUseVirtualCrate(boolean useVirtualCrate) {
        this.useVirtualCrate = useVirtualCrate;
    }

    public PlacedCrate getLastOpenedPlacedCrate() {
        return lastOpenedPlacedCrate;
    }

    public void setLastOpenedPlacedCrate(PlacedCrate lastOpenedPlacedCrate) {
        this.lastOpenedPlacedCrate = lastOpenedPlacedCrate;
    }

    public DisplayPage getLastPage() {
        return lastPage;
    }

    public void setLastPage(DisplayPage lastPage) {
        this.lastPage = lastPage;
    }

    public AnimationDataHolder getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(AnimationDataHolder currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    public boolean isInCrateAnimation() {
        return currentAnimation != null;
    }

    public long getLastClickedCrateTime() {
        return lastClickedCrateTime;
    }

    public void setLastClickedCrateTime(long lastClickedCrateTime) {
        this.lastClickedCrateTime = lastClickedCrateTime;
    }

    public long getNextPageInventoryCloseGrace() {
        return nextPageInventoryCloseGrace;
    }

    public void setNextPageInventoryCloseGrace(long nextPageInventoryCloseGrace) {
        this.nextPageInventoryCloseGrace = nextPageInventoryCloseGrace;
    }

    public boolean isInCratesClaimMenu() {
        return isInCratesClaimMenu;
    }

    public void setInCratesClaimMenu(boolean inCratesClaimMenu) {
        isInCratesClaimMenu = inCratesClaimMenu;
    }
}

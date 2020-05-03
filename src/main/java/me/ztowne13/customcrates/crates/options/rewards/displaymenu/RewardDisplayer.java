package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class RewardDisplayer
{
    Crate crates;
    String name = null;
    boolean multiplePages = false;
    boolean requirePermForPreview = false;

    FileHandler fileHandler;

    public RewardDisplayer(Crate crates)
    {
        this.crates = crates;
        this.fileHandler = crates.getSettings().getFileHandler();
    }

    public abstract void open(Player p);

    public abstract InventoryBuilder createInventory(Player p);

    public abstract void load();

    public void openFor(Player player)
    {
        if(isRequirePermForPreview())
        {
            if(!player.hasPermission(getCrates().getSettings().getPermission()))
            {
                Messages.NO_PERMISSION_CRATE.msgSpecified(crates.getCc(), player);
                return;
            }
        }

        open(player);
    }

    public String getInvName()
    {
        if (name == null)
            return ChatUtils.toChatColor(
                    getCrates().getCc().getSettings().getConfigValues().get("inv-reward-display-name").toString()
                            .replace("%crate%", getCrates().getName()));
        else
            return ChatUtils.toChatColor(name);
    }

    public void loadDefaults()
    {
        FileConfiguration fc = fileHandler.get();

        if(fc.contains("reward-display.name"))
        {
            this.name = fc.getString("reward-display.name");
        }

        if(fc.contains("reward-display.require-permission"))
        {
            try
            {
                this.requirePermForPreview = fc.getBoolean("reward-display.require-permission");
            }
            catch(Exception exc)
            {

            }
        }
    }

    public void saveToFile()
    {
        getFileHandler().get().set("reward-display.type", getCrates().getSettings().getCrateDisplayType().name());
        getFileHandler().get().set("reward-display.name", name);
        getFileHandler().get().set("reward-display.require-permission", requirePermForPreview);
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public FileHandler getFileHandler()
    {
        return fileHandler;
    }

    public boolean isRequirePermForPreview()
    {
        return requirePermForPreview;
    }

    public void setRequirePermForPreview(boolean requirePermForPreview)
    {
        this.requirePermForPreview = requirePermForPreview;
    }
}

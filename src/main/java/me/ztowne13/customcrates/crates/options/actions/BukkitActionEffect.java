package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.actions.actionbar.ActionBar;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 2/14/2017.
 */
public class BukkitActionEffect extends ActionEffect {
    String title;
    String subtitle;
    int fadeIn;
    int stay;
    int fadeOut;

    public BukkitActionEffect(SpecializedCrates cc) {
        super(cc);
    }

    public ActionBar getActionBarExecutor() {
        return new ActionBar();
    }

    public void newTitle() {
        fadeIn = Integer.parseInt(SettingsValue.CA_FADE_IN.getValue(cc).toString());
        stay = Integer.parseInt(SettingsValue.CA_STAY.getValue(cc).toString());
        fadeOut = Integer.parseInt(SettingsValue.CA_FADE_OUT.getValue(cc).toString());
    }

    public void playTitle(Player p) {
        p.sendTitle(title, subtitle, fadeIn * 20, stay * 20, fadeOut * 20);
        resetData();
    }

    public void setDisplayTitle(String title) {
        this.title = title;
    }

    public void setDisplaySubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void resetData() {
        title = "";
        subtitle = "";
    }
}

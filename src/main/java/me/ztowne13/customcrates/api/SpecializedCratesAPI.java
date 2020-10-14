package me.ztowne13.customcrates.api;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpecializedCratesAPI {

    private final SpecializedCrates cc;

    public SpecializedCratesAPI(SpecializedCrates cc) {
        this.cc = cc;
    }

    public void giveKeysSilent(Player player, String crateName, int amount) {
        Crate crate = Crate.getCrate(cc, crateName);
        ItemStack key = crate.getSettings().getKeyItemHandler().getItem(amount);

        Boolean toNotDrop = (Boolean) SettingsValue.VIRTUAL_KEY_INSTEAD_OF_DROP.getValue(cc);
        int count = Utils.addItemAndDropRest(player, key, !toNotDrop);

        if (toNotDrop) {
            PlayerDataManager pdm = PlayerManager.get(cc, player).getPdm();
            pdm.setVirtualCrateKeys(crate, pdm.getVCCrateData(crate).getKeys() + count);
        }
    }

}

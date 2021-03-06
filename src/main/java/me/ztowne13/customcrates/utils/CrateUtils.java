package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.inventory.ItemStack;

public class CrateUtils {
    public static Crate searchByKey(ItemStack stack) {
        for (Crate crates : Crate.getLoadedCrates().values()) {
            if (isCrateUsable(crates)) {
                if (crates.getSettings().getKeyItemHandler().keyMatchesToStack(stack, false)) {
                    return crates;
                }
            }
        }
        return null;
    }

    public static Crate searchByCrate(ItemStack stack) {
        return searchByCrate(stack, false);
    }

    public static Crate searchByCrate(ItemStack stack, boolean includeDisabled) {
        for (Crate crates : Crate.getLoadedCrates().values()) {
            if (isCrateUsable(crates) || includeDisabled) {
                if (crates.getSettings().getCrateItemHandler().crateMatchesToStack(stack)) {
                    return crates;
                }
            }
        }
        return null;
    }

    public static boolean isCrateUsable(Crate crates) {
        return crates != null && crates.isEnabled() && !crates.isDisabledByError();
    }

    public static boolean isCrateUsable(PlacedCrate cm) {
        return cm.isCratesEnabled() && cm.getCrate() != null && cm.getCrate().isEnabled() && !cm.getCrate().isDisabledByError();
    }
}
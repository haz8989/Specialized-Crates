package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import org.bukkit.Location;

/**
 * Created by ztowne13 on 6/26/16.
 */
public class GrowingSpiralPA extends ParticleAnimationEffect {
    static int degrees = 540;
    int updatesPerSec;

    double toChangeHeight = 0;
    double currentYOffset = 0;
    double toChangeRadius = 0;
    double currentRadius = 0;

    public GrowingSpiralPA(SpecializedCrates cc, ParticleData particleData) {
        super(cc, particleData);
        this.updatesPerSec = (int) particleData.getSpeed();
    }

    @Override
    public void update() {
        toDisplay.clear();

        totalTick += updatesPerSec;
        tick += updatesPerSec;

        double radius = particleData.getRangeX();
        double yOffset = particleData.getRangeY();
        double height = particleData.getRangeZ();

        if (toChangeHeight == 0 && height != 0) {
            toChangeHeight = height / degrees;
        }

        if (toChangeRadius == 0) {
            currentRadius = radius;
            toChangeRadius = -(radius / degrees);
        }

        int i = tick;
        while (i < tick + updatesPerSec) {
            currentYOffset += toChangeHeight;
            currentRadius += toChangeRadius;
            if (i % (2 + particleData.getAmount()) == 0) {
                double toX = Math.sin(Math.toRadians(i)) * currentRadius;
                double toY = Math.cos(Math.toRadians(i)) * currentRadius;

                Location newL = new Location(null, toX, currentYOffset + yOffset, toY);
                Location newL2 = new Location(null, -toX, currentYOffset + yOffset, -toY);

                toDisplay.add(newL);
                toDisplay.add(newL2);

                if (i > degrees) {
                    i = 1;
                    tick = 0;
                }

                if ((currentYOffset > height && toChangeHeight > 0) || (currentYOffset < 0 && toChangeHeight < 0)) {
                    toChangeHeight = -toChangeHeight;
                    toChangeRadius = -toChangeRadius;
                }
            }
            i++;
        }
    }
}

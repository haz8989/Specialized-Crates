package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import org.bukkit.Location;

/**
 * Created by ztowne13 on 6/25/16.
 */
public class CirclePA extends ParticleAnimationEffect {
    int updatesPerSec;

    public CirclePA(SpecializedCrates cc, ParticleData particleData) {
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

        int i = tick;
        while (i < tick + updatesPerSec) {
            if (i % (2 + particleData.getAmount()) == 0) {
                double toX = Math.sin(Math.toRadians(i)) * radius;
                double toY = Math.cos(Math.toRadians(i)) * radius;

                double formattedHeight = height / 10;
                for (double add = 0; add < formattedHeight; add += .1) {
                    Location newL = new Location(null, toX, add + yOffset, toY);
                    toDisplay.add(newL);
                }

                if (i > 360) {
                    i = 1;
                    tick = 0;
                }
            }
            i++;
        }

    }
}

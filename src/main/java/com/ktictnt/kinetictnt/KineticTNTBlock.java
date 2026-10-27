package com.ktictnt.kinetictnt;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class KineticTNTBlock extends FallingBlock {

    public static final MapCodec<KineticTNTBlock> CODEC = simpleCodec(KineticTNTBlock::new);

    public KineticTNTBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0xFF00E5FF; // Cyan sparks
    }

    /**
     * CHAIN REACTION:
     * When an explosion detonates nearby, launch this block as a flying kinetic projectile!
     */
    @Override
    public void onBlockExploded(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion) {
        // Remove the block from the world
        level.removeBlock(pos, false);

        // Spawn as a falling/flying entity projectile
        FallingBlockEntity projectile = FallingBlockEntity.fall(level, pos, state);
        projectile.dropItem = false;

        // Calculate direction vector pointing away from explosion center
        Vec3 explosionCenter = explosion.center();
        Vec3 blockCenter = pos.getCenter();
        Vec3 blastDirection = blockCenter.subtract(explosionCenter);

        double distance = blastDirection.length();
        if (distance < 0.1) {
            // If right at the center, launch straight up with a slight random tilt
            blastDirection = new Vec3(level.getRandom().nextGaussian() * 0.2, 1.0, level.getRandom().nextGaussian() * 0.2);
            distance = 1.0;
        }

        // Normalize direction and calculate impulse speed
        Vec3 normalizedDir = blastDirection.normalize();
        double launchSpeed = Math.max(0.8, 2.5 / (distance + 0.5));

        // Set ballistic arc velocity
        Vec3 velocity = new Vec3(
                normalizedDir.x * launchSpeed,
                Math.min(1.2, Math.max(0.4, normalizedDir.y * launchSpeed + 0.4)),
                normalizedDir.z * launchSpeed
        );

        projectile.setDeltaMovement(velocity);

        super.onBlockExploded(state, level, pos, explosion);
    }

    /**
     * IMPACT DETONATION:
     * When the projectile or falling block hits the terrain.
     */
    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        if (!level.isClientSide()) {
            // Vertical drop height
            int verticalDrop = fallingBlock.getStartPos().getY() - pos.getY();

            // Horizontal flight distance
            double dx = fallingBlock.getStartPos().getX() - pos.getX();
            double dz = fallingBlock.getStartPos().getZ() - pos.getZ();
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

            // Total kinetic factor: drop height + bonus from projectile travel
            double kineticPower = verticalDrop + (horizontalDistance * 0.6);

            // Explode if it dropped 3+ blocks OR was flung across distance
            if (kineticPower >= 3.0) {
                float explosionRadius = 4.0F + (float) ((kineticPower - 3.0) * 0.5F);

                // Cap radius at 25.0 to prevent server overload during huge chain reactions
                explosionRadius = Math.min(25.0F, explosionRadius);

                level.removeBlock(pos, false);

                // Level.ExplosionInteraction.TNT ensures this explosion triggers other nearby Kinetic TNTs!
                level.explode(
                        null,
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D,
                        explosionRadius,
                        Level.ExplosionInteraction.TNT
                );
            }
        }
    }
}
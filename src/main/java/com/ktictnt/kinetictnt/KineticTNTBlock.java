package com.ktictnt.kinetictnt;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class KineticTNTBlock extends FallingBlock {

    // Codec required by modern Minecraft
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
        return 0xFFFFFFFF;
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        // Calculate the height difference from where it started to where it landed
        int fallDistance = fallingBlock.getStartPos().getY() - pos.getY();

        // Only explode on the server side if it fell 3 or more blocks
        if (!level.isClientSide() && fallDistance >= 3) {
            // Base radius 4.0F + 0.5F per block dropped past 3
            float explosionRadius = 4.0F + (float) ((fallDistance - 3) * 0.5F);

            // Remove the block
            level.removeBlock(pos, false);

            // Detonate
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
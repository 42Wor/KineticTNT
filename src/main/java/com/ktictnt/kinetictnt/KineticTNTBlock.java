package com.ktictnt.kinetictnt;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class KineticTNTBlock extends FallingBlock {

    // Required by FallingBlock in 1.21+: a codec that can serialize this block type
    public static final MapCodec<KineticTNTBlock> CODEC = simpleCodec(KineticTNTBlock::new);

    @Override
    public MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    public KineticTNTBlock(Properties properties) {
        super(properties);
    }

    // Required by modern FallingBlock for the falling dust particle color
    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return 0xFFFFFFFF;
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        // In modern Minecraft, fallDistance is a double
        double fallDistance = fallingBlock.fallDistance;

        // isClientSide() is a method call in modern versions
        if (!level.isClientSide() && fallDistance >= 3.0) {
            float explosionRadius = 4.0F + (float) ((fallDistance - 3.0) * 0.5);

            level.removeBlock(pos, false);

            level.explode(
                    null,
                    pos.getX() + 0.5D,
                    pos.getY(),
                    pos.getZ() + 0.5D,
                    explosionRadius,
                    Level.ExplosionInteraction.TNT
            );
        }
    }
}
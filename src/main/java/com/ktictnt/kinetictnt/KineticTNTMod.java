package com.ktictnt.kinetictnt;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(KineticTNTMod.MODID)
public class KineticTNTMod {
    public static final String MODID = "kinetictnt";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Modern 26.1+ ResourceKeys using Identifier
    private static final ResourceKey<Block> BLOCK_KEY = ResourceKey.create(
            Registries.BLOCK,
            Identifier.fromNamespaceAndPath(MODID, "kinetic_tnt")
    );

    private static final ResourceKey<Item> ITEM_KEY = ResourceKey.create(
            Registries.ITEM,
            Identifier.fromNamespaceAndPath(MODID, "kinetic_tnt")
    );

    // Register Kinetic TNT Block
    public static final RegistryObject<Block> KINETIC_TNT = BLOCKS.register("kinetic_tnt",
            () -> new KineticTNTBlock(BlockBehaviour.Properties.of()
                    .setId(BLOCK_KEY)
                    .sound(SoundType.SAND)
                    .strength(0.5F)));

    // Register Item form
    public static final RegistryObject<Item> KINETIC_TNT_ITEM = ITEMS.register("kinetic_tnt",
            () -> new BlockItem(KINETIC_TNT.get(), new Item.Properties().setId(ITEM_KEY)));

    // Register Custom Creative Tab
    public static final RegistryObject<CreativeModeTab> KINETIC_TAB = CREATIVE_MODE_TABS.register("kinetic_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.literal("Kinetic TNT"))
                    .icon(() -> new ItemStack(KINETIC_TNT_ITEM.get()))
                    .displayItems((parameters, output) -> output.accept(KINETIC_TNT_ITEM.get()))
                    .build());

    public KineticTNTMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();
        BLOCKS.register(modBusGroup);
        ITEMS.register(modBusGroup);
        CREATIVE_MODE_TABS.register(modBusGroup);
    }
}
package com.example.furniturepower;

import com.example.furniturepower.block.BlockElectricGenerator;
import com.example.furniturepower.block.BlockPowerCable;
import com.example.furniturepower.config.PowerConfig;
import com.example.furniturepower.event.PowerEvents;
import com.example.furniturepower.tile.GeneratorTile;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.RegistryEvent;

@Mod(modid = FurniturePower.MODID, name = FurniturePower.NAME, version = FurniturePower.VERSION,
        dependencies = "required-after:cfm@[6.3.2,)")
public final class FurniturePower {
    public static final String MODID = "furniturepower";
    public static final String NAME = "Furniture Power";
    public static final String VERSION = "2.0.0";

    public static Block ELECTRIC_GENERATOR;
    public static Block POWER_CABLE;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        PowerConfig.load(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(new PowerEvents());
        GameRegistry.registerTileEntity(GeneratorTile.class, new ResourceLocation(MODID, "electric_generator"));
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static final class Registry {
        private Registry() {}

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            ELECTRIC_GENERATOR = new BlockElectricGenerator();
            POWER_CABLE = new BlockPowerCable();
            event.getRegistry().register(ELECTRIC_GENERATOR.setRegistryName(MODID, "electric_generator").setTranslationKey(MODID + ".electric_generator"));
            event.getRegistry().register(POWER_CABLE.setRegistryName(MODID, "power_cable").setTranslationKey(MODID + ".power_cable"));
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().register(new ItemBlock(ELECTRIC_GENERATOR).setRegistryName(ELECTRIC_GENERATOR.getRegistryName()));
            event.getRegistry().register(new ItemBlock(POWER_CABLE).setRegistryName(POWER_CABLE.getRegistryName()));
        }
    }
}

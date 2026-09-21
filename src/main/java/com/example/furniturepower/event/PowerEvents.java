package com.example.furniturepower.event;

import com.example.furniturepower.FurniturePower;
import com.example.furniturepower.config.PowerConfig;
import com.example.furniturepower.tile.GeneratorTile;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

/**
 * Integrates the original CFM electronics without copying or replacing its furniture.
 * Reflection is used only for CFM TileEntity methods so the add-on remains loosely coupled
 * to the exact implementation while still requiring CFM 6.3.2 at runtime.
 */
public final class PowerEvents {

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getWorld().isRemote || event.getHand() != EnumHand.MAIN_HAND) return;
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Block block = world.getBlockState(pos).getBlock();
        if (!isElectronicBlock(block)) return;

        String id = registryName(block);
        // Turning an already-on light off does not require power.
        boolean turningOff = id.endsWith("_on");
        if (!turningOff && !consumeNearbyPower(world, pos, PowerConfig.startupCost)) {
            event.setCanceled(true);
            event.getEntityPlayer().sendMessage(new TextComponentString(
                    "Sem energia: conecte o aparelho a um gerador usando os cabos."));
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.world.isRemote) return;
        if (event.world.getTotalWorldTime() % PowerConfig.energyTickInterval != 0) return;

        World world = event.world;
        for (TileEntity te : world.loadedTileEntityList) {
            if (te == null || te instanceof GeneratorTile) continue;
            Block block = world.getBlockState(te.getPos()).getBlock();
            if (!isElectronicBlock(block)) continue;

            String teName = te.getClass().getSimpleName();
            String id = registryName(block);
            int cost = energyCost(block);

            // Computer is a GUI device, so its power is charged while its container is open.
            if (teName.equals("TileEntityComputer")) continue;

            if (!isContinuouslyActive(te, teName, id)) continue;
            if (!consumeNearbyPower(world, te.getPos(), cost)) {
                stopWithoutPower(te, teName);
            }
        }

    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        if (player == null || player.world == null || player.world.isRemote) return;
        Container open = player.openContainer;
        if (open == null || !open.getClass().getName().contains("ContainerComputer")) return;
        BlockPos pos = findTileEntityBySimpleName(player.world, player, "TileEntityComputer");
        if (pos != null && player.ticksExisted % PowerConfig.energyTickInterval == 0 &&
                !consumeNearbyPower(player.world, pos, PowerConfig.computer)) {
            player.closeScreen();
            player.sendMessage(new TextComponentString("Computador desligado: sem energia."));
        }
    }

    public static boolean isElectronicBlock(Block block) {
        if (block == null) return false;
        ResourceLocation id = block.getRegistryName();
        if (id == null || !"cfm".equals(id.getResourceDomain())) return false;
        String n = id.getResourcePath().toLowerCase();
        switch (n) {
            case "tv": case "modern_tv": case "computer": case "fridge": case "freezer":
            case "microwave": case "dishwasher": case "washing_machine": case "blender":
            case "toaster": case "printer": case "oven": case "stereo": case "ceiling_fan":
            case "digital_clock": case "ceiling_light_off": case "ceiling_light_on":
            case "modern_light_off": case "modern_light_on":
                return true;
            default:
                return false;
        }
    }

    public static int energyCost(Block block) {
        String n = registryName(block);
        switch (n) {
            case "tv": return PowerConfig.tv;
            case "modern_tv": return PowerConfig.modernTv;
            case "computer": return PowerConfig.computer;
            case "fridge": return PowerConfig.fridge;
            case "freezer": return PowerConfig.freezer;
            case "microwave": return PowerConfig.microwave;
            case "dishwasher": return PowerConfig.dishwasher;
            case "washing_machine": return PowerConfig.washingMachine;
            case "blender": return PowerConfig.blender;
            case "toaster": return PowerConfig.toaster;
            case "printer": return PowerConfig.printer;
            case "oven": return PowerConfig.oven;
            case "stereo": return PowerConfig.stereo;
            case "ceiling_fan": return PowerConfig.ceilingFan;
            case "digital_clock": return PowerConfig.digitalClock;
            case "ceiling_light_off": case "ceiling_light_on": return PowerConfig.ceilingLight;
            case "modern_light_off": case "modern_light_on": return PowerConfig.modernLight;
            default: return 0;
        }
    }

    public static boolean consumeNearbyPower(World world, BlockPos appliance, int amount) {
        if (amount <= 0) return true;
        Set<BlockPos> seen = new HashSet<BlockPos>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<BlockPos>();
        ArrayDeque<GeneratorTile> generators = new ArrayDeque<GeneratorTile>();
        queue.add(appliance);
        seen.add(appliance);
        int visited = 0;

        while (!queue.isEmpty() && visited < PowerConfig.maxNetworkNodes) {
            BlockPos current = queue.removeFirst();
            visited++;
            if (current.distanceSq(appliance) > (double) PowerConfig.networkRadius * PowerConfig.networkRadius) continue;

            TileEntity te = world.getTileEntity(current);
            if (te instanceof GeneratorTile) generators.add((GeneratorTile) te);

            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos next = current.offset(facing);
                if (seen.contains(next)) continue;
                if (next.distanceSq(appliance) > (double) PowerConfig.networkRadius * PowerConfig.networkRadius) continue;
                Block nextBlock = world.getBlockState(next).getBlock();
                if (nextBlock == FurniturePower.POWER_CABLE || nextBlock == FurniturePower.ELECTRIC_GENERATOR) {
                    seen.add(next);
                    queue.addLast(next);
                }
            }
        }

        int available = 0;
        for (GeneratorTile generator : generators) {
            available += generator.getEnergyStorage().getEnergyStored();
            if (available >= amount) break;
        }
        if (available < amount) return false;

        int remaining = amount;
        for (GeneratorTile generator : generators) {
            int extracted = generator.getEnergyStorage().extractEnergy(remaining, false);
            remaining -= extracted;
            generator.markDirty();
            if (remaining <= 0) break;
        }
        return remaining <= 0;
    }

    private static boolean isContinuouslyActive(TileEntity te, String teName, String blockId) {
        String method = null;
        if (teName.equals("TileEntityMicrowave")) method = "isCooking";
        else if (teName.equals("TileEntityFreezer")) method = "isFreezing";
        else if (teName.equals("TileEntityDishwasher")) method = "isWashing";
        else if (teName.equals("TileEntityWashingMachine")) method = "isWashing";
        else if (teName.equals("TileEntityBlender")) method = "isBlending";
        else if (teName.equals("TileEntityToaster")) method = "isToasting";
        else if (teName.equals("TileEntityPrinter")) method = "isPrinting";
        else if (teName.equals("TileEntityOven")) return invokeBoolean(te, "isCooking") || invokeBoolean(te, "isBurning");
        else if (teName.equals("TileEntityTV")) method = "isPowered";
        else if (teName.equals("TileEntityCeilingFan")) method = "isPowered";
        return method != null && invokeBoolean(te, method);
    }

    private static void stopWithoutPower(TileEntity te, String teName) {
        String method = null;
        if (teName.equals("TileEntityMicrowave")) method = "stopCooking";
        else if (teName.equals("TileEntityFreezer")) method = "stopFreezing";
        else if (teName.equals("TileEntityDishwasher")) method = "stopWashing";
        else if (teName.equals("TileEntityWashingMachine")) method = "stopWashing";
        else if (teName.equals("TileEntityTV")) { invokeVoid(te, "setPowered", false); return; }
        else if (teName.equals("TileEntityCeilingFan")) { invokeVoid(te, "setPowered", false); return; }
        if (method != null) invokeVoid(te, method);
        te.markDirty();
    }

    private static boolean invokeBoolean(Object target, String name) {
        try {
            Method method = target.getClass().getMethod(name);
            Object result = method.invoke(target);
            return result instanceof Boolean && ((Boolean) result).booleanValue();
        } catch (Exception ignored) {
            return false;
        }
    }

    private static void invokeVoid(Object target, String name, Object... args) {
        try {
            Method method = null;
            if (args.length == 0) method = target.getClass().getMethod(name);
            else if (args.length == 1 && args[0] instanceof Boolean) method = target.getClass().getMethod(name, boolean.class);
            if (method != null) method.invoke(target, args);
        } catch (Exception ignored) {
            // An appliance without a public stop method keeps its own state machine.
        }
    }

    private static String registryName(Block block) {
        ResourceLocation id = block == null ? null : block.getRegistryName();
        return id == null ? "" : id.getResourcePath().toLowerCase();
    }

    private static BlockPos findTileEntityBySimpleName(World world, EntityPlayer player, String simpleName) {
        BlockPos base = player.getPosition();
        for (int dx = -2; dx <= 2; dx++) for (int dy = -2; dy <= 3; dy++) for (int dz = -2; dz <= 2; dz++) {
            BlockPos p = base.add(dx, dy, dz);
            TileEntity te = world.getTileEntity(p);
            if (te != null && te.getClass().getSimpleName().equals(simpleName)) return p;
        }
        return null;
    }
}

package com.example.furniturepower.block;

import com.example.furniturepower.FurniturePower;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class BlockPowerCable extends Block {
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");

    public BlockPowerCable() {
        super(Material.CIRCUITS);
        setHardness(0.4F);
        setLightOpacity(0);
        setCreativeTab(net.minecraft.creativetab.CreativeTabs.REDSTONE);
        setDefaultState(blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false)
                .withProperty(SOUTH, false).withProperty(WEST, false).withProperty(UP, false).withProperty(DOWN, false));
    }

    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, UP, DOWN); }
    @Override public int getMetaFromState(IBlockState state) { return 0; }
    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState(); }

    @Override public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.withProperty(NORTH, canConnect(world, pos.offset(EnumFacing.NORTH)))
                .withProperty(EAST, canConnect(world, pos.offset(EnumFacing.EAST)))
                .withProperty(SOUTH, canConnect(world, pos.offset(EnumFacing.SOUTH)))
                .withProperty(WEST, canConnect(world, pos.offset(EnumFacing.WEST)))
                .withProperty(UP, canConnect(world, pos.offset(EnumFacing.UP)))
                .withProperty(DOWN, canConnect(world, pos.offset(EnumFacing.DOWN)));
    }

    private boolean canConnect(IBlockAccess world, BlockPos pos) {
        Block b = world.getBlockState(pos).getBlock();
        if (b == FurniturePower.POWER_CABLE || b == FurniturePower.ELECTRIC_GENERATOR) return true;
        return false;
    }
}

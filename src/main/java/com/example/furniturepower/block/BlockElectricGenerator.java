package com.example.furniturepower.block;

import com.example.furniturepower.config.PowerConfig;
import com.example.furniturepower.tile.GeneratorTile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockElectricGenerator extends Block {
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public BlockElectricGenerator() {
        super(Material.IRON);
        setHardness(2.5F);
        setHarvestLevel("pickaxe", 1);
        setCreativeTab(net.minecraft.creativetab.CreativeTabs.REDSTONE);
        setDefaultState(blockState.getBaseState().withProperty(ACTIVE, false));
    }

    @Override protected BlockStateContainer createBlockState() { return new BlockStateContainer(this, ACTIVE); }
    @Override public int getMetaFromState(IBlockState state) { return state.getValue(ACTIVE) ? 1 : 0; }
    @Override public IBlockState getStateFromMeta(int meta) { return getDefaultState().withProperty(ACTIVE, meta != 0); }

    @Override public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return state.withProperty(ACTIVE, te instanceof GeneratorTile && ((GeneratorTile) te).isFueled());
    }

    @Override public boolean hasTileEntity(IBlockState state) { return true; }
    @Override public TileEntity createTileEntity(World world, IBlockState state) { return new GeneratorTile(); }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof GeneratorTile)) return true;
        if (!world.isRemote) {
            GeneratorTile generator = (GeneratorTile) te;
            ItemStack held = player.getHeldItem(hand);
            if (generator.addFuel(held) > 0) {
                player.sendMessage(new TextComponentString("Gerador abastecido. Combustivel: " + generator.getFuelTicks() + " t"));
            } else {
                player.sendMessage(new TextComponentString("Energia: " + generator.getEnergyStorage().getEnergyStored() + "/" +
                        PowerConfig.capacity + " FE | Combustivel: " + generator.getFuelTicks() + " t"));
            }
        }
        return true;
    }
}

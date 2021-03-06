package com.bartz24.usefulnullifiers.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import com.bartz24.usefulnullifiers.References;
import com.bartz24.usefulnullifiers.UsefulNullifiers;
import com.bartz24.usefulnullifiers.inventory.OverflowInventory;
import com.bartz24.usefulnullifiers.registry.ModCreativeTabs;
import com.bartz24.usefulnullifiers.registry.ModGuiHandler;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class OverflowNullifierItem extends Item {
	public OverflowNullifierItem(String unlocalizedName, String registryName) {
		this.setUnlocalizedName(References.ModID + "." + unlocalizedName);
		setRegistryName(registryName);
		this.setCreativeTab(ModCreativeTabs.tabMain);
		this.setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		OverflowInventory inv = new OverflowInventory(itemStack);
		ItemStack invStack = inv.getStackInSlot(0);
		Block block = invStack.func_190926_b() ? Blocks.AIR : Block.getBlockFromItem(invStack.getItem());

		RayTraceResult rayTrace = this.rayTrace(world, player, true);

		if (rayTrace == null || block == Blocks.AIR || world.isAirBlock(rayTrace.getBlockPos())) {
			if (!world.isRemote) {
				player.openGui(UsefulNullifiers.instance, ModGuiHandler.OverflowGUI, world,
						player.inventory.currentItem, 0, 0);
			}
		} else {
			BlockPos hitPos = rayTrace.getBlockPos();
			EnumFacing blockHitSide = rayTrace.sideHit;
			if (block.canPlaceBlockAt(world, hitPos.add(blockHitSide.getDirectionVec()))
					&& (world.getBlockState(hitPos.add(blockHitSide.getDirectionVec())) == null
							|| world.getBlockState(hitPos.add(blockHitSide.getDirectionVec())).getBlock()
									.isReplaceable(world, hitPos.add(blockHitSide.getDirectionVec())))) {
				world.playSound(player, hitPos.add(blockHitSide.getDirectionVec()),
						block.getSoundType().getPlaceSound(), SoundCategory.BLOCKS, block.getSoundType().getVolume(),
						block.getSoundType().getPitch());
				player.swingArm(hand);

				if (!world.isRemote) {
					world.setBlockState(hitPos.add(blockHitSide.getDirectionVec()),
							block.onBlockPlaced(world, hitPos.add(blockHitSide.getDirectionVec()), blockHitSide,
									(float) rayTrace.hitVec.xCoord, (float) rayTrace.hitVec.yCoord,
									(float) rayTrace.hitVec.zCoord, invStack.getMetadata(), player),
							3);

					inv.decrStackSize(0, 1);
					inv.markDirty();
				}
			}
		}
		return new ActionResult(EnumActionResult.SUCCESS, itemStack);
	}

	public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {

		list.add(TextFormatting.BLUE + "Similar to a dev/null/");
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			list.add(TextFormatting.DARK_GREEN
					+ "Places blocks stored and destroys any extra items picked up of the same type.");
		} else
			list.add(TextFormatting.DARK_GREEN + "Hold LSHIFT for description.");
	}

	public String getItemStackDisplayName(ItemStack stack) {
		OverflowInventory inv = new OverflowInventory(stack);
		String name = TextFormatting.GREEN
				+ (inv.getStackInSlot(0) == ItemStack.field_190927_a ? "None" : inv.getStackInSlot(0).getDisplayName()
				+ " x " + inv.getStackInSlot(0).func_190916_E());
		return super.getItemStackDisplayName(stack) + " (" + name + TextFormatting.WHITE + ")";
	}
}

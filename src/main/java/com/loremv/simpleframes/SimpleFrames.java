package com.loremv.simpleframes;

import com.loremv.simpleframes.blocks.*;
import com.loremv.simpleframes.utility.CapturedBlockStorage;
import com.loremv.simpleframes.utility.FakeryBakery;
import com.loremv.simpleframes.utility.FrameBlockUtils;
import com.loremv.simpleframes.utility.ModelIdeas;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SimpleFrames implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Identifier SYNC_TEXTURE_PACKET = new Identifier("simpleframes","sync_texture_packet");
	//public static final TagKey<Block> FRAMES = TagKey.of(Registries.BLOCK_KEY,new Identifier("simpleframes", "frames"));
	public static final ItemGroup TAB = FabricItemGroup.builder().icon(()->new ItemStack(SimpleFrames.FRAME_BLOCK)).displayName(Text.translatable("itemGroup.simpleframes.tab")).build();
	public static int STATE = 0;
	public static final Logger LOGGER = LoggerFactory.getLogger("simpleframes");
	public static final FakeryBakery BAKERY = new FakeryBakery();
	public static final CapturedBlockStorage STORAGE = new CapturedBlockStorage();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModelIdeas.init();
		registerBlocks();
		registerItems();
		LOGGER.info("Hello Fabric world!");

		CommandRegistrationCallback.EVENT.register((dispatcher, phase,registrationEnvironment) -> dispatcher.register(

				literal("simpleframes")
						.requires(source -> source.hasPermissionLevel(2))
						.then(literal("set")
							.then(argument("id", IntegerArgumentType.integer())
							.then(argument("block", BlockStateArgumentType.blockState(phase))
							.executes(context -> {
								NbtCompound compound = context.getSource().getServer().getDataCommandStorage().get(FrameBlockUtils.USED_STATES_STORAGE);
								NbtList states = (NbtList) compound.get("states");
								states.set(IntegerArgumentType.getInteger(context,"id"), NbtHelper.fromBlockState(BlockStateArgumentType.getBlockState(context,"block").getBlockState()));
								compound.put("states",states);
								context.getSource().getServer().getDataCommandStorage().set(FrameBlockUtils.USED_STATES_STORAGE,compound);
								context.getSource().sendFeedback(()->Text.of("changed the state at that id"),false);
								FrameBlockUtils.forceUpdate(context.getSource().getPlayer());
								return 1;
						}))))
						.then(literal("report").executes(context ->
						{
							NbtCompound compound = context.getSource().getServer().getDataCommandStorage().get(FrameBlockUtils.USED_STATES_STORAGE);
							NbtList states = (NbtList) compound.get("states");
							NbtCompound analysis = compound.getCompound("analysis");
							context.getSource().sendFeedback(()->Text.of("==block report=="),false);
							for(int i = 0; i<states.size(); i++)
							{
								int finalI = i;
								context.getSource().sendFeedback(()->Text.of(finalI +": "+states.get(finalI)+" (in world="+analysis.get(""+finalI)+")"),false);
							}
							context.getSource().sendFeedback(()->Text.of("==block report=="),false);

							return 1;
						}))



		));


	}

	public static final Block FRAME_BASE = new Block(AbstractBlock.Settings.create());
	public static final FrameStairBlock FRAME_STAIR_BLOCK = new FrameStairBlock().thenRegister(
			new String[]{"block/framed_stairs","stair"},
			new String[]{"block/framed_stairs_inner","stair"},
			new String[]{"block/framed_stairs_outer","stair"});;
	public static final FrameBlock FRAME_BLOCK = new FrameBlock("cobblestone");
	public static final FrameSlabBlock FRAME_SLAB_BLOCK = new FrameSlabBlock();
	public static final FrameCarpetBlock FRAME_CARPET_BLOCK = new FrameCarpetBlock();
	public static final FrameStairBlock FRAME_RAMP_BLOCK = new FrameStairBlock().thenRegister(new String[]{"block/framed_ramp","ramp"});
	public static final FramePartitionBlock FRAME_PARTITION = new FramePartitionBlock("block/framed_partition");
	public static final FramePartitionBlock FRAME_HFENCE = new FramePartitionBlock("block/framed_hfence");
	public static final FramePanelBlock FRAME_PANEL_BLOCK = new FramePanelBlock("block/framed_panel");
	public static final FrameCoverBlock FRAME_COVER_BLOCK = new FrameCoverBlock("block/framed_cover");
	public static final FrameCoverBlock FRAME_COVER_CORNER_BLOCK = new FrameCoverBlock("block/framed_cover_corner");
	public static final FrameCoverBlock FRAME_COVER_INSET_BLOCK = new FrameCoverBlock("block/framed_inset_cover");
	public static final FrameSpikeBlock FRAME_SPIKE_BLOCK = new FrameSpikeBlock();
	public static final FrameBlock NON_JSON_FRAME_BLOCK = new FrameBlock("cobblestone");
	public static final ResizedFrameBlock FRAME_DOWN_EXTENDED = new ResizedFrameBlock("block/framed_extended_block_down",Block.createCuboidShape(0,-8,0,16,16,16));
	public static final ResizedFrameBlock FRAME_UP_EXTENDED = new ResizedFrameBlock("block/framed_extended_block_up",Block.createCuboidShape(0,0,0,16,24,16));
	public static final FrameBigBlock FRAME_BIG_BLOCK = new FrameBigBlock(2,null);
	public static final FrameBigBlock FRAME_HUGE_BLOCK = new FrameBigBlock(4,"CUBE4x4");
	public static final FramedChest FRAMED_CHEST = new FramedChest();
	public static final CascadingFrameBlock CASCADING_FRAME_BLOCK = new CascadingFrameBlock();
	public static final FrameFenceBlock FRAME_FENCE_BLOCK = new FrameFenceBlock();
	public static final FramedDoor FRAMED_DOOR = new FramedDoor();
	public void registerBlocks()
	{
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_stairs"),FRAME_STAIR_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","frame_base"),FRAME_BASE);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_block"),FRAME_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_carpet_block"),FRAME_CARPET_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_slab_block"),FRAME_SLAB_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_ramp_block"),FRAME_RAMP_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","non_json_framed_block"),NON_JSON_FRAME_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_partition"),FRAME_PARTITION);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_panel"),FRAME_PANEL_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_hfence"),FRAME_HFENCE);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_cover"),FRAME_COVER_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_spike_block"),FRAME_SPIKE_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_cover_corner"),FRAME_COVER_CORNER_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_inset_cover"),FRAME_COVER_INSET_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_big_block"),FRAME_BIG_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_extended_block_down"),FRAME_DOWN_EXTENDED);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_extended_block_up"),FRAME_UP_EXTENDED);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_chest"),FRAMED_CHEST);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_huge_block"),FRAME_HUGE_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","cascading_frame_block"),CASCADING_FRAME_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_fence"),FRAME_FENCE_BLOCK);
		Registry.register(Registries.BLOCK,new Identifier("simpleframes","framed_door"),FRAMED_DOOR);
	}

	public void registerItems()
	{
		Registry.register(Registries.ITEM_GROUP, new Identifier("simpleframes", "tab"), TAB);
		int firstid = Registries.ITEM.size();
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_stairs"),new BlockItem(FRAME_STAIR_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_block"),new BlockItem(FRAME_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_carpet_block"),new BlockItem(FRAME_CARPET_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_slab_block"),new BlockItem(FRAME_SLAB_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_ramp_block"),new BlockItem(FRAME_RAMP_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_partition"),new BlockItem(FRAME_PARTITION,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_panel"),new BlockItem(FRAME_PANEL_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_hfence"),new BlockItem(FRAME_HFENCE,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_cover"),new BlockItemWithLore(FRAME_COVER_BLOCK,new Item.Settings(), Arrays.asList("you can cover corners with this","just make sure you place the second on the left"," then third on the right!")));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_cover_corner"),new BlockItem(FRAME_COVER_CORNER_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_inset_cover"),new BlockItem(FRAME_COVER_INSET_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_spike_block"),new BlockItemWithLore(FRAME_SPIKE_BLOCK,new Item.Settings(), List.of("doubles fall damage when landed upon")));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_big_block"),new BlockItemWithLore(FRAME_BIG_BLOCK,new Item.Settings(), List.of("creates a 2x2 block large...block")));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_extended_block_down"),new BlockItemWithLore(FRAME_DOWN_EXTENDED,new Item.Settings(), List.of("good for cursed slabs")));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_extended_block_up"),new BlockItemWithLore(FRAME_UP_EXTENDED,new Item.Settings(), List.of("good for cursed slabs")));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_chest"),new BlockItem(FRAMED_CHEST,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_huge_block"),new BlockItemWithLore(FRAME_HUGE_BLOCK,new Item.Settings().rarity(Rarity.RARE), List.of("creates a 4x4 block large...block")));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_fence"),new BlockItem(FRAME_FENCE_BLOCK,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","framed_door"),new BlockItem(FRAMED_DOOR,new Item.Settings()));
		int rsize = Registries.ITEM.size();



		Registry.register(Registries.ITEM,new Identifier("simpleframes","frame_base"),new BlockItem(FRAME_BASE,new Item.Settings()));
		Registry.register(Registries.ITEM,new Identifier("simpleframes","non_json_framed_block"),new BlockItem(NON_JSON_FRAME_BLOCK,new Item.Settings()));


		ItemGroupEvents.modifyEntriesEvent(Registries.ITEM_GROUP.getKey(TAB).get()).register(content -> {
			for (int i = firstid; i < rsize; i++) {
				content.add(Registries.ITEM.get(i));
			}
		});
	}
}

package com.loremv.simpleframes.blocks;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockItemWithLore extends BlockItem {
    List<String> lore;
    public BlockItemWithLore(Block block, Settings settings,List<String> lore) {
        super(block, settings);
        this.lore=lore;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.addAll(lore.stream().map(Text::literal).map(a->a.fillStyle(Style.EMPTY.withColor(Formatting.AQUA))).toList());
    }
}

/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.fluid;

import com.hrznstudio.titanium.module.RegistryHelper;
import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import net.minecraft.core.Registry;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;

public class TitaniumFluidInstance {

    private Fluid flowingFluid;
    private Fluid sourceFluid;
    private Item bucketFluid;
    private Block blockFluid;
    private final String fluid;

    public TitaniumFluidInstance(RegistryHelper helper, String fluid, FluidAttributes.Builder attributes, CreativeModeTab group) {
        this.fluid = fluid;
        this.sourceFluid = helper.registerGeneric(Registry.FLUID, fluid, () -> new TitaniumFluid.Source(attributes, this));
        this.flowingFluid = helper.registerGeneric(Registry.FLUID, fluid + "_flowing", () ->  new TitaniumFluid.Flowing(attributes, this));
        this.bucketFluid = helper.registerGeneric(Registry.ITEM, fluid + "_bucket", () -> new BucketItem(this.sourceFluid, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).tab(group)));
        this.blockFluid = helper.registerGeneric(Registry.BLOCK, fluid, () -> new LiquidBlock((FlowingFluid) sourceFluid, Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops()));
    }

    public Fluid  getFlowingFluid() {
        return flowingFluid;
    }

    public Fluid  getSourceFluid() {
        return sourceFluid;
    }

    public Item getBucketFluid() {
        return bucketFluid;
    }

    public Block getBlockFluid() {
        return blockFluid;
    }

    public String getFluid() {
        return fluid;
    }
}

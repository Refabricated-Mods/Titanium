/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.module;

import com.hrznstudio.titanium.block.BasicBlock;
import com.hrznstudio.titanium.block.BasicTileBlock;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryHelper {
    List<BlockEntityType<?>> blockEntityTypes = new ArrayList<>();

    private final String modId;

    public RegistryHelper(String modId) {
        this.modId = modId;
    }

    private  <T> T register(Registry<T> cl, String name, Supplier<T> object) {
        return Registry.register(cl, new ResourceLocation(modId, name), object.get());
    }

    public <T> T registerGeneric(Registry<T> cl, String name, Supplier<T> object) {
        return this.register(cl, name, object);
    }

    public BlockEntityType<?> registerBlockEntityType(String name, Supplier<BlockEntityType<?>> object) {
        BlockEntityType<?> type = registerGeneric(Registry.BLOCK_ENTITY_TYPE, name, object);
        blockEntityTypes.add(type);
        return type;
    }

    public EntityType<?> registerEntityType(String name, Supplier<EntityType<?>> object) {
        return registerGeneric(Registry.ENTITY_TYPE, name, object);
    }

    public Block registerBlockWithItem(String name, Supplier<? extends BasicBlock> blockSupplier){
        Block blockRegistryObject = registerGeneric(Registry.BLOCK, name, blockSupplier::get);
        registerGeneric(Registry.ITEM, name, () -> new BlockItem(blockRegistryObject, new Item.Properties().tab(((BasicBlock) blockRegistryObject).getItemGroup())));
        return blockRegistryObject;
    }

    public Block registerBlockWithItem(String name, Supplier<? extends Block> blockSupplier, Function<Block, Supplier<Item>> itemSupplier){
        Block block = registerGeneric(Registry.BLOCK, name, blockSupplier::get);
        registerGeneric(Registry.ITEM, name, itemSupplier.apply(block));
        return block;
    }

    public Pair<Block, BlockEntityType<?>> registerBlockWithTile(String name, Supplier<BasicTileBlock> blockSupplier){
        Block block = registerBlockWithItem(name, blockSupplier);
        return Pair.of(block, registerBlockEntityType(name, () -> BlockEntityType.Builder.of(((BasicTileBlock<?>)block).getTileEntityFactory(), block).build(null)));
    }

    public Pair<Block, BlockEntityType<?>> registerBlockWithTileItem(String name, Supplier<BasicTileBlock> blockSupplier, Function<Block, Supplier<Item>> itemSupplier){
        Block block = registerBlockWithItem(name, blockSupplier, itemSupplier);
        return Pair.of(block, registerBlockEntityType(name, () -> BlockEntityType.Builder.of(((BasicTileBlock<?>)block).getTileEntityFactory(), block).build(null)));
    }
}

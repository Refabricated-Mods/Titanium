/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.datagenerator.loot.block;

import com.hrznstudio.titanium.util.NonNullLazy;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.function.BiConsumer;

public class BasicBlockLootTables extends FabricBlockLootTableProvider {
    private final NonNullLazy<List<Block>> blocksToProcess;

    public BasicBlockLootTables(NonNullLazy<List<Block>> blocksToProcess, FabricDataGenerator dataGenerator) {
        super(dataGenerator);
        this.blocksToProcess = blocksToProcess;
    }


    @Override
    public void generateBlockLootTables() {
        blocksToProcess.get()
            .forEach(block -> {
                if (block instanceof IBlockLootTableProvider) {
                    this.add(block, ((IBlockLootTableProvider) block).getLootTable(this));
                }
            });
    }

    public LootTable.Builder droppingNothing() {
        return LootTable.lootTable();
    }

    public LootTable.Builder droppingSelf(ItemLike itemProvider) {
        return LootTable.lootTable()
            .withPool(applyExplosionCondition(itemProvider, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(itemProvider))));
    }

    public LootTable.Builder droppingSelfWithNbt(ItemLike itemProvider, CopyNbtFunction.Builder nbtBuilder) {
        return LootTable.lootTable()
            .withPool(applyExplosionCondition(itemProvider, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(itemProvider).apply(nbtBuilder))));
    }

    protected Iterable<Block> getKnownBlocks() {
        return blocksToProcess.get();
    }
}

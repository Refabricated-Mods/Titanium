/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.datagenerator.loot;

import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.util.NonNullLazy;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLootTableProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TitaniumLootTableProvider extends FabricBlockLootTableProvider {
    private final NonNullLazy<List<Block>> blocksToProcess;

    public TitaniumLootTableProvider(FabricDataGenerator dataGenerator, NonNullLazy<List<Block>> blocks) {
        super(dataGenerator);
        this.blocksToProcess = blocks;
    }

    protected FabricBlockLootTableProvider createBlockLootTables() {
        return new BasicBlockLootTables(blocksToProcess, this.dataGenerator);
    }

    @Override
    public void accept(BiConsumer<ResourceLocation, LootTable.Builder> biConsumer) {
        super.accept(biConsumer);
        createBlockLootTables().accept(biConsumer);
    }

    @Override
    protected void generateBlockLootTables() {
    }
}

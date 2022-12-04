package com.hrznstudio.titanium.datagenerator;

import com.hrznstudio.titanium.datagenerator.loot.TitaniumLootTableProvider;
import com.hrznstudio.titanium.datagenerator.model.BlockItemModelGeneratorProvider;
import com.hrznstudio.titanium.recipe.generator.titanium.JsonRecipeSerializerProvider;
import com.hrznstudio.titanium.util.NonNullLazy;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.hrznstudio.titanium.Titanium.MODID;

public class TitaniumDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        NonNullLazy<List<Block>> blocksToProcess = NonNullLazy.of(() ->
            Registry.BLOCK
                .stream()
                .filter(basicBlock -> Optional.ofNullable(basicBlock.getRegistryName())
                    .map(ResourceLocation::getNamespace)
                    .filter(MODID::equalsIgnoreCase)
                    .isPresent())
                .collect(Collectors.toList())
        );
        fabricDataGenerator.addProvider(f -> new BlockItemModelGeneratorProvider(f, MODID, blocksToProcess));
        fabricDataGenerator.addProvider(f -> new TitaniumLootTableProvider(f, blocksToProcess));
        fabricDataGenerator.addProvider(f -> new JsonRecipeSerializerProvider(fabricDataGenerator, MODID));
    }
}

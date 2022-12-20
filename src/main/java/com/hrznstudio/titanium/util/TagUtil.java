/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.util;

import com.hrznstudio.titanium._impl.TagConfig;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TagUtil {

    public static <T> boolean hasTag(Registry<T> registry, T type, TagKey<T> tag) {
        return registry.getTag(tag).map(n -> n.contains(Holder.direct(type))).orElse(false);
    }

    public static Collection<Block> getAllBlockTags() {
        return Registry.BLOCK.getTags().map(t -> t.getSecond().stream().map(Holder::value).toList()).flatMap(Collection::stream).toList();
    }

    public static Collection<Item> getAllItemTags() {
        return Registry.ITEM.getTags().map(t -> t.getSecond().stream().map(Holder::value).toList()).flatMap(Collection::stream).toList();
    }

    public static Collection<Fluid> getAllFluidTags() {
        return Registry.FLUID.getTags().map(t -> t.getSecond().stream().map(Holder::value).toList()).flatMap(Collection::stream).toList();
    }

    public static <T> Collection<T> getAllEntries(Registry<T> registry, TagKey<T>... tags) {
        if (tags.length == 0)
            return Collections.emptyList();
        if (tags.length == 1)
            return registry.getTag(tags[0]).map(n -> n.stream().map(Holder::value).toList()).orElse(List.of()); //getAllElements
        List<T> list = new ArrayList<>();
        for (TagKey<T> tag : tags) {
            list.addAll(registry.getTag(tag).map(n -> n.stream().map(Holder::value).toList()).orElse(List.of())); //getAllElements
        }
        return list;
    }

    public static <T> Collection<T> getAllEntries(Registry<T> registry, TagKey<T> tag) {
        return registry.getTag(tag).map(n -> n.stream().map(Holder::value).toList()).orElse(List.of());
    }

    public static <T> TagKey<T> getOrCreateTag(Registry<T> registry, ResourceLocation resourceLocation) {
        /*
        if (registry.tags().stream().anyMatch(ts -> ts.getKey().location().equals(resourceLocation))) {

        }
        return collection.getTagOrEmpty(resourceLocation);
        */
        return TagKey.create(registry.key(), resourceLocation);
    }

    public static TagKey<Item> getItemTag(ResourceLocation resourceLocation) {
        /*
        if (ItemTags.getAllTags().getAvailableTags().contains(resourceLocation)) {
            return ItemTags.getAllTags().getTag(resourceLocation);
        }
        return ItemTags.create(resourceLocation);
        */

        return getOrCreateTag(Registry.ITEM, resourceLocation);
    }

    public static TagKey<Block> getBlockTag(ResourceLocation resourceLocation) {
        /*if (BlockTags.getAllTags().getAvailableTags().contains(resourceLocation)) {
            return BlockTags.getAllTags().getTag(resourceLocation);
        }
        return BlockTags.create(resourceLocation);*/
        return getOrCreateTag(Registry.BLOCK, resourceLocation);

    }

    public static TagKey<EntityType<?>> getEntityTypeTag(ResourceLocation resourceLocation) {
        /*if (EntityTypeTags.getAllTags().getAvailableTags().contains(resourceLocation)) {
            return EntityTypeTags.getAllTags().getTag(resourceLocation);
        }
        return TagKey.create(Registry.ENTITY_TYPE_REGISTRY, resourceLocation);*/
        return getOrCreateTag(Registry.ENTITY_TYPE, resourceLocation);
    }

    public static TagKey<Fluid> getFluidTag(ResourceLocation resourceLocation) {
        /*if (FluidTags.getAllTags().getAvailableTags().contains(resourceLocation)) {
            return FluidTags.getAllTags().getTag(resourceLocation);
        }
        Registry.FLUID_REGISTRY.cast()
        return FluidTags.create(resourceLocation);*/
        return getOrCreateTag(Registry.FLUID, resourceLocation);
    }

    public static ItemStack getItemWithPreference(TagKey<Item> tagKey){
        Collection<Item> collection = getAllEntries(Registry.ITEM, tagKey);
        if (collection.isEmpty()) return ItemStack.EMPTY;
        List<Item> elements = collection.stream().toList();
        for (String modid : TagConfig.INSTANCE.ITEM_PREFERENCE) {
            for (Item allElement : collection) {
                if (allElement.getRegistryName().getNamespace().equalsIgnoreCase(modid)) return new ItemStack(allElement);
            }
        }
        return new ItemStack(elements.get(0));
    }
}

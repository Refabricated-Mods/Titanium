/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium._impl;

import com.hrznstudio.titanium.annotation.config.ConfigVal;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.Arrays;
import java.util.List;

@Config(name = "titanium/titanium-tags")
public class TagConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static TagConfig INSTANCE;
    @Comment(value = "A list of mod ids sorted by preference when getting an Item for a tag")
    public List<String> ITEM_PREFERENCE = Arrays.asList("minecraft" , "emendatusenigmatica", "immersiveengineering", "thermal", "create", "mekanism", "jaopca", "kubejs", "appliedenergistics2", "pneumaticcraft", "occultism", "tmechworks", "industrialforegoing", "botania", "quark", "pedestals");

    public static void init(){
        AutoConfig.register(TagConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(TagConfig.class).getConfig();
    }
}

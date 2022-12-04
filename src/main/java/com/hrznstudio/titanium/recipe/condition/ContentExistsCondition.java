/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.recipe.condition;

import com.hrznstudio.titanium.Titanium;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;


public class ContentExistsCondition implements ICondition {
    public static final ResourceLocation NAME = new ResourceLocation(Titanium.MODID, "content_exists");

    private final Registry<?> forgeRegistry;
    private final ResourceLocation contentName;

    public ContentExistsCondition(Registry<?> forgeRegistry, ResourceLocation contentName) {
        this.forgeRegistry = forgeRegistry;
        this.contentName = contentName;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        return forgeRegistry.containsKey(contentName);
    }

    public Registry<?> getForgeRegistry() {
        return forgeRegistry;
    }

    public ResourceLocation getContentName() {
        return this.contentName;
    }
}

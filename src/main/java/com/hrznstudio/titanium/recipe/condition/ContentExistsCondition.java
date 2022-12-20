/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.recipe.condition;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hrznstudio.titanium.Titanium;
import net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.function.Predicate;


public class ContentExistsCondition implements ConditionJsonProvider {

    private final Registry<?> registry;
    private final ResourceLocation contentName;

    public ContentExistsCondition(Registry<?> registry, ResourceLocation contentName) {
        this.registry = registry;
        this.contentName = contentName;
    }

    public static boolean test(JsonObject jsonObject) {
        String registryName = GsonHelper.getAsString(jsonObject, "registry");
        Registry<?> registry = Registry.REGISTRY.get(new ResourceLocation(registryName));
        if (registry == null) {
            Titanium.LOGGER.catching(new JsonParseException("Didn't Find Registry for registry: " + registryName));
            return false;
        }
        ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
        return registry.containsKey(id);
    }

    @Override
    public ResourceLocation getConditionId() {
        return new ResourceLocation(Titanium.MODID, "content_exists");
    }

    @Override
    public void writeParameters(JsonObject json) {
        json.addProperty("registry", registry.key().location().toString());
        json.addProperty("name", contentName.toString());
    }
}

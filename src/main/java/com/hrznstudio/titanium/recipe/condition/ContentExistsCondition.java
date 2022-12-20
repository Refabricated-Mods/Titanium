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
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.function.Predicate;


public class ContentExistsCondition implements Predicate<JsonObject> {

    public static JsonObject condition(Registry<?> registry, ResourceLocation id){
        JsonObject json = new JsonObject();
        json.addProperty("registry", registry.key().location().toString());
        json.addProperty("name", id.toString());
        return json;
    }

    @Override
    public boolean test(JsonObject jsonObject) {
        String registryName = GsonHelper.getAsString(jsonObject, "registry");
        Registry<?> registry = Registry.REGISTRY.get(new ResourceLocation(registryName));
        if (registry == null) {
            Titanium.LOGGER.catching(new JsonParseException("Didn't Find Registry for registry: " + registryName));
            return false;
        }
        ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(jsonObject, "name"));
        return registry.containsKey(id);
    }
}

/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.model.IModelGeometry;
import io.github.fabricators_of_create.porting_lib.model.IModelLoader;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class TitaniumModelLoader implements IModelLoader {

    public static final Event<Load> MODEL_EVENT = EventFactory.createArrayBacked(Load.class, callbacks -> (event) -> {
        for (var callback : callbacks){
            callback.accept(event);
        }
    });

    private final Map<ResourceLocation, UnbakedModel> MODEL_MAP = new HashMap<>();

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        MODEL_MAP.clear();
        MODEL_EVENT.invoker().accept(new TitaniumModelEvent(this));
    }

    @Override
    public IModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        return null;
    }

    public interface Load extends Consumer<TitaniumModelEvent> {}

    public static class TitaniumModelEvent {
        private final TitaniumModelLoader loader;

        public TitaniumModelEvent(TitaniumModelLoader loader) {
            this.loader = loader;
        }

        public void register(ResourceLocation resourceLocation, UnbakedModel model) {
            loader.MODEL_MAP.put(resourceLocation, model);
        }

        public void register(String location, UnbakedModel model) {
            register(new ResourceLocation(location), model);
        }

        public void register(String domain, String location, UnbakedModel model) {
            register(new ResourceLocation(domain, location), model);
        }
    }
}

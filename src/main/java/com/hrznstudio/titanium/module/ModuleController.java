/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.module;

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.plugin.FeaturePlugin;
import com.hrznstudio.titanium.block.tile.IEnergyTile;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.config.AnnotationConfigManager;
import com.hrznstudio.titanium.item.IFluidStorageItem;
import com.hrznstudio.titanium.plugin.PluginManager;
import com.hrznstudio.titanium.plugin.PluginPhase;
import com.hrznstudio.titanium.util.AnnotationUtil;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTransferable;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemTransferable;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraftforge.api.fml.event.config.ModConfigEvent;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleBatteryItem;

public abstract class ModuleController implements ModInitializer {
    private final String modid;
    private final AnnotationConfigManager configManager;
    private final PluginManager modPluginManager;
    private final RegistryHelper registryHelper;

    public ModuleController(String modid) {
        this.modid = modid;
        this.modPluginManager = new PluginManager(modid, FeaturePlugin.FeaturePluginType.MOD, featurePlugin -> FabricLoader.getInstance().isModLoaded(featurePlugin.value()), true);
        this.modPluginManager.execute(PluginPhase.CONSTRUCTION);
        this.registryHelper = new RegistryHelper(this.modid);
        this.configManager = new AnnotationConfigManager(modid);
    }

    @Override
    public void onInitialize() {
        onPreInit();
        onInit();
        onPostInit();
        registryHelper.blockEntityTypes.forEach(blockEntityType -> {
            EnergyStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity instanceof IEnergyTile tile ? tile.getEnergyStorage(direction) : null, blockEntityType);
            FluidStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity instanceof FluidTransferable tile ? tile.getFluidStorage(direction) : null, blockEntityType);
            ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> blockEntity instanceof ItemTransferable tile ? tile.getItemStorage(direction) : null, blockEntityType);
        });
        registryHelper.items.forEach(i -> {
            if (i instanceof IFluidStorageItem item) FluidStorage.ITEM.registerForItems((s, c) -> IFluidStorageItem.createFluidStorage(c, item.getCapacity()), i);
        });
    }

    private void addConfig(AnnotationConfigManager.Type type) {
        for (Class configClass : type.getConfigClass()) {
            if (configManager.isClassManaged(configClass)) return;
        }
        configManager.add(type);
    }

    public void onPreInit() {
        this.modPluginManager.execute(PluginPhase.PRE_INIT);
    }

    public void onInit() {
        initModules();

        this.modPluginManager.execute(PluginPhase.INIT);
    }

    public void onPostInit() {
        AnnotationUtil.getFilteredAnnotatedClasses(ConfigFile.class, modid).forEach(aClass -> {
            ConfigFile annotation = (ConfigFile) aClass.getAnnotation(ConfigFile.class);
            addConfig(AnnotationConfigManager.Type.of(annotation.type(), aClass).setName(annotation.value()));
        });
        ModConfigEvent.LOADING.register(c -> {
            if (c.getModId().equals(modid)){
                configManager.inject();
                this.modPluginManager.execute(PluginPhase.CONFIG_LOAD);
            }
        });
        ModConfigEvent.RELOADING.register(c -> {
            if (c.getModId().equals(modid)){
                configManager.inject();
                this.modPluginManager.execute(PluginPhase.CONFIG_RELOAD);
            }
        });
        this.modPluginManager.execute(PluginPhase.POST_INIT);
    }

    protected abstract void initModules();

    public RegistryHelper getRegistries() {
        return registryHelper;
    }
}

/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.block.tile;

import com.google.common.collect.Sets;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.component.energy.EnergyStorageComponent;
import com.hrznstudio.titanium.container.addon.IContainerAddon;
import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public abstract class PoweredTile<T extends PoweredTile<T>> extends ActiveTile<T> implements IEnergyTile {
    @Save
    private final EnergyStorageComponent<T> energyStorage;

    private boolean showEnergy = true;

    public PoweredTile(BasicTileBlock<T> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.energyStorage = this.createEnergyStorage();
        this.energyStorage.setComponentHarness(this.getSelf());
    }

    @Nonnull
    public EnergyStorageComponent<T> getEnergyStorage() {
        return energyStorage;
    }

    @Nonnull
    protected EnergyStorageComponent<T> createEnergyStorage() {
        return new EnergyStorageComponent<>(10000, 4, 10);
    }

    public Set<Direction> getValidEnergyFaces() {
        return Sets.newHashSet(Direction.values());
    }

    @Environment(EnvType.CLIENT)
    @Override
    @Nonnull
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> screenAddons = super.getScreenAddons();
        if (showEnergy) {
            screenAddons.addAll(this.getEnergyStorage().getScreenAddons());
        }
        return screenAddons;
    }

    @Override
    @Nonnull
    public List<IFactory<? extends IContainerAddon>> getContainerAddons() {
        List<IFactory<? extends IContainerAddon>> containerAddons = super.getContainerAddons();
        if (showEnergy) {
            containerAddons.addAll(this.getEnergyStorage().getContainerAddons());
        }
        return containerAddons;
    }

    @Override
    public EnergyStorage getEnergyStorage(@Nullable Direction side){
        return energyStorage;
    }

    public void setShowEnergy(boolean showEnergy) {
        this.showEnergy = showEnergy;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }
}

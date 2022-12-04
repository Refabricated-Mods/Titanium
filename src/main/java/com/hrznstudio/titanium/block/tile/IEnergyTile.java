package com.hrznstudio.titanium.block.tile;

import net.minecraft.core.Direction;
import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyTile {
    EnergyStorage getEnergyStorage(@Nullable Direction side);
}

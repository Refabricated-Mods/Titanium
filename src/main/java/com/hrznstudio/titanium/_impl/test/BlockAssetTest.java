/*
 * This file is part of Titanium
 * Copyright (C) 2019, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium._impl.test;

import com.hrznstudio.titanium._impl.test.tile.TileAssetTest;
import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.block.BlockRotation;
import net.minecraft.block.material.Material;

import javax.annotation.Nonnull;

public class BlockAssetTest extends BlockRotation<TileAssetTest> {
    public static BlockAssetTest TEST;

    public BlockAssetTest() {
        super("block_asset_test", Properties.create(Material.ROCK), TileAssetTest.class);
    }

    @Override
    public IFactory<TileAssetTest> getTileEntityFactory() {
        return TileAssetTest::new;
    }

    @Nonnull
    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

}

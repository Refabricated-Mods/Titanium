/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium._impl.test.tile;

import com.hrznstudio.titanium._impl.test.TwentyFourTestBlock;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.api.IItemStackQuery;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.client.screen.addon.WidgetScreenAddon;
import com.hrznstudio.titanium.component.inventory.InventoryComponent;
import com.hrznstudio.titanium.component.progress.ProgressBarComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.VolumeSlider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class TwentyFourTestTile extends PoweredTile<TwentyFourTestTile> {
    @Save
    private ProgressBarComponent<TwentyFourTestTile> bar;
    @Save
    private InventoryComponent<TwentyFourTestTile> first;
    @Save
    private InventoryComponent<TwentyFourTestTile> second;
    @Save
    private InventoryComponent<TwentyFourTestTile> third;

    public TwentyFourTestTile(BlockPos pos, BlockState state) {
        super((BasicTileBlock<TwentyFourTestTile>) TwentyFourTestBlock.TEST.getLeft(), TwentyFourTestBlock.TEST.getRight(), pos, state);
        this.addInventory(first = new InventoryComponent<TwentyFourTestTile>("test", 80, 20, 1)
            .setComponentHarness(this)
            .setInputFilter(IItemStackQuery.ANYTHING.toSlotFilter()));
        this.addInventory(second = new InventoryComponent<TwentyFourTestTile>("test2", 80, 40, 1)
            .setComponentHarness(this)
            .setInputFilter(IItemStackQuery.ANYTHING.toSlotFilter()));
        this.addProgressBar(bar = new ProgressBarComponent<TwentyFourTestTile>(110, 20, 500)
            .setCanIncrease(componentHarness -> true)
            .setOnFinishWork(() -> System.out.println("WOWOOW")));
        this.addInventory(third = new InventoryComponent<TwentyFourTestTile>("test3", 80, 60, 1)
            .setComponentHarness(this)
            .setInputFilter(IItemStackQuery.ANYTHING.toSlotFilter()));
    }

    @Environment(EnvType.CLIENT)
    private static IScreenAddon createScreen() {
        return new WidgetScreenAddon(30, -25, new EditBox(Minecraft.getInstance().font, 0, 0, 120, 20, new TextComponent("")));
    }

    @Environment(EnvType.CLIENT)
    private static IScreenAddon createScreen1() {
        return new WidgetScreenAddon(30, 185, new VolumeSlider(Minecraft.getInstance(), 0, 0, SoundSource.HOSTILE, 120));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void initClient() {
        super.initClient();
        this.addGuiAddonFactory(TwentyFourTestTile::createScreen1);
        this.addGuiAddonFactory(TwentyFourTestTile::createScreen);
    }

    @Override
    @ParametersAreNonnullByDefault
    public InteractionResult onActivated(Player player, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        openGui(player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, TwentyFourTestTile blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        Transaction transaction = Transaction.openOuter();
        this.getEnergyStorage().insert(10, transaction);
        transaction.commit();
        markForUpdate();
    }
/*
    @Nonnull
    @Override
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        List<IFactory<? extends IScreenAddon>> addons = super.getScreenAddons();
        for (IFactory<? extends IScreenAddon> addon : addons) {

            if (addon.create() instanceof EnergyBarScreenAddon) {
                addons.remove(addon);
                addons.add(() -> new EnergyBarScreenAddon(50, 20, this.getEnergyStorage()));
            }
        }
        return addons;
    }*/

    @Override
    @Nonnull
    public TwentyFourTestTile getSelf() {
        return this;
    }
}

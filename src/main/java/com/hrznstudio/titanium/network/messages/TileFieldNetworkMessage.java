/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.network.messages;

import com.hrznstudio.titanium.block.tile.BasicTile;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;


public class TileFieldNetworkMessage extends Message {

    private BlockPos pos;
    private CompoundTag data;

    public TileFieldNetworkMessage(BlockPos pos, CompoundTag data) {
        this.pos = pos;
        this.data = data;
    }

    public TileFieldNetworkMessage() {
    }

    @Override
    protected void handleClient(ServerPlayer sender) {
    }

    @Override
    protected void handleServer() {
        BlockEntity entity = Minecraft.getInstance().player.getCommandSenderWorld().getBlockEntity(pos);
        if (entity instanceof BasicTile){
            ((BasicTile<?>) entity).handleSyncObject(data);
        }
    }
}

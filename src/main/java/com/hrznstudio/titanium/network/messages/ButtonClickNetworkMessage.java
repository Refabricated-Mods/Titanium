/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.network.messages;

import com.hrznstudio.titanium.network.IButtonHandler;
import com.hrznstudio.titanium.network.Message;
import com.hrznstudio.titanium.network.locator.LocatorInstance;
import com.hrznstudio.titanium.util.CastingUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;


public class ButtonClickNetworkMessage extends Message {
    private LocatorInstance locatorInstance;
    private int id;
    private CompoundTag data;

    public ButtonClickNetworkMessage(LocatorInstance locatorInstance, int id, CompoundTag data) {
        this.locatorInstance = locatorInstance;
        this.id = id;
        this.data = data;
    }

    public ButtonClickNetworkMessage() {

    }

    @Override
    protected void handleClient(ServerPlayer sender) {
        Optional.ofNullable(sender)
                .flatMap(locatorInstance::locale)
                .flatMap(CastingUtil.attemptCast(IButtonHandler.class))
                .ifPresent(iButtonHandler -> iButtonHandler.handleButtonMessage(id, sender, data));
    }
}

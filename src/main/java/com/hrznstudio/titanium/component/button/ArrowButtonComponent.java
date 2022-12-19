/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.component.button;

import com.hrznstudio.titanium.api.IFactory;
import com.hrznstudio.titanium.api.client.IScreenAddon;
import com.hrznstudio.titanium.client.screen.addon.ArrowButtonScreenAddon;
import com.hrznstudio.titanium.util.FacingUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Collections;
import java.util.List;

public class ArrowButtonComponent extends ButtonComponent {

    public final FacingUtil.Sideness direction;

    public ArrowButtonComponent(int posX, int posY, int sizeX, int sizeY, FacingUtil.Sideness direction) {
        super(posX, posY, sizeX, sizeY);
        this.direction = direction;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<IFactory<? extends IScreenAddon>> getScreenAddons() {
        return Collections.singletonList(this::createScreen);
    }

    public FacingUtil.Sideness getDirection() {
        return direction;
    }

    @Environment(EnvType.CLIENT)
    private IScreenAddon createScreen() {
        return new ArrowButtonScreenAddon(this);
    }
}

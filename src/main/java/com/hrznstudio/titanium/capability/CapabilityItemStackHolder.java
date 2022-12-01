/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.capability;

import com.hrznstudio.titanium.api.capability.IStackHolder;

public class CapabilityItemStackHolder {


    public static final Capability<IStackHolder> ITEMSTACK_HOLDER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });


    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IStackHolder.class);
    }

}

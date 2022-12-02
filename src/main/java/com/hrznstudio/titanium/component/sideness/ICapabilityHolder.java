/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium.component.sideness;

import com.hrznstudio.titanium.util.FacingUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface ICapabilityHolder<T> {

    @Nonnull
    T getCapabilityForSide(@Nullable FacingUtil.Sideness sideness);

    boolean handleFacingChange(String handlerName, FacingUtil.Sideness facing, int mode);

    Collection<T> getLazyOptionals();
}

package com.hrznstudio.titanium.item;

import com.hrznstudio.titanium.capability.FluidHandlerScreenProviderItemStack;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public interface IFluidStorageItem {
    static Storage<FluidVariant> createFluidStorage(ContainerItemContext context, long capacity){
        return new FluidHandlerScreenProviderItemStack(context, capacity);
    }

    long getCapacity();
}

/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium;

import com.hrznstudio.titanium._impl.TagConfig;
import com.hrznstudio.titanium._impl.creative.CreativeFEGeneratorBlock;
import com.hrznstudio.titanium._impl.test.*;
import com.hrznstudio.titanium.block.tile.PoweredTile;
import com.hrznstudio.titanium.capability.CapabilityItemStackHolder;
import com.hrznstudio.titanium.command.RewardCommand;
import com.hrznstudio.titanium.command.RewardGrantCommand;
import com.hrznstudio.titanium.container.BasicAddonContainer;
import com.hrznstudio.titanium.module.ModuleController;
import com.hrznstudio.titanium.network.NetworkHandler;
import com.hrznstudio.titanium.network.locator.LocatorTypes;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.network.messages.TileFieldNetworkMessage;
import com.hrznstudio.titanium.recipe.condition.ContentExistsCondition;
import com.hrznstudio.titanium.recipe.shapelessenchant.ShapelessEnchantSerializer;
import com.hrznstudio.titanium.reward.Reward;
import com.hrznstudio.titanium.reward.RewardManager;
import com.hrznstudio.titanium.reward.RewardSyncMessage;
import com.hrznstudio.titanium.reward.storage.RewardWorldStorage;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;


public class Titanium extends ModuleController {

    public static final String MODID = "titanium";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static NetworkHandler NETWORK = new NetworkHandler(MODID);

    public Titanium() {
        super(MODID);
    }

    @Override
    public void onPreInit() {
        super.onPreInit();
        CapabilityItemStackHolder.init();
        NETWORK.registerMessage(ButtonClickNetworkMessage.class);
        NETWORK.registerMessage(RewardSyncMessage.class);
        NETWORK.registerMessage(TileFieldNetworkMessage.class);
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        //EventManager.forge(PlayerEvent.PlayerLoggedInEvent.class).process(this::onPlayerLoggedIn).subscribe();
        //EventManager.mod(RegisterCapabilitiesEvent.class).process(CapabilityItemStackHolder::register).subscribe();
        ResourceConditions.register(new ResourceLocation(Titanium.MODID, "content_exists"), ContentExistsCondition::test);
    }

    @Override
    public void onInit() {
        super.onInit();
        TagConfig.init();
        //TitaniumConfig.init();
    }

    @Override
    protected void initModules() {
        if (true) { //ENABLE IN DEV
            BasicAddonContainer.TYPE = new ExtendedScreenHandlerType<>(BasicAddonContainer::create);
            getRegistries().registerGeneric(Registry.MENU, "addon_container", () -> BasicAddonContainer.TYPE);
            getRegistries().registerGeneric(Registry.RECIPE_SERIALIZER, "shapeless_enchant", ShapelessEnchantSerializer::new);
            TestBlock.TEST = getRegistries().registerBlockWithTile("block_test", TestBlock::new);
            TwentyFourTestBlock.TEST = getRegistries().registerBlockWithTile("block_twenty_four_test", TwentyFourTestBlock::new);
            AssetTestBlock.TEST = getRegistries().registerBlockWithTile("block_asset_test", AssetTestBlock::new);
            MachineTestBlock.TEST = getRegistries().registerBlockWithTile("machine_test", MachineTestBlock::new);
            CreativeFEGeneratorBlock.INSTANCE = getRegistries().registerBlockWithTile("creative_generator", CreativeFEGeneratorBlock::new);
        }
        /*
        addModule(Module.builder("test_module")
                .disableByDefault()
                .description("Test module for titanium features")
                .feature(Feature.builder("blocks")
                        .description("Adds test titanium blocks")

                )
                .feature(Feature.builder("events")
                        .description("Adds test titanium events")
                        .event(EventManager.forge(EntityItemPickupEvent.class).filter(ev -> ev.getItem().getItem().getItem() == Items.STICK).process(ev -> ev.getItem().lifespan = 0).cancel())
                )
                .feature(Feature.builder("recipe")
                        .description("Testing of recipe stuff")
                        .content(RecipeSerializer.class, (RecipeSerializer) TestSerializableRecipe.SERIALIZER)
                        .event(EventManager.mod(FMLCommonSetupEvent.class).process(event -> Registry.register(Registry.RECIPE_TYPE, TestSerializableRecipe.SERIALIZER.getRegistryName(), TestSerializableRecipe.SERIALIZER.getRecipeType())))
                        .event(EventManager.forge(PlayerInteractEvent.LeftClickBlock.class)
                                .filter(leftClickBlock -> !leftClickBlock.getWorld().isClientSide && leftClickBlock.getPlayer() != null)
                                .process(leftClickBlock -> {
                                    Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = ObfuscationReflectionHelper.getPrivateValue(RecipeManager.class, leftClickBlock.getWorld().getRecipeManager(), "recipes");
                                    recipes.get(TestSerializableRecipe.SERIALIZER.getRecipeType()).values().stream()
                                            .map(iRecipe -> (TestSerializableRecipe) iRecipe)
                                            .filter(testSerializableRecipe -> testSerializableRecipe.isValid(leftClickBlock.getPlayer().getItemInHand(leftClickBlock.getHand()), leftClickBlock.getWorld().getBlockState(leftClickBlock.getPos()).getBlock()))
                                            .findFirst().ifPresent(testSerializableRecipe -> {
                                        leftClickBlock.getPlayer().getItemInHand(leftClickBlock.getHand()).shrink(1);
                                        ItemHandlerHelper.giveItemToPlayer(leftClickBlock.getPlayer(), testSerializableRecipe.getResultItem().copy());
                                        leftClickBlock.setCanceled(true);
                                    });
                                }))
                )
        );
        addModule(Module.builder("creative")
                .disableByDefault()
                .description("Creative features")
                .feature(Feature.builder("blocks")
                        .description("Adds creative machine features")
                        .content(Block.class, CreativeFEGeneratorBlock.INSTANCE)));
        */
    }

    @Override
    public void onPostInit() {
        super.onPostInit();
        RewardManager.get().getRewards().values().forEach(rewardGiver -> rewardGiver.getRewards().forEach(reward -> reward.register(EnvType.SERVER)));
        LocatorTypes.register();
    }
    private void onPlayerLoggedIn(Player player) {
        player.getServer().execute(() -> {
            RewardWorldStorage storage = RewardWorldStorage.get(player.getServer().getLevel(Level.OVERWORLD));
            if (!storage.getConfiguredPlayers().contains(player.getUUID())) {
                for (ResourceLocation collectRewardsResourceLocation : RewardManager.get().collectRewardsResourceLocations(player.getUUID())) {
                    Reward reward = RewardManager.get().getReward(collectRewardsResourceLocation);
                    storage.add(player.getUUID(), reward.getResourceLocation(), reward.getOptions()[0]);
                }
                storage.getConfiguredPlayers().add(player.getUUID());
                storage.setDirty();
            }
            CompoundTag nbt = storage.serializeSimple();
            player.getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> Titanium.NETWORK.get().sendToClient(new RewardSyncMessage(nbt), serverPlayerEntity));
        });
    }

    private void onServerStart(MinecraftServer server) {
        RewardCommand.register(server.getCommands().getDispatcher());
        RewardGrantCommand.register(server.getCommands().getDispatcher());
    }
}

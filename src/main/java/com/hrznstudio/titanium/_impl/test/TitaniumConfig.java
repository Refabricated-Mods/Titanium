/*
 * This file is part of Titanium
 * Copyright (C) 2022, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.hrznstudio.titanium._impl.test;

import com.hrznstudio.titanium._impl.TagConfig;
import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "titanium/titanium")
public class TitaniumConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    public static TitaniumConfig INSTANCE;

    @Comment(value = "A Boolean that is true by default")
    public boolean thisIsABoolean = true;
    @Comment(value = "A Boolean that is false by default")
    public boolean thisIsNotABoolean = false;
    public int intAngery = 7;
    @ConfigEntry.Gui.CollapsibleObject
    public Dabber dabber = new Dabber();

    public static class Dabber {

        public String dabby = "lil dab";

    }

    public static void init(){
        AutoConfig.register(TitaniumConfig.class, JanksonConfigSerializer::new);
        INSTANCE = AutoConfig.getConfigHolder(TitaniumConfig.class).getConfig();
    }
}

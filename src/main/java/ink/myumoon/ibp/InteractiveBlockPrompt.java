package ink.myumoon.ibp;

import ink.myumoon.ibp.config.ConfigCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.fml.common.Mod;

@Mod(InteractiveBlockPrompt.MODID)
public class InteractiveBlockPrompt {
    public static final String MODID = "ibp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public InteractiveBlockPrompt(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigCommon.SPEC);
    }
}

package ink.myumoon.ibp;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(InteractiveBlockPrompt.MODID)
public class InteractiveBlockPrompt {
    public static final String MODID = "ibp";
    private static final Logger LOGGER = LogUtils.getLogger();

    public InteractiveBlockPrompt() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}

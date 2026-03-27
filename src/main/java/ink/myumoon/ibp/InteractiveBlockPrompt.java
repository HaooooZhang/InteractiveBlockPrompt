package ink.myumoon.ibp;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(InteractiveBlockPrompt.MODID)
public class InteractiveBlockPrompt {
    public static final String MODID = "ibp";

    @SuppressWarnings("removal")
    public InteractiveBlockPrompt() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}

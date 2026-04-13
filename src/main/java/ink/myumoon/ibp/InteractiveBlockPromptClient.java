package ink.myumoon.ibp;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

@Mod(value = InteractiveBlockPrompt.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = InteractiveBlockPrompt.MODID, value = Dist.CLIENT)
public class InteractiveBlockPromptClient {
}

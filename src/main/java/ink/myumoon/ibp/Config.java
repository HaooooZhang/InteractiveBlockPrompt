package ink.myumoon.ibp;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = InteractiveBlockPrompt.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

	public static final ForgeConfigSpec SPEC;
	private static final ForgeConfigSpec.EnumValue<IconDirection> ICON_DIRECTION;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.push("ui");
		ICON_DIRECTION = builder
				.comment("Icon direction relative to the crosshair: UP, DOWN, LEFT, RIGHT")
				.defineEnum("iconDirection", IconDirection.RIGHT);
		builder.pop();

		SPEC = builder.build();
	}

	public static IconDirection getIconDirection() {
		return ICON_DIRECTION.get();
	}

}

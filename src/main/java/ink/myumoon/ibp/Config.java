package ink.myumoon.ibp;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = InteractiveBlockPrompt.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

	public static final ForgeConfigSpec SPEC;
	private static final ForgeConfigSpec.EnumValue<IconDirection> ICON_DIRECTION;
	private static final ForgeConfigSpec.BooleanValue ENABLE_JSON_INDICATOR_RULES;
	private static final ForgeConfigSpec.ConfigValue<String> JSON_INDICATOR_DIRECTORY;

	static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.push("ui");
		ICON_DIRECTION = builder
				.comment("Icon direction relative to the crosshair: UP, DOWN, LEFT, RIGHT")
				.defineEnum("iconDirection", IconDirection.RIGHT);
		builder.pop();

		builder.push("indicatorRules");
		ENABLE_JSON_INDICATOR_RULES = builder
				.comment("Enable indicator matching rules loaded from JSON files in the config directory")
				.define("enableJsonRules", true);
		JSON_INDICATOR_DIRECTORY = builder
				.comment("Directory under config/ where per-indicator json files are stored")
				.define("jsonDirectory", "ibp/indicators");
		builder.pop();

		SPEC = builder.build();
	}

	public static IconDirection getIconDirection() {
		return ICON_DIRECTION.get();
	}

	public static boolean isJsonIndicatorRulesEnabled() {
		return ENABLE_JSON_INDICATOR_RULES.get();
	}

	public static String getJsonIndicatorDirectory() {
		return JSON_INDICATOR_DIRECTORY.get();
	}

}



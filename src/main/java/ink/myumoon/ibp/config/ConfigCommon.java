package ink.myumoon.ibp.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigCommon {

    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.EnumValue<IconDirection> ICON_DIRECTION;
    private static final ModConfigSpec.BooleanValue ENABLE_JSON_INDICATOR_RULES;
    private static final ModConfigSpec.ConfigValue<String> JSON_INDICATOR_DIRECTORY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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

    public static boolean isEnableJsonIndicatorRules() {
        return ENABLE_JSON_INDICATOR_RULES.get();
    }

    public static String getJsonIndicatorDirectory() {
        return JSON_INDICATOR_DIRECTORY.get();
    }
}

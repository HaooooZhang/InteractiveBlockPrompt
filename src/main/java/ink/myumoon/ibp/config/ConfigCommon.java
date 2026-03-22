package ink.myumoon.ibp.config;

import ink.myumoon.ibp.IconDirection;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ConfigCommon {

    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.EnumValue<IconDirection> ICON_DIRECTION;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

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

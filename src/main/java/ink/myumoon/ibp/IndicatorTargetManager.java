package ink.myumoon.ibp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = InteractiveBlockPrompt.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class IndicatorTargetManager {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<String> INDICATOR_KEYS = List.of(
            "book", "button", "click", "interest", "notice", "search", "toggle", "wrench"
    );

    private static final String JSON_TEMPLATE = "{\n  \"rules\": []\n}\n";
    private static volatile Map<String, List<TargetRule>> indicatorRules = Collections.emptyMap();

    private IndicatorTargetManager() {
    }

    public static void loadFromDisk() {
        if (!Config.isJsonIndicatorRulesEnabled()) {
            indicatorRules = Collections.emptyMap();
            return;
        }

        Path root = FMLPaths.CONFIGDIR.get().resolve(Config.getJsonIndicatorDirectory()).normalize();
        ensureTemplates(root);

        Map<String, List<TargetRule>> loaded = new LinkedHashMap<>();
        for (String indicator : INDICATOR_KEYS) {
            loaded.put(indicator, loadRules(root.resolve(indicator + ".json")));
        }

        indicatorRules = Collections.unmodifiableMap(loaded);
    }

    public static boolean matches(String indicatorKey, BlockState blockState) {
        List<TargetRule> rules = indicatorRules.get(indicatorKey);
        if (rules == null || rules.isEmpty()) {
            return false;
        }

        for (TargetRule rule : rules) {
            if (rule.matches(blockState)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && event.getConfig().getSpec() == Config.SPEC) {
            loadFromDisk();
        }
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.COMMON && event.getConfig().getSpec() == Config.SPEC) {
            loadFromDisk();
        }
    }

    private static void ensureTemplates(Path root) {
        try {
            Files.createDirectories(root);
            for (String indicator : INDICATOR_KEYS) {
                Path file = root.resolve(indicator + ".json");
                if (!Files.exists(file)) {
                    Files.writeString(file, JSON_TEMPLATE, StandardCharsets.UTF_8);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to initialize indicator json directory: {}", root, e);
        }
    }

    private static List<TargetRule> loadRules(Path file) {
        if (!Files.exists(file)) {
            return List.of();
        }

        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            JsonElement rootElement = JsonParser.parseReader(reader);
            JsonArray ruleArray = extractRuleArray(rootElement);
            if (ruleArray == null) {
                LOGGER.warn("Ignoring indicator rule file {} because it has no array rules", file);
                return List.of();
            }

            List<TargetRule> rules = new ArrayList<>();
            for (JsonElement element : ruleArray) {
                if (!element.isJsonObject()) {
                    continue;
                }
                TargetRule rule = parseRule(file, element.getAsJsonObject());
                if (rule != null) {
                    rules.add(rule);
                }
            }
            return Collections.unmodifiableList(rules);
        } catch (Exception e) {
            LOGGER.error("Failed to read indicator rules from {}", file, e);
            return List.of();
        }
    }

    @Nullable
    private static JsonArray extractRuleArray(JsonElement rootElement) {
        if (rootElement == null || rootElement.isJsonNull()) {
            return null;
        }

        if (rootElement.isJsonArray()) {
            return rootElement.getAsJsonArray();
        }

        if (rootElement.isJsonObject()) {
            JsonObject object = rootElement.getAsJsonObject();
            JsonElement rules = object.get("rules");
            if (rules != null && rules.isJsonArray()) {
                return rules.getAsJsonArray();
            }
        }

        return null;
    }

    @Nullable
    private static TargetRule parseRule(Path file, JsonObject object) {
        ResourceLocation blockId = parseBlockId(file, object);
        TagKey<Block> blockTag = parseBlockTag(file, object);

        if (blockId == null && blockTag == null) {
            LOGGER.warn("Ignoring rule in {} because neither block nor block_tag is set", file);
            return null;
        }

        return new TargetRule(blockId, blockTag);
    }

    @Nullable
    private static ResourceLocation parseBlockId(Path file, JsonObject object) {
        JsonElement raw = object.get("block");
        if (raw == null || !raw.isJsonPrimitive()) {
            return null;
        }

        ResourceLocation id = ResourceLocation.tryParse(raw.getAsString());
        if (id == null || !ForgeRegistries.BLOCKS.containsKey(id)) {
            LOGGER.warn("Ignoring unknown block id {} in {}", raw.getAsString(), file);
            return null;
        }

        return id;
    }

    @Nullable
    private static TagKey<Block> parseBlockTag(Path file, JsonObject object) {
        JsonElement raw = object.get("block_tag");
        if (raw == null || !raw.isJsonPrimitive()) {
            return null;
        }

        ResourceLocation id = ResourceLocation.tryParse(raw.getAsString());
        if (id == null) {
            LOGGER.warn("Ignoring invalid block_tag {} in {}", raw.getAsString(), file);
            return null;
        }

        return BlockTags.create(id);
    }

    private record TargetRule(@Nullable ResourceLocation blockId, @Nullable TagKey<Block> blockTag) {

        private boolean matches(BlockState blockState) {
            if (blockId != null && !blockId.equals(ForgeRegistries.BLOCKS.getKey(blockState.getBlock()))) {
                return false;
            }

            return blockTag == null || blockState.is(blockTag);
        }
    }
}


package ink.myumoon.ibp;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import ink.myumoon.ibp.config.ConfigCommon;
import ink.myumoon.ibp.config.IconDirection;
import ink.myumoon.ibp.config.IndicatorTargetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = InteractiveBlockPrompt.MODID)
public class RenderEvent {

    private static final int ICON_SIZE = 12;
    private static final int ICON_OFFSET = 8;
    // Matches vanilla crosshair invert blend: ONE_MINUS_DST_COLOR / ONE_MINUS_SRC_COLOR.
    private static final RenderPipeline ICON_INVERT_PIPELINE = RenderPipelines.CROSSHAIR;

    private static final TagKey<Block> BOOK_TAG = create("book");
    private static final TagKey<Block> BUTTON_TAG = create("button");
    private static final TagKey<Block> CLICK_TAG = create("click");
    private static final TagKey<Block> INTEREST_TAG = create("interest");
    private static final TagKey<Block> NOTICE_TAG = create("notice");
    private static final TagKey<Block> SEARCH_TAG = create("search");
    private static final TagKey<Block> TOGGLE_TAG = create("toggle");
    private static final TagKey<Block> WRENCH_TAG = create("wrench");

    private static final Identifier BOOK_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/book.png");
    private static final Identifier BUTTON_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/button.png");
    private static final Identifier CLICK_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/click.png");
    private static final Identifier INTEREST_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/interest.png");
    private static final Identifier NOTICE_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/notice.png");
    private static final Identifier SEARCH_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/search.png");
    private static final Identifier TOGGLE_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/toggle.png");
    private static final Identifier WRENCH_ICON = Identifier.fromNamespaceAndPath("ibp","textures/gui/wrench.png");

    private static TagKey<Block> create(String create){
        return BlockTags.create(Identifier.fromNamespaceAndPath(InteractiveBlockPrompt.MODID, create));
    }

    @SubscribeEvent
    public static void onInteractiveBlock(RenderGuiEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        Options options = minecraft.options;

        boolean isFirstPerson = options.getCameraType().isFirstPerson();
        boolean isNotSpectator = minecraft.gameMode != null && minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR;
        boolean canRenderForSpectator = canRenderCrosshairForSpectator(minecraft.hitResult);
        boolean isHideGui = options.hideGui;


        if (isFirstPerson && (isNotSpectator || canRenderForSpectator) && !isHideGui) {
            //方块识别
            if (minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
                return;
            }

            BlockHitResult blockHitResult = (BlockHitResult) minecraft.hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();

            if (minecraft.level != null) {
                BlockState blockState = minecraft.level.getBlockState(blockPos);
                int screenWidth = event.getGuiGraphics().guiWidth();
                int screenHeight = event.getGuiGraphics().guiHeight();

                int[] iconPosition = getIconPosition(screenWidth, screenHeight, ConfigCommon.getIconDirection());
                int x = iconPosition[0];
                int y = iconPosition[1];

                if (IndicatorTargetManager.matches("book",blockState) || blockState.is(BOOK_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, BOOK_ICON);
                } else if (IndicatorTargetManager.matches("button",blockState) || blockState.is(BUTTON_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, BUTTON_ICON);
                } else if (IndicatorTargetManager.matches("click",blockState) || blockState.is(CLICK_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, CLICK_ICON);
                } else if (IndicatorTargetManager.matches("interest",blockState) || blockState.is(INTEREST_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, INTEREST_ICON);
                } else if (IndicatorTargetManager.matches("notice",blockState) || blockState.is(NOTICE_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, NOTICE_ICON);
                } else if (IndicatorTargetManager.matches("search",blockState) || blockState.is(SEARCH_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, SEARCH_ICON);
                } else if (IndicatorTargetManager.matches("toggle",blockState) || blockState.is(TOGGLE_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, TOGGLE_ICON);
                } else if (IndicatorTargetManager.matches("wrench",blockState) ||  blockState.is(WRENCH_TAG)) {
                    drawIcon(event.getGuiGraphics(), x, y, WRENCH_ICON);
                }
            }
        }
    }

    private static int[] getIconPosition(int screenWidth, int screenHeight, IconDirection direction){
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        return switch (direction) {
            case UP -> new int[] {centerX - ICON_SIZE / 2, centerY - ICON_OFFSET - ICON_SIZE};
            case DOWN -> new int[] {centerX - ICON_SIZE / 2, centerY + ICON_OFFSET};
            case LEFT -> new int[] {centerX - ICON_OFFSET - ICON_SIZE, centerY - ICON_SIZE / 2};
            case RIGHT -> new int[] {centerX + ICON_OFFSET, centerY - ICON_SIZE / 2};
        };
    }

    private static void drawIcon(GuiGraphicsExtractor guiGraphics, int x, int y, Identifier icon){
        guiGraphics.blit(
                ICON_INVERT_PIPELINE,
                icon,
                x,y,
                0,0,
                ICON_SIZE,ICON_SIZE,
                ICON_SIZE,ICON_SIZE
        );
    }

    //from Gui#canRenderCrosshairForSpectator
    private static boolean canRenderCrosshairForSpectator(@Nullable HitResult rayTrace) {
        if (rayTrace == null) {
            return false;
        } else if (rayTrace.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)rayTrace).getEntity() instanceof MenuProvider;
        } else if (rayTrace.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = ((BlockHitResult)rayTrace).getBlockPos();
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                return level.getBlockState(blockpos).getMenuProvider(level, blockpos) != null;
            }
        } else {
            return false;
        }
        return true;
    }
}

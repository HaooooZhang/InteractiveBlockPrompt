package ink.myumoon.ibp;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = "ibp")
public class RenderEvent {

    private static final int ICON_SIZE = 12;
    private static final int ICON_OFFSET = 10;

    private static final ResourceLocation INTERACTIVE_ICON = ResourceLocation.fromNamespaceAndPath("ibp","textures/gui/interactive_icon.png");
    private static final TagKey<Block> INTERACTIVE_TAG = create("interactive");
    private static TagKey<Block> create(String create){
        return  BlockTags.create(ResourceLocation.fromNamespaceAndPath(InteractiveBlockPrompt.MODID, create));
    }

    @SubscribeEvent
    public static void onInteractiveBlock(RenderGuiEvent.Pre event){
        Minecraft minecraft = Minecraft.getInstance();

        Options options = minecraft.options;

        boolean isFirstPerson = options.getCameraType().isFirstPerson();
        boolean isNotSpectator = minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR;
        boolean canRenderForSpectator = canRenderCrosshairForSpectator(minecraft.hitResult);

        if (isFirstPerson && (isNotSpectator || canRenderForSpectator)){
            //方块识别
            if (minecraft.hitResult == null || minecraft.hitResult.getType() != HitResult.Type.BLOCK){
                return;
            }

            BlockHitResult blockHitResult = (BlockHitResult) minecraft.hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();

            if (minecraft.level != null && minecraft.level.getBlockState(blockPos).is(INTERACTIVE_TAG)) {
                int screenWidth = event.getGuiGraphics().guiWidth();
                int screenHeight = event.getGuiGraphics().guiHeight();

                int x = screenWidth / 2 + ICON_OFFSET;
                int y = screenHeight / 2 - ICON_SIZE / 2;

                drawIcon(event.getGuiGraphics(), x, y);
            }
        }
    }

    private static void drawIcon(GuiGraphics guiGraphics,int x, int y){
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        guiGraphics.blit(
                INTERACTIVE_ICON,
                x,y,
                0,0,
                ICON_SIZE,ICON_SIZE,
                ICON_SIZE,ICON_SIZE
        );

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
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

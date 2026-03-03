package ink.myumoon.ibp;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

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

        // 恢复默认混合模式
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }
}

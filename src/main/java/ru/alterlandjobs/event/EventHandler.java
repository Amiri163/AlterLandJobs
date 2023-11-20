package ru.alterlandjobs.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;


@Mod.EventBusSubscriber
public class EventHandler {
    static boolean flag = true;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
     if(flag)
        renderLight(event.getMatrixStack(), event.getPartialTicks());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            int posX = 212;
            int posY = 70 - 1; // Проверяем блок под игроком
            int posZ = 222;

            if (player.getX() == 212 && player.getY() == posY && player.getZ() == 222) {
                player.sendMessage(new StringTextComponent("aboba"), Util.NIL_UUID);
                flag = false;
            }
        }
    }

    public static void renderLight(MatrixStack matrixStack, float particalTicks) {
        Minecraft mc = Minecraft.getInstance();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        Matrix4f matrix4f = matrixStack.last().pose();
        Vector3f color = new Vector3f(255, 0, 0); // Цвет линии (R, G, B)

        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

        double startX = 212; // Мировые координаты первой точки
        double startY = 40;
        double startZ = 222;

        double endY = 150; // Ваша вторая координата по оси Y

        PlayerEntity player = mc.player;
        double playerX = player.getX();
        double playerY = player.getY();
        double playerZ = player.getZ();

        // Интерполяция между предыдущей и текущей позицией игрока
        double interpolatedPlayerX = player.xo + (playerX - player.xo) * particalTicks;
        double interpolatedPlayerY = player.yo + (playerY - player.yo) * particalTicks;
        double interpolatedPlayerZ = player.zo + (playerZ - player.zo) * particalTicks;

        // Корректировка координат относительно интерполированной позиции игрока
        startX -= interpolatedPlayerX + 0.5;
        startY -= interpolatedPlayerY + 0.5;
        startZ -= interpolatedPlayerZ + 0.5;

        endY -= interpolatedPlayerY;


        bufferBuilder.vertex(matrix4f, (float) startX, (float) startY, (float) startZ).color((int) color.x(), (int) color.y(), (int) color.z(), 255).endVertex();
        bufferBuilder.vertex(matrix4f, (float) startX, (float) endY, (float) startZ).color((int) color.x(), (int) color.y(), (int) color.z(), 255).endVertex();

        tessellator.end();

        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        matrixStack.popPose();
    }
}
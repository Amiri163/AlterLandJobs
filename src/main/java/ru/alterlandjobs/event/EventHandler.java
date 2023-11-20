package ru.alterlandjobs.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;


@Mod.EventBusSubscriber
public class EventHandler {


    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        MatrixStack matrixStack = event.getMatrixStack();
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
        double interpolatedPlayerX = player.xo + (playerX - player.xo) * event.getPartialTicks();
        double interpolatedPlayerY = player.yo + (playerY - player.yo) * event.getPartialTicks();
        double interpolatedPlayerZ = player.zo + (playerZ - player.zo) * event.getPartialTicks();

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
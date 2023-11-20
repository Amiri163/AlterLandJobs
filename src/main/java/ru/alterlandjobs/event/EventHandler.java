package ru.alterlandjobs.event;



import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class EventHandler {

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        MatrixStack matrixStack = event.getMatrixStack();
        Matrix4f matrix4f = matrixStack.last().pose();
        Vector3f vec3f1 = new Vector3f(255.0F, 0, 0); // Цвет луча (R, G, B)

        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

        // Начальные координаты луча
        float startX = 212;
        float startY = 10;
        float startZ = 222;

        // Конечные координаты луча
        float endY = 256;

        // Добавление точек в буфер
        bufferBuilder.vertex(matrix4f, startX, startY, startZ).color(vec3f1.x(), vec3f1.y(), vec3f1.z(), 1.0F).endVertex();
        bufferBuilder.vertex(matrix4f, startX, endY, startZ).color(vec3f1.x(), vec3f1.y(), vec3f1.z(), 1.0F).endVertex();

        tessellator.end();

        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();

        matrixStack.popPose();
    }

}
package ru.alterlandjobs.event;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import ru.alterlandjobs.jobs.BusDriverAdmin;


@Mod.EventBusSubscriber
public class EventHandler {
    static boolean flag = false;

    static int startXF; // Мировые координаты первой точки
    static int startYF;
    static int startZF;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (startXF != 0 || startZF != 0) {
            if (!flag)
                renderLight(event.getMatrixStack(), event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;
            if (!BusDriverAdmin.points.isEmpty()) {
                for (String coordinates : BusDriverAdmin.points) {
                    String[] coords = coordinates.split(" ");
                    if (coords.length < 3) {
                        continue;
                    }
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    int z = Integer.parseInt(coords[2]);

                    startXF = x;
                    startYF = y;
                    startZF = z;


                }



            }

        }
    }

    public static void renderLight(MatrixStack matrixStack, float particalTicks) {
        double endY = 200; // Вторая координата по оси Y
        double startX = startXF;
        double startZ = startZF;

        double startY = 10;

        Minecraft mc = Minecraft.getInstance();
        ParticleManager particleManager = Minecraft.getInstance().particleEngine;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();
        Matrix4f matrix4f = matrixStack.last().pose();
        Vector3f color = new Vector3f(255, 0, 0); // Цвет линии (R, G, B)

        matrixStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();

        bufferBuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);

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
        Particle particle = particleManager.createParticle(ParticleTypes.EXPLOSION, startXF + 0.5,
                startYF + 0.5, startZF + 0.5, 5, 5, 5);
        particle.scale(0.2f); // Устанавливает размер партикла
        particleManager.add(particle);

        tessellator.end();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        matrixStack.popPose();
    }
}
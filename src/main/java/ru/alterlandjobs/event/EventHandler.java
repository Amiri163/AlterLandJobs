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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.alterlandjobs.jobs.BusDriverAdmin;

@Mod.EventBusSubscriber
public class EventHandler {
    public static boolean flag = false;
    public static boolean flag2 = true;

    static int startXF; // Мировые координаты первой точки
    static int startYF;
    static int startZF;
    static int i = 0;

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (startXF != 0 || startZF != 0) {
            if (!flag)
                renderLight(event.getMatrixStack(), event.getPartialTicks());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (flag2) {
            if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayerEntity) {
                if (!BusDriverAdmin.points.isEmpty()) {
                    ServerPlayerEntity player = (ServerPlayerEntity) event.player;

                    int playerX = MathHelper.floor(player.getX());
                    int playerY = MathHelper.floor(player.getY());
                    int playerZ = MathHelper.floor(player.getZ());

                    if (i < BusDriverAdmin.points.size()) {
                        String coordinates1 = BusDriverAdmin.points.get(i);
                        String[] coords2 = coordinates1.split(" ");

                        int x = Integer.parseInt(coords2[0]);
                        int y = Integer.parseInt(coords2[1]);
                        int z = Integer.parseInt(coords2[2]);
                        startXF = x;
                        startYF = y;
                        startZF = z;

                        if (playerX == startXF && playerY == startYF && playerZ == startZF) {
                            i++;
                        }
                    } else {
                        i = 0; // Сбрасываем i до нуля, чтобы начать массив заново
                    }
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

        matrixStack.popPose();
    }
}
package viw.xs.viewvanilla.client.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import viw.xs.viewvanilla.client.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {

    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    private void onDrawBlockOutline(
            MatrixStack matrices,
            VertexConsumer vertexConsumer,
            Entity entity,
            double camX,
            double camY,
            double camZ,
            BlockPos pos,
            BlockState state,
            int colorARGB,
            CallbackInfo ci
    ) {
        if (!ModConfig.blockOverlay) return;

        ci.cancel();

        VoxelShape shape = state.getOutlineShape(entity.getWorld(), pos);

        double offsetX = pos.getX() - camX;
        double offsetY = pos.getY() - camY;
        double offsetZ = pos.getZ() - camZ;

        shape.forEachEdge((x1, y1, z1, x2, y2, z2) -> {
            float dx = (float)(x2 - x1);
            float dy = (float)(y2 - y1);
            float dz = (float)(z2 - z1);
            float length = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);

            dx /= length;
            dy /= length;
            dz /= length;

            MatrixStack.Entry entry = matrices.peek();

            vertexConsumer
                    .vertex(entry.getPositionMatrix(),
                            (float)(x1 + offsetX),
                            (float)(y1 + offsetY),
                            (float)(z1 + offsetZ))
                    .color(ModConfig.overlayRed,
                            ModConfig.overlayGreen,
                            ModConfig.overlayBlue,
                            ModConfig.overlayAlpha)
                    .normal(entry, dx, dy, dz);

            vertexConsumer
                    .vertex(entry.getPositionMatrix(),
                            (float)(x2 + offsetX),
                            (float)(y2 + offsetY),
                            (float)(z2 + offsetZ))
                    .color(ModConfig.overlayRed,
                            ModConfig.overlayGreen,
                            ModConfig.overlayBlue,
                            ModConfig.overlayAlpha)
                    .normal(entry, dx, dy, dz);
        });
    }
}
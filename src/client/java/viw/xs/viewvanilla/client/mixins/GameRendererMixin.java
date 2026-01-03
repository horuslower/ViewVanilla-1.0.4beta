package viw.xs.viewvanilla.client.mixins;

import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import viw.xs.viewvanilla.client.ModConfig;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    private float viewDistance;

    @Inject(method = "getBasicProjectionMatrix", at = @At("RETURN"), cancellable = true)
    private void viewvanilla$modifyProjectionMatrix(float fovDegrees, CallbackInfoReturnable<Matrix4f> cir) {
        if (ModConfig.aspectRatio > 0.0f) {
            Matrix4f originalMatrix = cir.getReturnValue();
            if (originalMatrix != null) {
                Matrix4f matrix = new Matrix4f(originalMatrix);
                matrix.scale(1.0f / ModConfig.aspectRatio, 1.0f, 1.0f);
                cir.setReturnValue(matrix);
            }
        }
    }
}
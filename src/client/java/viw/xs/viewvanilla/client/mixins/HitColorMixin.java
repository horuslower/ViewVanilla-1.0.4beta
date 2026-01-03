package viw.xs.viewvanilla.client.mixins;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import viw.xs.viewvanilla.client.ModConfig;

@Mixin(LivingEntityRenderer.class)
public abstract class HitColorMixin {

    @ModifyArgs(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V"
            )
    )
    private void viewvanilla$modifyHitColor(Args args, LivingEntityRenderState state,
                                            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (!ModConfig.hitColor || !state.hurt) return;

        int color = args.get(4);

        int newA = Math.max(200, (int) (ModConfig.hitColorAlpha() * 255f));
        int newR = (int) (ModConfig.hitColorRed() * 255f);
        int newG = (int) (ModConfig.hitColorGreen() * 255f);
        int newB = (int) (ModConfig.hitColorBlue() * 255f);

        args.set(4, (newA << 24) | (newR << 16) | (newG << 8) | newB);
    }
}

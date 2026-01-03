package viw.xs.viewvanilla.client.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable; // Заменили здесь
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import viw.xs.viewvanilla.client.ModConfig;

@Mixin(MinecraftClient.class)
public class DistanceHitMixin {

    @Shadow @Nullable public HitResult crosshairTarget;
    @Shadow @Nullable public net.minecraft.client.network.ClientPlayerEntity player;

@Inject(method = "doAttack", at = @At("HEAD"))
    private void onAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.distanceHud || player == null) return;

        // Проверяем попадание именно по сущности
        if (this.crosshairTarget instanceof EntityHitResult hitResult) {
            // Дистанция от глаз игрока до точки контакта
            double dist = hitResult.getPos().distanceTo(player.getEyePos());
            ModConfig.lastHitDistance = String.format("%.1f", dist);
            ModConfig.distanceHitTime = 20; // Показывать дистанцию 20 тиков (1 секунда)
        }
    }
}

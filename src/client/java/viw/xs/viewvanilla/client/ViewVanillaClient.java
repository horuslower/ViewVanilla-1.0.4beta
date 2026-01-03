package viw.xs.viewvanilla.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.lwjgl.glfw.GLFW;

public class ViewVanillaClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModConfig.load();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.viewvanilla.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.viewvanilla.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Таймер DistanceHUD
            if (ModConfig.distanceHitTime > 0) {
                ModConfig.distanceHitTime--;
                if (ModConfig.distanceHitTime == 0) {
                    ModConfig.lastHitDistance = ""; // Очищаем после показа
                }
            }

            // Открытие GUI
            while (openGuiKey.wasPressed()) {
                client.setScreen(new FeaturesScreen());
            }

            // Логика AutoSprint
            if (ModConfig.autoSprint && client.player.forwardSpeed > 0 && !client.player.isSneaking() && !client.player.horizontalCollision) {
                client.player.setSprinting(true);
            }

            // ЛОГИКА FULLBRIGHT (Night Vision)
            if (ModConfig.fullBright) {
                // Выдаем эффект бесконечно (на 520 тиков / 26 секунд, обновляется каждый тик)
                // Параметры: эффект, длительность, уровень, без частиц, без иконки
                client.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 520, 0, false, false, false));
            } else {
                // Если выключили в меню, а эффект остался от мода — убираем его
                if (client.player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                    StatusEffectInstance effect = client.player.getStatusEffect(StatusEffects.NIGHT_VISION);
                    // Проверяем, что эффект "наш" (длительность около 500), чтобы не снять обычное зелье игрока
                    if (effect != null && effect.getDuration() <= 520) {
                        client.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                    }
                }
            }
        });

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.options.hudHidden) return;

            int cX = mc.getWindow().getScaledWidth() / 2;
            int cY = mc.getWindow().getScaledHeight() / 2;

            // HUD TotemCounter
            if (ModConfig.totemCounter) {
                int count = 0;
                for (int i = 0; i < mc.player.getInventory().size(); i++) {
                    if (mc.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) count += mc.player.getInventory().getStack(i).getCount();
                }
                if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) count += mc.player.getOffHandStack().getCount();

                if (count > 0) {
                    int tx = cX + ModConfig.totemX;
                    int ty = cY + ModConfig.totemY;
                    drawContext.drawItem(new ItemStack(Items.TOTEM_OF_UNDYING), tx, ty);
                    drawContext.drawText(mc.textRenderer, String.valueOf(count), tx + 17, ty + 9, 0xFFFFFFFF, true);
                }
            }

            // HUD ItemViewer
            if (ModConfig.itemViewer) {
                ItemStack hand = mc.player.getMainHandStack();
                if (!hand.isEmpty()) {
                    int ix = cX + ModConfig.itemX;
                    int iy = cY + ModConfig.itemY;
                    drawContext.drawItem(hand, ix, iy);
                    if (hand.getCount() > 1) {
                        drawContext.drawText(mc.textRenderer, String.valueOf(hand.getCount()), ix + 17, iy + 9, 0xFFFFFFFF, true);
                    }
                }
            }

            // HUD ArmorStatus
            ArmorStatusRenderer.render(drawContext, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
            
            // HUD DistanceHit
            DistanceHudRenderer.render(drawContext, mc.getWindow().getScaledWidth(), mc.getWindow().getScaledHeight());
        });
    }
}
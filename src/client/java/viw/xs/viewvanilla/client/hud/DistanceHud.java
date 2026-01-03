package viw.xs.viewvanilla.client.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import viw.xs.viewvanilla.client.ModConfig;

public class DistanceHud {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void render(DrawContext context, TextRenderer textRenderer) {
        if (!ModConfig.distanceHud || client.player == null) return;
        
        String lastHit = ModConfig.lastHitDistance;
        if (lastHit == null || lastHit.isEmpty()) return;

        int x = ModConfig.distanceHudX;
        int y = ModConfig.distanceHudY;
        
        // Форматируем текст
        String text = "Hit Distance: " + lastHit + "m";
        
        // Рисуем фон
        int textWidth = textRenderer.getWidth(text);
        int backgroundColor = 0x80000000; // Полупрозрачный черный
        
        context.fill(x - 2, y - 2, x + textWidth + 2, y + 10, backgroundColor);
        
        // Рисуем текст
        context.drawText(textRenderer, text, x, y, 0xFFFFFF, true);
    }
}
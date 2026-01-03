package viw.xs.viewvanilla.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class DistanceHudRenderer {

    public static void render(DrawContext context, int screenWidth, int screenHeight) {
        if (!ModConfig.distanceHud) return;

        MinecraftClient client = MinecraftClient.getInstance();

        // Получаем координаты из конфига
        int x = (screenWidth / 2) + ModConfig.distanceHudX;
        int y = (screenHeight / 2) + ModConfig.distanceHudY;

// Формируем строку: эмодзи + число
        String text = "🗡️" + ModConfig.lastHitDistance;
        int textWidth = client.textRenderer.getWidth(text);

        // Отрисовка текста (x - textWidth / 2 обеспечивает центрирование по горизонтали)
        context.drawTextWithShadow(client.textRenderer, text, x - (textWidth / 2), y, ModConfig.distanceHudColor);
    }
}
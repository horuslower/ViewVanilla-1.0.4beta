package viw.xs.viewvanilla.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ModConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "viewvanilla.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // --- Render ---
    public static boolean fullBright = false;
    public static boolean blockOverlay = false;
    public static float aspectRatio = 0.0f;
    public static float overlayRed = 1.0f, overlayGreen = 1.0f, overlayBlue = 1.0f, overlayAlpha = 0.4f, lineWidth = 2.0f;

    // HitColor
    public static boolean hitColor = true;
    public static int hitColorValue = 0xFFFF0000; // RGB
    public static float hitColorAlpha = 0.6f;     // Отдельная Alpha

    // --- Utilities ---
    public static boolean autoSprint = false;
    public static boolean totemCounter = false;
    public static boolean itemViewer = false;
    public static int closeKey = 256;

    // --- HUD ---
    public static boolean armorStatus = true;
    public static boolean distanceHud = false; // Новый модуль

    // Координаты HUD элементов
    public static int totemX = 0, totemY = 0;
    public static int itemX = 0, itemY = 0;
    public static int armorStatusX = -115, armorStatusY = 0;
    public static int distanceHudX = 0, distanceHudY = 0; // Координаты DistanceHUD

    // Theme
    public static int themeColor = 0xFF33CCCC;

// Runtime variables (не сохраняются, но нужны для работы)
    public static String lastHitDistance = "0.00";
    public static int distanceHitTime = 0; // Таймер показа дистанции
    public static int distanceHudColor = 0xFFFF5555; // Цвет текста DistanceHUD (красный по умолчанию)

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new ConfigData(), writer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void load() {
        if (!CONFIG_FILE.exists()) { save(); return; }
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                fullBright = data.fullBright; blockOverlay = data.blockOverlay; aspectRatio = data.aspectRatio;
                overlayRed = data.overlayRed; overlayGreen = data.overlayGreen; overlayBlue = data.overlayBlue;
                overlayAlpha = data.overlayAlpha; lineWidth = data.lineWidth;

                hitColor = data.hitColor; hitColorValue = data.hitColorValue; hitColorAlpha = data.hitColorAlpha;

                autoSprint = data.autoSprint; totemCounter = data.totemCounter; itemViewer = data.itemViewer;
                closeKey = data.closeKey;

                armorStatus = data.armorStatus; distanceHud = data.distanceHud;

                totemX = data.totemX; totemY = data.totemY;
                itemX = data.itemX; itemY = data.itemY;
                armorStatusX = data.armorStatusX; armorStatusY = data.armorStatusY;
                distanceHudX = data.distanceHudX; distanceHudY = data.distanceHudY;

                themeColor = data.themeColor;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void applyGamma() {
        MinecraftClient.getInstance().options.getGamma().setValue(fullBright ? 16.0 : 1.0);
    }

    // Методы для Mixin (HitColor)
    public static float hitColorRed() { return ((hitColorValue >> 16) & 0xFF) / 255.0f; }
    public static float hitColorGreen() { return ((hitColorValue >> 8) & 0xFF) / 255.0f; }
    public static float hitColorBlue() { return (hitColorValue & 0xFF) / 255.0f; }
    public static float hitColorAlpha() { return hitColorAlpha; }

    private static class ConfigData {
        boolean fullBright = ModConfig.fullBright, blockOverlay = ModConfig.blockOverlay, autoSprint = ModConfig.autoSprint;
        boolean totemCounter = ModConfig.totemCounter, itemViewer = ModConfig.itemViewer, armorStatus = ModConfig.armorStatus;
        boolean distanceHud = ModConfig.distanceHud; // Save functionality

        float aspectRatio = ModConfig.aspectRatio, overlayRed = ModConfig.overlayRed, overlayGreen = ModConfig.overlayGreen;
        float overlayBlue = ModConfig.overlayBlue, overlayAlpha = ModConfig.overlayAlpha, lineWidth = ModConfig.lineWidth;

        boolean hitColor = ModConfig.hitColor;
        int hitColorValue = ModConfig.hitColorValue;
        float hitColorAlpha = ModConfig.hitColorAlpha;

        int closeKey = ModConfig.closeKey;
        int totemX = ModConfig.totemX, totemY = ModConfig.totemY;
        int itemX = ModConfig.itemX, itemY = ModConfig.itemY;
        int armorStatusX = ModConfig.armorStatusX, armorStatusY = ModConfig.armorStatusY;
        int distanceHudX = ModConfig.distanceHudX, distanceHudY = ModConfig.distanceHudY; // Save positions

        int themeColor = ModConfig.themeColor;
    }
}
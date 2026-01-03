package viw.xs.viewvanilla.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class FeaturesScreen extends Screen {
    private final int bgW = 500, bgH = 320, sideBarW = 130, themeW = 140, themeGap = 15;
    private String currentTab = "Render", settingsTarget = null;

    // Флаги перетаскивания
    private boolean draggingTotem = false;
    private boolean draggingItem = false;
    private boolean draggingArmor = false;
    private boolean draggingDistance = false; // Новый флаг

    private boolean isBindingClose = false;

    private final int[] themeColors = {
            0xFF33CCCC, 0xFFFFC440, 0xFF39E24D, 0xFF9A001E, 0xFFE0B0FF,
            0xFF00FFCC, 0xFFFF007F, 0xFF50C878, 0xFFF4BB44, 0xFF7DF9FF
    };
    private final String[] themeNames = {
            "Aquamarine", "Gold", "Emerald", "Cherry", "Sakura",
            "Neon", "Pinky", "Vivid", "Mango", "Electric"
    };
    private final int DEFAULT_COLOR = 0xFFFFFFFF;

    public FeaturesScreen() { super(Text.of("Features")); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Логика перетаскивания элементов
        if (draggingTotem) {
            ModConfig.totemX = mouseX - (this.width / 2);
            ModConfig.totemY = mouseY - (this.height / 2);
            return;
        }
        if (draggingItem) {
            ModConfig.itemX = mouseX - (this.width / 2);
            ModConfig.itemY = mouseY - (this.height / 2);
            return;
        }
        if (draggingArmor) {
            ModConfig.armorStatusX = mouseX - (this.width / 2);
            ModConfig.armorStatusY = mouseY - (this.height - 62);
            ArmorStatusRenderer.render(context, this.width, this.height);
            return;
        }
        if (draggingDistance) { // Перетаскивание DistanceHUD
            ModConfig.distanceHudX = mouseX - (this.width / 2);
            ModConfig.distanceHudY = mouseY - (this.height / 2);
            DistanceHudRenderer.render(context, this.width, this.height);
            return;
        }

        // Отрисовка фона меню
        context.fill(0, 0, this.width, this.height, 0x55000000);
        int x = this.width / 2 - bgW / 2, y = this.height / 2 - bgH / 2;

        drawRoundedRect(context, x, y, bgW, bgH, 12, 0x990A0A0A);
        drawRoundedRectLeft(context, x, y, sideBarW, bgH, 12, 0x33000000);

        context.fill(x + sideBarW, y + 12, x + sideBarW + 1, y + bgH - 12, 0x22FFFFFF);
        context.fill(x + sideBarW + 10, y + 40, x + bgW - 10, y + 41, 0x22FFFFFF);

        context.drawText(this.textRenderer, "§lVIEW VANILLA", x + 12, y + 15, ModConfig.themeColor, false);
        context.drawText(this.textRenderer, "§7v1.0.6 Beta", x + 12, y + 26, 0xFF777777, false);

        int tabsX = x + sideBarW + 20;
        drawTab(context, "Render", tabsX, y + 10);
        drawTab(context, "Utilities", tabsX + 75, y + 10);
        drawTab(context, "HUD", tabsX + 150, y + 10);

        int rx = x + sideBarW + 10, ry = y + 55;

        // Отрисовка модулей во вкладках
        if (currentTab.equals("Render")) {
            drawModuleRow(context, "FullBright", ModConfig.fullBright, rx, ry, "");
            drawModuleRow(context, "BlockOverlay", ModConfig.blockOverlay, rx, ry + 35, "RGBA");
            drawModuleRow(context, "AspectRatio", ModConfig.aspectRatio > 0, rx, ry + 70, ModConfig.aspectRatio > 0 ? String.format("%.2f", ModConfig.aspectRatio) : "Off");
            drawModuleRow(context, "HitColor", ModConfig.hitColor, rx, ry + 105, "RGB");
        } else if (currentTab.equals("Utilities")) {
            drawModuleRow(context, "AutoSprint", ModConfig.autoSprint, rx, ry, "On");
            drawModuleRow(context, "TotemCounter", ModConfig.totemCounter, rx, ry + 35, "Pos");
            drawModuleRow(context, "ItemViewer", ModConfig.itemViewer, rx, ry + 70, "Pos");
            drawModuleRow(context, "PreBindESC", true, rx, ry + 105, getCloseKeyName());
        } else if (currentTab.equals("HUD")) {
            drawModuleRow(context, "ArmorStatus", ModConfig.armorStatus, rx, ry, "Pos");
            drawModuleRow(context, "DistanceHUD", ModConfig.distanceHud, rx, ry + 35, "Pos"); // Кнопка DistanceHUD
        }

        // Отрисовка панели тем
        int tX = x + bgW + themeGap;
        drawRoundedRect(context, tX, y, themeW, bgH, 12, 0xCC0A0A0A);
        context.drawText(this.textRenderer, "THEME", tX + 12, y + 15, 0xFFFFFFFF, false);
        context.fill(tX + 10, y + 40, tX + themeW - 10, y + 41, 0x22FFFFFF);

        for (int i = 0; i < themeColors.length; i++) {
            drawThemeToggle(context, themeNames[i], tX + 10, y + 45 + (i * 26), ModConfig.themeColor == themeColors[i], themeColors[i]);
        }

// Отрисовка всплывающих настроек
        if (settingsTarget != null) {
            if (settingsTarget.equals("BlockOverlay")) drawSettingsMenu(context, mouseX, mouseY);
            else if (settingsTarget.equals("PreBindESC")) drawBindSettings(context, mouseX, mouseY);
            else if (settingsTarget.equals("AspectRatio")) drawAspectSettings(context, mouseX, mouseY);
            else if (settingsTarget.equals("HitColor")) drawHitColorSettings(context, mouseX, mouseY);
            else if (settingsTarget.equals("DistanceHUD")) drawDistanceHudSettings(context, mouseX, mouseY);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawHitColorSettings(DrawContext context, int mouseX, int mouseY) {
        int mW = 200, mH = 200, mX = this.width / 2 - mW / 2, mY = this.height / 2 - mH / 2;
        drawRoundedRect(context, mX, mY, mW, mH, 10, 0xF0050505);
        context.drawText(this.textRenderer, "Hit Color Settings", mX + 15, mY + 15, ModConfig.themeColor, false);

        // Предпросмотр текущего цвета
        int pX = mX + 160, pY = mY + 12;
        drawRoundedRect(context, pX, pY, 25, 14, 4, 0xFFFFFFFF);
        drawRoundedRect(context, pX + 1, pY + 1, 23, 12, 3, (0xFF << 24) | ModConfig.hitColorValue);

        String[] labels = {"Red", "Green", "Blue", "Alpha", "Brightness"};
        float r = ((ModConfig.hitColorValue >> 16) & 0xFF) / 255f;
        float g = ((ModConfig.hitColorValue >> 8) & 0xFF) / 255f;
        float b = (ModConfig.hitColorValue & 0xFF) / 255f;

        float[] values = {r, g, b, ModConfig.hitColorAlpha, 1.0f};
        for (int i = 0; i < 5; i++) {
            int sy = mY + 45 + (i * 25);
            context.drawText(this.textRenderer, labels[i], mX + 15, sy, 0xFFAAAAAA, false);
            drawSlider(context, mX + 20, sy + 12, 160, values[i]);
        }
        context.drawText(this.textRenderer, "§7Tip: Red + Green = Yellow", mX + 20, mY + 175, 0xFF666666, false);
    }

    private void drawBindSettings(DrawContext context, int mouseX, int mouseY) {
        int mW = 200, mH = 100, mX = this.width / 2 - mW / 2, mY = this.height / 2 - mH / 2;
        drawRoundedRect(context, mX, mY, mW, mH, 10, 0xF0050505);
        context.drawText(this.textRenderer, "PreBindESC Settings", mX + 15, mY + 15, ModConfig.themeColor, false);
        String status = isBindingClose ? "§bPRESS ANY KEY..." : "Current: §f" + getCloseKeyName();
        context.drawText(this.textRenderer, status, mX + 20, mY + 45, 0xFFAAAAAA, false);
        int btnY = mY + 65;
        drawRoundedRect(context, mX + 20, btnY, 160, 20, 5, isBindingClose ? 0x44FFFFFF : 0x22FFFFFF);
        context.drawText(this.textRenderer, "Click to Change", mX + 58, btnY + 6, 0xFFFFFFFF, false);
    }

private void drawAspectSettings(DrawContext context, int mouseX, int mouseY) {
        int mW = 200, mH = 85, mX = this.width / 2 - mW / 2, mY = this.height / 2 - mH / 2;
        drawRoundedRect(context, mX, mY, mW, mH, 10, 0xF0050505);
        context.drawText(this.textRenderer, "Aspect Ratio Stretch", mX + 15, mY + 15, ModConfig.themeColor, false);
        context.drawText(this.textRenderer, "Value: " + String.format("%.2f", ModConfig.aspectRatio), mX + 15, mY + 40, 0xFFAAAAAA, false);
        drawSlider(context, mX + 20, mY + 58, 160, ModConfig.aspectRatio);
    }

    private void drawDistanceHudSettings(DrawContext context, int mouseX, int mouseY) {
        int mW = 220, mH = 180, mX = this.width / 2 - mW / 2, mY = this.height / 2 - mH / 2;
        drawRoundedRect(context, mX, mY, mW, mH, 10, 0xF0050505);
        context.drawText(this.textRenderer, "Distance HUD Settings", mX + 15, mY + 15, ModConfig.themeColor, false);

        // Предпросмотр цвета
        String preview = "🗡️" + ModConfig.lastHitDistance;
        context.drawText(this.textRenderer, "Preview:", mX + 15, mY + 45, 0xFFAAAAAA, false);
        context.drawTextWithShadow(this.textRenderer, preview, mX + 15, mY + 60, ModConfig.distanceHudColor);

        // Кнопки выбора цвета
        int[] colors = {0xFFFF5555, 0xFF55FF55, 0xFF5555FF, 0xFFFFFF55, 0xFFFFFF55, 0xFFFFD700};
        String[] names = {"Red", "Green", "Blue", "Cyan", "Yellow", "Gold"};

        for (int i = 0; i < colors.length; i++) {
            int btnX = mX + 15 + (i % 3) * 70;
            int btnY = mY + 90 + (i / 3) * 35;
            boolean selected = ModConfig.distanceHudColor == colors[i];

            drawRoundedRect(context, btnX, btnY, 60, 25, 4, selected ? colors[i] : 0x22FFFFFF);
            drawRoundedRect(context, btnX + 1, btnY + 1, 58, 23, 3, selected ? colors[i] : 0x11FFFFFF);
            context.drawText(this.textRenderer, names[i], btnX + 30 - this.textRenderer.getWidth(names[i]) / 2, btnY + 8, selected ? 0xFF000000 : 0xFFAAAAAA, false);

            if (selected) {
                drawRoundedRect(context, btnX - 2, btnY - 2, 64, 29, 4, 0x44FFFFFF);
            }
        }

        // Инфо
        context.drawText(this.textRenderer, "§7Right click DistanceHUD to open", mX + 15, mY + mH - 25, 0xFFAAAAAA, false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isBindingClose) {
            ModConfig.closeKey = (keyCode == GLFW.GLFW_KEY_ESCAPE) ? 256 : keyCode;
            isBindingClose = false; ModConfig.save(); return true;
        }
        if (keyCode == ModConfig.closeKey || keyCode == GLFW.GLFW_KEY_ESCAPE) { this.close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
if (settingsTarget != null) {
            int mW = 200, mH = (settingsTarget.equals("BlockOverlay") || settingsTarget.equals("HitColor")) ? 200 :
                          (settingsTarget.equals("PreBindESC") ? 100 :
                          (settingsTarget.equals("DistanceHUD") ? 220 : 85));
            int mX = this.width / 2 - mW / 2, mY = this.height / 2 - mH / 2;
if (settingsTarget.equals("PreBindESC") && isHovering(mouseX, mouseY, mX + 20, mY + 65, 160, 20)) { isBindingClose = true; return true; }
            if (settingsTarget.equals("DistanceHUD")) {
                // Кнопки выбора цвета
                int[] colors = {0xFFFF5555, 0xFF55FF55, 0xFF5555FF, 0xFFFFFF55, 0xFFFFFF55, 0xFFFFD700};
                for (int i = 0; i < colors.length; i++) {
                    int btnX = mX + 15 + (i % 3) * 70;
                    int btnY = mY + 90 + (i / 3) * 35;
                    if (isHovering(mouseX, mouseY, btnX, btnY, 60, 25)) {
                        ModConfig.distanceHudColor = colors[i];
                        ModConfig.save();
                        return true;
                    }
                }
            }
            if (!isHovering(mouseX, mouseY, mX, mY, mW, mH)) { settingsTarget = null; return true; }
            return true;
        }

        if (button == 0) {
            int centerX = this.width / 2, centerY = this.height / 2;
            if (ModConfig.totemCounter && isHovering(mouseX, mouseY, centerX + ModConfig.totemX - 16, centerY + ModConfig.totemY - 16, 32, 32)) {
                draggingTotem = true;
                return true;
            }
            if (ModConfig.itemViewer && isHovering(mouseX, mouseY, centerX + ModConfig.itemX - 30, centerY + ModConfig.itemY - 10, 60, 20)) {
                draggingItem = true;
                return true;
            }
            if (ModConfig.armorStatus && isHovering(mouseX, mouseY, centerX + ModConfig.armorStatusX, (this.height - 62) + ModConfig.armorStatusY, 40, 40)) {
                draggingArmor = true;
                return true;
            }
            // Проверка клика для DistanceHUD
            // Зона клика теперь меньше, так как нет иконки (ширина ~40 пикселей)
if (ModConfig.distanceHud && isHovering(mouseX, mouseY, centerX + ModConfig.distanceHudX - 20, centerY + ModConfig.distanceHudY, 40, 12)) {
                if (button == 1) {
                    settingsTarget = "DistanceHUD"; // ПКМ - открыть меню настроек
                } else {
                    draggingDistance = true; // ЛКМ - перетащить
                }
                return true;
            }
        }

        int x = this.width / 2 - bgW / 2, y = this.height / 2 - bgH / 2;
        int tabsX = x + sideBarW + 20;
        if (isHovering(mouseX, mouseY, tabsX, y + 10, 65, 20)) { currentTab = "Render"; return true; }
        if (isHovering(mouseX, mouseY, tabsX + 75, y + 10, 75, 20)) { currentTab = "Utilities"; return true; }
        if (isHovering(mouseX, mouseY, tabsX + 150, y + 10, 50, 20)) { currentTab = "HUD"; return true; }

        int rx = x + sideBarW + 10, ry = y + 55, rw = bgW - sideBarW - 20;
        if (currentTab.equals("Render")) {
            if (isHovering(mouseX, mouseY, rx, ry, rw, 30)) { ModConfig.fullBright = !ModConfig.fullBright; ModConfig.applyGamma(); }
            if (isHovering(mouseX, mouseY, rx, ry + 35, rw, 30)) { if (button == 1) settingsTarget = "BlockOverlay"; else ModConfig.blockOverlay = !ModConfig.blockOverlay; }
            if (isHovering(mouseX, mouseY, rx, ry + 70, rw, 30)) { if (button == 1) settingsTarget = "AspectRatio"; else ModConfig.aspectRatio = (ModConfig.aspectRatio > 0 ? 0.0f : 0.3f); }
            if (isHovering(mouseX, mouseY, rx, ry + 105, rw, 30)) { if (button == 1) settingsTarget = "HitColor"; else ModConfig.hitColor = !ModConfig.hitColor; }
        } else if (currentTab.equals("Utilities")) {
            if (isHovering(mouseX, mouseY, rx, ry, rw, 30)) ModConfig.autoSprint = !ModConfig.autoSprint;
            if (isHovering(mouseX, mouseY, rx, ry + 35, rw, 30)) ModConfig.totemCounter = !ModConfig.totemCounter;
            if (isHovering(mouseX, mouseY, rx, ry + 70, rw, 30)) ModConfig.itemViewer = !ModConfig.itemViewer;
            if (isHovering(mouseX, mouseY, rx, ry + 105, rw, 30) && button == 1) settingsTarget = "PreBindESC";
        } else if (currentTab.equals("HUD")) {
            if (isHovering(mouseX, mouseY, rx, ry, rw, 30)) ModConfig.armorStatus = !ModConfig.armorStatus;
            if (isHovering(mouseX, mouseY, rx, ry + 35, rw, 30)) ModConfig.distanceHud = !ModConfig.distanceHud; // Клик по DistanceHUD
        }

        int tX = x + bgW + themeGap;
        for (int i = 0; i < themeColors.length; i++) {
            if (isHovering(mouseX, mouseY, tX + 10, y + 45 + (i * 26), themeW - 20, 25)) {
                ModConfig.themeColor = (ModConfig.themeColor == themeColors[i]) ? DEFAULT_COLOR : themeColors[i];
                ModConfig.save(); return true;
            }
        }
        ModConfig.save(); return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (settingsTarget != null) {
            int mW = 200, mX = this.width / 2 - mW / 2, sX = mX + 20, sW = 160;
            if (settingsTarget.equals("BlockOverlay") && mouseY >= this.height / 2 - 50 && mouseY <= this.height / 2 + 75) {
                float val = MathHelper.clamp((float) (mouseX - sX) / sW, 0, 1);
                int row = (int) ((mouseY - (this.height / 2 - 40)) / 25);
                if (row == 0) ModConfig.overlayRed = val; else if (row == 1) ModConfig.overlayGreen = val; else if (row == 2) ModConfig.overlayBlue = val; else if (row == 3) ModConfig.overlayAlpha = val; else if (row == 4) ModConfig.lineWidth = val * 5.0f;
                ModConfig.save(); return true;
            }
            if (settingsTarget.equals("HitColor") && mouseY >= this.height / 2 - 50 && mouseY <= this.height / 2 + 75) {
                float val = MathHelper.clamp((float) (mouseX - sX) / sW, 0, 1);
                int row = (int) ((mouseY - (this.height / 2 - 40)) / 25);
                int r = (ModConfig.hitColorValue >> 16) & 0xFF, g = (ModConfig.hitColorValue >> 8) & 0xFF, b = ModConfig.hitColorValue & 0xFF;
                if (row == 0) r = (int)(val * 255); else if (row == 1) g = (int)(val * 255); else if (row == 2) b = (int)(val * 255); else if (row == 3) ModConfig.hitColorAlpha = val;
                ModConfig.hitColorValue = (r << 16) | (g << 8) | b;
                ModConfig.save(); return true;
            }
            if (settingsTarget.equals("AspectRatio") && isHovering(mouseX, mouseY, sX, this.height / 2 + 10, sW, 15)) {
                ModConfig.aspectRatio = MathHelper.clamp((float) (mouseX - sX) / sW, 0.0f, 1.0f);
                ModConfig.save(); return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingTotem = false; draggingItem = false; draggingArmor = false; draggingDistance = false;
        ModConfig.save(); return super.mouseReleased(mouseX, mouseY, button);
    }

    private void drawSettingsMenu(DrawContext context, int mouseX, int mouseY) {
        int mW = 200, mH = 180, mX = this.width / 2 - mW / 2, mY = this.height / 2 - mH / 2;
        drawRoundedRect(context, mX, mY, mW, mH, 10, 0xF0050505);
        context.drawText(this.textRenderer, "Overlay Settings", mX + 15, mY + 15, ModConfig.themeColor, false);
        String[] labels = {"Red", "Green", "Blue", "Alpha", "Width"};
        float[] values = {ModConfig.overlayRed, ModConfig.overlayGreen, ModConfig.overlayBlue, ModConfig.overlayAlpha, ModConfig.lineWidth / 5.0f};
        for (int i = 0; i < 5; i++) {
            int sy = mY + 45 + (i * 25);
            context.drawText(this.textRenderer, labels[i], mX + 15, sy, 0xFFAAAAAA, false);
            drawSlider(context, mX + 20, sy + 12, 160, values[i]);
        }
    }

    private void drawSlider(DrawContext context, int x, int y, int w, float value) {
        context.fill(x, y + 2, x + w, y + 3, 0x44FFFFFF);
        int handleX = x + (int) (value * w);
        drawRoundedRect(context, handleX - 2, y, 4, 6, 2, ModConfig.themeColor);
    }

    private void drawThemeToggle(DrawContext context, String name, int tx, int ty, boolean active, int color) {
        int tw = themeW - 20;
        drawRoundedRect(context, tx, ty, tw, 22, 6, 0x88151515);
        context.drawText(this.textRenderer, name, tx + 8, ty + 7, active ? color : 0xFFCCCCCC, false);
        int swX = tx + tw - 26, swY = ty + 6;
        drawRoundedRect(context, swX, swY, 18, 10, 5, active ? color : 0xCC333333);
        int knobX = active ? swX + 9 : swX + 2;
        drawRoundedRect(context, knobX, swY + 2, 6, 6, 3, 0xFFFFFFFF);
    }

    private void drawModuleRow(DrawContext context, String name, boolean state, int rx, int ry, String rightText) {
        int rw = bgW - sideBarW - 20;
        drawRoundedRect(context, rx, ry, rw, 30, 8, 0x88151515);
        context.drawText(this.textRenderer, name, rx + 10, ry + 11, 0xFFEEEEEE, false);
        if (!rightText.isEmpty()) context.drawText(this.textRenderer, "§7" + rightText, rx + rw - 65, ry + 11, 0xFFFFFFFF, false);
        int swX = rx + rw - 35, swY = ry + 9;
        drawRoundedRect(context, swX, swY, 24, 12, 6, state ? ModConfig.themeColor : 0xCC333333);
        int knobX = state ? swX + 14 : swX + 2;
        drawRoundedRect(context, knobX, swY + 2, 8, 8, 4, 0xFFFFFFFF);
    }

    private void drawRoundedRect(DrawContext context, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w / 2, h / 2));
        context.fill(x + r, y, x + w - r, y + h, color);
        context.fill(x, y + r, x + r, y + h - r, color);
        context.fill(x + w - r, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int lw = (int) Math.round(Math.sqrt(r * r - dy * dy));
            context.fill(x + r - lw, y + i, x + r, y + i + 1, color);
            context.fill(x + w - r, y + i, x + w - r + lw, y + i + 1, color);
            context.fill(x + r - lw, y + h - i - 1, x + r, y + h - i, color);
            context.fill(x + w - r, y + h - i - 1, x + w - r + lw, y + h - i, color);
        }
    }

    private void drawRoundedRectLeft(DrawContext context, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h / 2));
        context.fill(x + r, y, x + w, y + h, color);
        context.fill(x, y + r, x + r, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int lw = (int) Math.round(Math.sqrt(r * r - dy * dy));
            context.fill(x + r - lw, y + i, x + r, y + i + 1, color);
            context.fill(x + r - lw, y + h - i - 1, x + r, y + h - i, color);
        }
    }

    private void drawTab(DrawContext context, String name, int tx, int ty) {
        boolean active = currentTab.equals(name);
        drawRoundedRect(context, tx, ty, this.textRenderer.getWidth(name) + 20, 20, 6, active ? 0xAA333333 : 0xAA151515);
        context.drawText(this.textRenderer, name, tx + 10, ty + 6, active ? 0xFFFFFFFF : 0xFFAAAAAA, false);
    }

    private String getCloseKeyName() {
        if (ModConfig.closeKey == 256) return "ESC";
        String name = GLFW.glfwGetKeyName(ModConfig.closeKey, 0);
        return (name != null) ? name.toUpperCase() : "KEY " + ModConfig.closeKey;
    }

    private boolean isHovering(double mx, double my, int x, int y, int w, int h) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
    @Override public boolean shouldPause() { return false; }
}
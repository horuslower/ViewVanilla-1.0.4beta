package viw.xs.viewvanilla.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ArmorStatusRenderer {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void render(DrawContext context, int screenWidth, int screenHeight) {
        if (!ModConfig.armorStatus || client.player == null || client.options.hudHidden) return;

        int x = (screenWidth / 2) + ModConfig.armorStatusX;
        int y = (screenHeight - 62) + ModConfig.armorStatusY;

        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = client.player.getEquippedStack(slots[i]);
            if (!stack.isEmpty()) {
                int rowY = y + (i * 10);
                context.getMatrices().pushMatrix();
                context.getMatrices().translate((float)x, (float)rowY - 1);
                context.getMatrices().scale(0.65f, 0.65f);
                context.drawItem(stack, 0, 0);
                context.getMatrices().popMatrix();

                if (stack.isDamageable()) {
                    String durability = String.valueOf(stack.getMaxDamage() - stack.getDamage());
                    context.drawTextWithShadow(client.textRenderer, durability, x + 11, rowY, getDurabilityColor(stack));
                }
            }
        }
    }

    private static int getDurabilityColor(ItemStack stack) {
        float damageFactor = (float) (stack.getMaxDamage() - stack.getDamage()) / stack.getMaxDamage();
        return MathHelper.hsvToRgb(damageFactor / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }
}
package viw.xs.viewvanilla.client.mixins;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import viw.xs.viewvanilla.client.ModConfig;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    private void onKeyInject(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        // Если нажата наша кастомная клавиша (и это не ESC)
        if (key == ModConfig.closeKey && key != GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
            MinecraftClient client = MinecraftClient.getInstance();

            // Имитируем нажатие настоящего ESC
            client.keyboard.onKey(window, GLFW.GLFW_KEY_ESCAPE, scancode, action, mods);

            // Отменяем оригинальное нажатие нашей клавиши, чтобы она не печаталась в чат
            ci.cancel();
        }
    }
}
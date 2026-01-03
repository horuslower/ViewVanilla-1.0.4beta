package viw.xs.viewvanilla.client.hitcolor;

import net.minecraft.entity.LivingEntity;

import java.util.Map;
import java.util.WeakHashMap;

public class HitColorManager {

    private static final Map<LivingEntity, Integer> HIT_TICKS = new WeakHashMap<>();
    private static final int MAX_TICKS = 10; // ~0.5 сек

    public static void markHit(LivingEntity entity) {
        HIT_TICKS.put(entity, MAX_TICKS);
    }

    public static float getProgress(LivingEntity entity) {
        Integer ticks = HIT_TICKS.get(entity);
        if (ticks == null) return 0.0f;
        return ticks / (float) MAX_TICKS;
    }

    public static void tick() {
        HIT_TICKS.entrySet().removeIf(e -> {
            int t = e.getValue() - 1;
            if (t <= 0) return true;
            e.setValue(t);
            return false;
        });
    }
}

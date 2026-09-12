package com.nebula.lib.api;

/**
 * "Focus Mode": detecta se o jogador está parado e olhando praticamente pro
 * mesmo lugar por um tempinho — nesse estado, dá pra cortar mais agressivo
 * fora do campo de visão sem o jogador notar (ele não tá girando a câmera
 * toda hora checando os cantos).
 */
public final class FocusAPI {

    private FocusAPI() {}

    private static final double MOVE_THRESHOLD = 0.05; // blocos entre updates
    private static final float ANGLE_THRESHOLD_DEG = 3.0f;
    private static final int TICKS_TO_FOCUS = 40; // ~2s parado/olhando fixo

    private static double lastX, lastY, lastZ;
    private static float lastYaw;
    private static int stillTicks = 0;
    private static boolean initialized = false;

    public static void updatePlayerState(double x, double y, double z, float yaw) {
        if (!initialized) {
            lastX = x; lastY = y; lastZ = z; lastYaw = yaw;
            initialized = true;
            return;
        }

        double moved = Math.abs(x - lastX) + Math.abs(y - lastY) + Math.abs(z - lastZ);
        float yawDelta = Math.abs(normalizeAngle(yaw - lastYaw));

        if (moved < MOVE_THRESHOLD && yawDelta < ANGLE_THRESHOLD_DEG) {
            stillTicks++;
        } else {
            stillTicks = 0;
        }

        lastX = x; lastY = y; lastZ = z; lastYaw = yaw;
    }

    private static float normalizeAngle(float angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public static boolean isFocused() {
        return stillTicks >= TICKS_TO_FOCUS;
    }

    /** Distância efetiva de render aumenta um pouco quando focado (parado olhando longe faz sentido ver mais longe). */
    public static double getFocusDistanceMultiplier() {
        return isFocused() ? 1.15 : 1.0;
    }

    /** Produto escalar mínimo pra considerar "dentro do campo de visão" quando focado (mais restrito que o normal). */
    public static double getFocusAngleThreshold() {
        return 0.5;
    }
}

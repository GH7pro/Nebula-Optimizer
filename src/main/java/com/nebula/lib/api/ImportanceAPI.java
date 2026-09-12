package com.nebula.lib.api;

/**
 * Classifica uma entidade em um nível de importância, pra decidir o quanto
 * ela "merece" ser cortada quando o jogo precisa aliviar o frame. Jogadores
 * e mobs hostis próximos nunca somem do nada; itens/passivos distantes são
 * os primeiros a ir embora.
 */
public final class ImportanceAPI {

    private ImportanceAPI() {}

    public enum Priority {
        CRITICAL, // outros jogadores — nunca corta
        HIGH,     // mob hostil perto — corta só em último caso
        NORMAL,   // mob comum / hostil longe
        LOW       // item, passivo distante, etc — primeiro a ser cortado
    }

    private static final double CLOSE_RANGE = 16.0; // blocos

    public static Priority classify(String entityType, double distance, boolean hostile, boolean isPlayer) {
        if (isPlayer) {
            return Priority.CRITICAL;
        }
        if (hostile && distance < CLOSE_RANGE) {
            return Priority.HIGH;
        }
        if (hostile) {
            return Priority.NORMAL;
        }
        if (entityType != null && entityType.contains("item")) {
            return Priority.LOW;
        }
        return distance < CLOSE_RANGE ? Priority.NORMAL : Priority.LOW;
    }

    /**
     * Regra extra de corte por importância, além da distância pura: um LOW
     * levemente atrás da câmera já pode sumir, mesmo perto — não vale a
     * pena gastar frame com um item que o jogador nem tá olhando.
     */
    public static boolean shouldCull(Priority priority, double distSq, double dot) {
        if (priority == Priority.CRITICAL) {
            return false;
        }
        if (priority == Priority.LOW && dot < 0.1) {
            return true;
        }
        return false;
    }
}

package com.nebula.lib.api;

/**
 * Ponto de entrada único pra "acordar" as APIs internas do Nebula
 * (Budget/Culling/Focus/Importance/Performance/Profile). Elas já são
 * static/stateless o suficiente pra não precisarem de inicialização
 * pesada, mas isso dá um lugar central pra logar e, no futuro, plugar
 * qualquer setup que precise rodar uma vez só, no início do jogo.
 */
public final class NebulaAPI {

    private static final NebulaAPI INSTANCE = new NebulaAPI();
    private boolean initialized = false;

    private NebulaAPI() {}

    public static NebulaAPI getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        if (initialized) return;
        initialized = true;
        BudgetAPI.beginFrame();
        System.out.println("[Nebula] 📚 NebulaAPI (lib) inicializada!");
    }

    public boolean isInitialized() {
        return initialized;
    }
}

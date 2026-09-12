package com.nebula.engine;

/**
 * Interface base para todos os módulos do Nebula.
 */
public interface NebulaModule {

    /**
     * Nome do módulo.
     */
    String getName();

    /**
     * Inicializa o módulo.
     */
    void initialize();

    /**
     * Executado a cada tick.
     */
    void tick();

    /**
     * Finaliza o módulo.
     */
    void shutdown();

    /**
     * Verifica se o módulo está ativo.
     */
    boolean isEnabled();
}

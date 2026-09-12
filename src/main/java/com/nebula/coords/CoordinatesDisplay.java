package com.nebula.coords;

import com.nebula.NebulaConfig;
import com.nebula.engine.NebulaModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CoordinatesDisplay implements NebulaModule {
    private static final CoordinatesDisplay INSTANCE = new CoordinatesDisplay();
    private boolean enabled = true;
    private boolean showCoords = true;
    
    private CoordinatesDisplay() {}
    
    public static CoordinatesDisplay getInstance() {
        return INSTANCE;
    }
    
    @Override
    public String getName() {
        return "Coordinates Display";
    }
    
    @Override
    public void initialize() {
        System.out.println("📍 [Nebula] Coordinates Display inicializado!");
        if (!NebulaConfig.enableCoordinatesDisplay) return;
        
        System.out.println("📍 [Nebula] ✅ Coordenadas ativadas!");
    }
    
    @Override
    public void tick() {
        // Não faz nada no tick, só renderiza
    }
    
    public void render(DrawContext context, MinecraftClient client) {
        if (!enabled || !NebulaConfig.enableCoordinatesDisplay) return;
        if (!showCoords) return;
        if (client == null || client.player == null) return;
        
        TextRenderer renderer = client.textRenderer;
        BlockPos pos = client.player.getBlockPos();
        
        // Obtém as coordenadas
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        
        // Obtém a direção
        String direction = getDirection(client.player.getYaw());
        
        // Formata o texto
        String coordsText = String.format("📍 %d %d %d  %s", x, y, z, direction);
        
        // Posição na tela (canto superior esquerdo)
        int xPos = 10;
        int yPos = 10;
        
        // Desenha o fundo
        int width = renderer.getWidth(coordsText) + 10;
        context.fill(xPos - 5, yPos - 2, xPos + width, yPos + 12, 0x80000000);
        
        // Desenha o texto
        context.drawText(renderer, Text.literal(coordsText), xPos, yPos, 0xFFFFFF, true);
    }
    
    private String getDirection(float yaw) {
        yaw = MathHelper.wrapDegrees(yaw);
        if (yaw < -135 || yaw >= 135) return "N";
        if (yaw >= -135 && yaw < -45) return "E";
        if (yaw >= -45 && yaw < 45) return "S";
        if (yaw >= 45 && yaw < 135) return "W";
        return "?";
    }
    
    public void toggle() {
        showCoords = !showCoords;
    }
    
    public boolean isShowing() {
        return showCoords;
    }
    
    @Override
    public void shutdown() {
        System.out.println("📍 [Nebula] Coordinates Display desligado!");
    }
    
    @Override
    public boolean isEnabled() {
        return enabled && NebulaConfig.enableCoordinatesDisplay;
    }
}

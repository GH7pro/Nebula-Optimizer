package com.nebula.profiles;

import net.minecraft.client.MinecraftClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DeviceDetector {
    private static final DeviceDetector INSTANCE = new DeviceDetector();
    
    // Resultados da detecção
    private DeviceType deviceType = DeviceType.UNKNOWN;
    private int cpuCores = 0;
    private long maxMemory = 0;
    private boolean hasGPU = false;
    private boolean isMobile = false;
    private boolean isLaptop = false;
    private String osName = "";
    
    public enum DeviceType {
        GAMING_PC("PC Gamer"),
        DESKTOP("Desktop"),
        LAPTOP("Notebook"),
        MOBILE("Celular/Tablet"),
        LOW_END("PC Fraco"),
        UNKNOWN("Desconhecido");
        
        private final String displayName;
        
        DeviceType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private DeviceDetector() {}
    
    public static DeviceDetector getInstance() {
        return INSTANCE;
    }
    
    public void detect() {
        System.out.println("📱 [Nebula] Detectando hardware...");
        
        // ===== 1. CPU =====
        cpuCores = Runtime.getRuntime().availableProcessors();
        System.out.println("📱 [Nebula] CPU: " + cpuCores + " núcleos");
        
        // ===== 2. MEMÓRIA =====
        maxMemory = Runtime.getRuntime().maxMemory();
        System.out.println("📱 [Nebula] RAM: " + (maxMemory / 1024 / 1024) + " MB");
        
        // ===== 3. SISTEMA OPERACIONAL =====
        osName = System.getProperty("os.name").toLowerCase();
        System.out.println("📱 [Nebula] OS: " + osName);
        
        // ===== 4. DETECTA SE É MOBILE (Android) =====
        isMobile = isAndroid();
        if (isMobile) {
            System.out.println("📱 [Nebula] 📱 Dispositivo móvel detectado!");
        }
        
        // ===== 5. DETECTA SE É LAPTOP =====
        isLaptop = isLaptopDevice();
        if (isLaptop) {
            System.out.println("📱 [Nebula] 💻 Notebook detectado!");
        }
        
        // ===== 6. DETECTA GPU =====
        hasGPU = detectGPU();
        System.out.println("📱 [Nebula] GPU: " + (hasGPU ? "✅ Sim" : "❌ Não detectada"));
        
        // ===== 7. DETERMINA O PERFIL =====
        determineProfile();
        
        System.out.println("📱 [Nebula] ✅ Perfil detectado: " + deviceType.getDisplayName());
    }
    
    private boolean isAndroid() {
        // Verifica se está rodando no Android (FCL, PojavLauncher)
        try {
            // Procura por arquivos típicos do Android
            String[] androidPaths = {
                "/system/build.prop",
                "/data/data/com.android",
                "/sdcard/Android"
            };
            
            for (String path : androidPaths) {
                try {
                    java.io.File f = new java.io.File(path);
                    if (f.exists()) {
                        return true;
                    }
                } catch (Exception e) {}
            }
            
            // Verifica propriedades do sistema
            String javaVendor = System.getProperty("java.vendor");
            if (javaVendor != null && javaVendor.toLowerCase().contains("android")) {
                return true;
            }
            
            // Verifica se está no termux
            try {
                Process process = Runtime.getRuntime().exec("uname -a");
                BufferedReader reader = new BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream())
                );
                String line = reader.readLine();
                reader.close();
                if (line != null && line.toLowerCase().contains("android")) {
                    return true;
                }
            } catch (Exception e) {}
            
        } catch (Exception e) {}
        
        return false;
    }
    
    private boolean isLaptopDevice() {
        try {
            // Linux: verifica se tem bateria
            String[] batteryPaths = {
                "/sys/class/power_supply/BAT0",
                "/sys/class/power_supply/BAT1",
                "/sys/class/power_supply/battery"
            };
            
            for (String path : batteryPaths) {
                try {
                    java.io.File f = new java.io.File(path);
                    if (f.exists() && f.isDirectory()) {
                        return true;
                    }
                } catch (Exception e) {}
            }
            
            // Windows: verifica se tem bateria
            if (osName.contains("win")) {
                try {
                    Process process = Runtime.getRuntime().exec("powercfg /getactivescheme");
                    BufferedReader reader = new BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream())
                    );
                    String line = reader.readLine();
                    reader.close();
                    // Se tem esquema de energia, provavelmente é laptop
                    if (line != null && line.toLowerCase().contains("power")) {
                        return true;
                    }
                } catch (Exception e) {}
            }
            
        } catch (Exception e) {}
        
        return false;
    }
    
    private boolean detectGPU() {
        try {
            // Linux: verifica GPU
            try {
                Process process = Runtime.getRuntime().exec("lspci | grep -i vga");
                BufferedReader reader = new BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream())
                );
                String line = reader.readLine();
                reader.close();
                if (line != null && !line.isEmpty()) {
                    return true;
                }
            } catch (Exception e) {}
            
            // Android: verifica OpenGL
            try {
                // Verifica se tem OpenGL ES
                Class.forName("javax.microedition.khronos.opengles.GL10");
                return true;
            } catch (ClassNotFoundException e) {}
            
        } catch (Exception e) {}
        
        return false;
    }
    
    private void determineProfile() {
        // ===== CRITÉRIOS PARA CADA PERFIL =====
        
        // MOBILE
        if (isMobile) {
            deviceType = DeviceType.MOBILE;
            return;
        }
        
        // LAPTOP
        if (isLaptop) {
            if (cpuCores >= 8 && maxMemory >= 4_000_000_000L && hasGPU) {
                deviceType = DeviceType.GAMING_PC;
            } else if (cpuCores >= 4 && maxMemory >= 2_000_000_000L) {
                deviceType = DeviceType.LAPTOP;
            } else {
                deviceType = DeviceType.LOW_END;
            }
            return;
        }
        
        // DESKTOP / PC
        if (cpuCores >= 8 && maxMemory >= 8_000_000_000L && hasGPU) {
            deviceType = DeviceType.GAMING_PC;
        } else if (cpuCores >= 4 && maxMemory >= 4_000_000_000L) {
            deviceType = DeviceType.DESKTOP;
        } else if (cpuCores >= 2 && maxMemory >= 2_000_000_000L) {
            deviceType = DeviceType.LOW_END;
        } else {
            deviceType = DeviceType.UNKNOWN;
        }
    }
    
    public DeviceType getDeviceType() {
        return deviceType;
    }
    
    public int getCpuCores() {
        return cpuCores;
    }
    
    public long getMaxMemory() {
        return maxMemory;
    }
    
    public boolean hasGPU() {
        return hasGPU;
    }
    
    public boolean isMobile() {
        return isMobile;
    }
    
    public boolean isLaptop() {
        return isLaptop;
    }
    
    public String getOsName() {
        return osName;
    }
}

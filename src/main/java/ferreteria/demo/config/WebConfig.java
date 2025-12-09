package ferreteria.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // --- Mapeo Dinámico para Archivos Subidos (uploaded-products) ---
        
        // 1. Obtiene la ruta de la raiz del proyecto (user.dir)
        String projectRootPath = System.getProperty("user.dir");

        // 2. Construye la ruta absoluta al directorio de imagenes subidas
        String absolutePath = projectRootPath + File.separator + "uploaded-products" + File.separator;

        // 3. Formatear la ruta para el manejador de recursos (añadir prefijo 'file:')
        
        // Reemplaza los separadores de sistema (ej. '\' en Windows) por barras normales ('/')
        String formattedPath = absolutePath.replace(File.separator, "/");

        // Añade el prefijo 'file:///' o 'file:' segun el sistema operativo
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
             formattedPath = "file:///" + formattedPath;
        } else {
            formattedPath = "file:" + formattedPath;
        }

        // 4. Mapeo: URL /uploaded-products/** se apunta a la carpeta fisica
        registry.addResourceHandler("/uploaded-products/**")
                .addResourceLocations(formattedPath)
                .setCachePeriod(3600);

        // MENSAJE DE DIAGNOSTICO (SIN UNICODE)
        System.out.println("--- CONFIGURACION IMAGENES ---");
        System.out.println("Mapeo de Imagenes Subidas (Dinamico): URL: /uploaded-products/** -> DISCO: " + formattedPath);
        System.out.println("------------------------------");

        // 5. Mapeo de recursos estaticos por defecto (CSS, JS, imagenes de /static/)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/public/", "classpath:/resources/");
    }
}
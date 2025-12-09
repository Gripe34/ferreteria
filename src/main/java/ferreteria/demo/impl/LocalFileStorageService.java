package ferreteria.demo.impl;

import ferreteria.demo.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements StorageService {

    private final Path rootLocation;
    private final String uploadDir;

    // Constructor que inicializa el directorio
    public LocalFileStorageService(@Value("${storage.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;

        // 1. Resuelve la ruta absoluta de la carpeta de subida.
        this.rootLocation = Paths.get(new File(uploadDir).getAbsolutePath());

        // 🔥 MODIFICACIÓN CLAVE 🔥
        // 2. Guarda la RUTA ABSOLUTA en una propiedad del sistema (file.upload.path).
        // Esto permite que WebConfig LEA la misma ruta exacta que este servicio usó.
        System.setProperty("file.upload.path", this.rootLocation.toString() + File.separator);

        // 3. MENSAJE DE DIAGNÓSTICO
        System.out.println("🔥 RUTA ABSOLUTA DE ALMACENAMIENTO: " + this.rootLocation.toString());

        try {
            if (!Files.exists(this.rootLocation)) {
                Files.createDirectories(this.rootLocation);
            }
        } catch (IOException e) {
            // Lanza una excepción al fallar la inicialización
            throw new RuntimeException("No se pudo inicializar el directorio de almacenamiento en: " + this.rootLocation.toString(), e);
        }
    }

   @Override
public String store(MultipartFile file) throws IOException {
    if (file.isEmpty()) {
        return null;
    }

    String originalFilename = file.getOriginalFilename();
    String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
    String newFilename = UUID.randomUUID().toString() + fileExtension;

    // 1. DEFINIR LA SUBCARPETA TARGET (La que espera la BD)
    Path subPath = Paths.get("images", "productos"); 
    
    // 2. CREAR EL DIRECTORIO COMPLETO si no existe
    Path targetDirectory = this.rootLocation.resolve(subPath);

    if (!Files.exists(targetDirectory)) {
        // Esta línea es CRÍTICA y crea C:/.../uploaded-products/images/productos
        Files.createDirectories(targetDirectory); 
    }
    
    // 3. DEFINIR LA RUTA ABSOLUTA FINAL DEL ARCHIVO
    Path destinationFile = targetDirectory.resolve(newFilename)
            .normalize().toAbsolutePath();

    // 4. Copiar (guardar) el archivo
    try (var inputStream = file.getInputStream()) {
        Files.copy(inputStream, destinationFile,
                StandardCopyOption.REPLACE_EXISTING);
    }

    // 5. Devolver la URL relativa que el navegador usará (Ya incluye la subcarpeta)
    // Se asegura de usar barras '/' para la URL, no File.separator (\)
    return "/images/productos/" + newFilename; 
}


    @Override
    public void delete(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        String fileName = Paths.get(filePath).getFileName().toString();

        try {
            // Se usa rootLocation que ya es absoluta.
            Path fileToDelete = this.rootLocation.resolve(fileName);
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            System.err.println("Advertencia: No se pudo eliminar el archivo. Error: " + e.getMessage());
        }
    }
}
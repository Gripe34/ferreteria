package ferreteria.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {

    // Guarda el archivo y devuelve la URL o ruta de acceso
    String store(MultipartFile file) throws IOException;

    // Opcional: Para eliminar archivos antiguos si se reemplazan
    void delete(String filePath);
}
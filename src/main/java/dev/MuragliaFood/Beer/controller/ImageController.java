package dev.MuragliaFood.Beer.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {
	
	private final String fotoProdottiDirectory = "src/main/resources/static/fotoProdotti";

    @GetMapping("/fotoProdotti/{imgName}")
    public ResponseEntity<byte[]> getFotoProdotto(@PathVariable String imgName) throws IOException {
        String uploadDirectory = fotoProdottiDirectory; // Il percorso della directory delle immagini
        Path imagePath = Path.of(uploadDirectory, imgName);

        if (Files.exists(imagePath)) {
            byte[] imageBytes = Files.readAllBytes(imagePath);
            // Determina dinamicamente il content type (tipo MIME) del file
            String contentType = Files.probeContentType(imagePath);
            return ResponseEntity.ok()
                                 .contentType(MediaType.parseMediaType(contentType))
                                 .body(imageBytes);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
}
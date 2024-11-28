package com.iesjuanbosco.wallapop.service;

import com.iesjuanbosco.wallapop.entity.Anuncio;
import com.iesjuanbosco.wallapop.entity.FotoAnuncio;
import com.iesjuanbosco.wallapop.repository.FotoAnuncioRepository;
import com.iesjuanbosco.wallapop.repository.AnuncioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FotoAnuncioService {

    private static final List<String> PERMITTED_TYPES = List.of("image/jpeg", "image/png", "image/gif", "image/avif", "image/webp");
    private static final long MAX_FILE_SIZE = 10000000; // 10 MB
    private static final String UPLOADS_DIRECTORY = "uploads/imagesAnuncios";

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private FotoAnuncioRepository fotoAnuncioRepository;

    // Guardar una lista de fotos para un anuncio
    public List<FotoAnuncio> guardarFotos(List<MultipartFile> fotos, Anuncio anuncio) {
        List<FotoAnuncio> listaFotos = new ArrayList<>();
        for (MultipartFile foto : fotos) {
            if (!foto.isEmpty()) {
                validarArchivo(foto); // Validar archivo
                String nombreFoto = generarNombreUnico(foto); // Generar nombre único
                guardarArchivo(foto, nombreFoto); // Guardar archivo físicamente

                FotoAnuncio fotoAnuncio = FotoAnuncio.builder()
                        .nombre(nombreFoto)
                        .anuncio(anuncio)
                        .build();

                listaFotos.add(fotoAnuncio);
            }
        }
        anuncio.getFotos().addAll(listaFotos); // Añadir fotos al anuncio
        return listaFotos;
    }


    // Agregar una única foto a un anuncio
    public void addFoto(MultipartFile foto, Anuncio anuncio) {
        if (!foto.isEmpty()) {
            validarArchivo(foto);
            String nombreFoto = generarNombreUnico(foto);
            guardarArchivo(foto, nombreFoto);

            FotoAnuncio fotoAnuncio = FotoAnuncio.builder()
                    .nombre(nombreFoto)
                    .anuncio(anuncio)
                    .build();

            anuncio.getFotos().add(fotoAnuncio);
            anuncioRepository.save(anuncio);
        }
    }

    // Validar que el archivo cumple con los requisitos
    public void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Archivo no seleccionado.");
        }
        if (!PERMITTED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("El archivo seleccionado no es una imagen válida.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Archivo demasiado grande. Solo se admiten archivos < 10MB.");
        }
    }

    // Generar un nombre único para la foto
    public String generarNombreUnico(MultipartFile file) {
        UUID nombreUnico = UUID.randomUUID();
        String extension;
        if (file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty()) {
            extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        } else {
            throw new IllegalArgumentException("El archivo seleccionado no tiene una extensión válida.");
        }
        return nombreUnico + extension;
    }

    // Guardar el archivo físicamente en el disco
    public void guardarArchivo(MultipartFile file, String nuevoNombreFoto) {
        Path ruta = Paths.get(UPLOADS_DIRECTORY + File.separator + nuevoNombreFoto);
        try {
            byte[] contenido = file.getBytes();
            Files.write(ruta, contenido);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo en el disco.", e);
        }
    }

    // Eliminar una foto específica
    public void deleteFoto(Long idFoto) {
        Optional<FotoAnuncio> fotoAnuncioOptional = fotoAnuncioRepository.findById(idFoto);
        if (fotoAnuncioOptional.isPresent()) {
            FotoAnuncio fotoAnuncio = fotoAnuncioOptional.get();
            Path archivoFoto = Paths.get("uploads/imagesAnuncios/" + fotoAnuncio.getNombre());
            try {
                Files.deleteIfExists(archivoFoto); // Eliminar archivo del disco
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar el archivo físico.", e);
            }
            fotoAnuncioRepository.deleteById(idFoto); // Eliminar de la base de datos
        } else {
            throw new IllegalArgumentException("La foto con ID " + idFoto + " no existe.");
        }
    }

}

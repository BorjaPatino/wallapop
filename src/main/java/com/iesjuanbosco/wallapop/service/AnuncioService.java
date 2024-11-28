package com.iesjuanbosco.wallapop.service;

import com.iesjuanbosco.wallapop.entity.Anuncio;
import com.iesjuanbosco.wallapop.entity.FotoAnuncio;
import com.iesjuanbosco.wallapop.entity.Usuario;
import com.iesjuanbosco.wallapop.repository.AnuncioRepository;
import com.iesjuanbosco.wallapop.repository.FotoAnuncioRepository;
import com.iesjuanbosco.wallapop.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final FotoAnuncioRepository fotoAnuncioRepository;
    private final UsuarioRepository usuarioRepository;
    private final FotoAnuncioService fotoAnuncioService; // Declarar el servicio

    @Autowired
    public AnuncioService(AnuncioRepository anuncioRepository,
                          FotoAnuncioRepository fotoAnuncioRepository,
                          UsuarioRepository usuarioRepository,
                          FotoAnuncioService fotoAnuncioService) { // Inyectar aquí
        this.anuncioRepository = anuncioRepository;
        this.fotoAnuncioRepository = fotoAnuncioRepository;
        this.usuarioRepository = usuarioRepository;
        this.fotoAnuncioService = fotoAnuncioService; // Asignar al atributo
    }

    // Listar todos los anuncios ordenados por fecha descendente
    public List<Anuncio> findAllAnuncios() {
        return anuncioRepository.findAllByOrderByFechaCreacionDesc();
    }

    // Listar anuncios por usuario
    public List<Anuncio> findAnunciosByUsuario(Usuario usuario) {
        return anuncioRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    // Guardar un nuevo anuncio
    public void saveAnuncio(Anuncio anuncio, Usuario usuario, List<MultipartFile> fotos) {
        anuncio.setUsuario(usuario);
        anuncio.setFechaCreacion(LocalDateTime.now());
        anuncioRepository.save(anuncio);
        guardarFotos(fotos, anuncio); // Manejo de las fotos
    }


    // Editar un anuncio existente con fotos
    public void updateAnuncio(Long id, Anuncio nuevosDatos, List<MultipartFile> nuevasFotos, List<Long> fotosEliminar, Usuario usuario) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado."));

        if (!anuncio.getUsuario().equals(usuario)) {
            throw new SecurityException("No tienes permiso para editar este anuncio.");
        }

        // Actualizar título y descripción
        anuncio.setTitulo(nuevosDatos.getTitulo());
        anuncio.setDescripcion(nuevosDatos.getDescripcion());
        anuncio.setPrecio(nuevosDatos.getPrecio());

        // Eliminar fotos seleccionadas
        if (fotosEliminar != null) {
            fotosEliminar.forEach(fotoId -> fotoAnuncioService.deleteFoto(fotoId));
        }

        // Agregar nuevas fotos
        if (nuevasFotos != null) {
            fotoAnuncioService.guardarFotos(nuevasFotos, anuncio);
        }

        anuncioRepository.save(anuncio); // Guardar cambios
    }

    public Optional<Anuncio> findById(Long id) {
        return anuncioRepository.findById(id);
    }



    // Eliminar un anuncio
    public void deleteAnuncio(Long id, Usuario usuario) {
        Anuncio anuncio = anuncioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado."));
        if (!anuncio.getUsuario().equals(usuario)) {
            throw new SecurityException("No tienes permisos para eliminar este anuncio.");
        }

        // Eliminar las fotos asociadas al anuncio
        anuncio.getFotos().forEach(foto -> {
            Path ruta = Paths.get("uploads/imagesAnuncios/" + foto.getNombre());
            try {
                Files.deleteIfExists(ruta); // Eliminar archivo físicamente
            } catch (IOException e) {
                throw new RuntimeException("Error al eliminar las fotos.", e);
            }
        });

        //eliminar anuncio
        anuncioRepository.delete(anuncio);
    }

    // Guardar fotos asociadas al anuncio
    private void guardarFotos(List<MultipartFile> fotos, Anuncio anuncio) {
        if (fotos != null) {
            for (MultipartFile foto : fotos) {
                if (!foto.isEmpty()) {
                    String nombreFoto = generarNombreUnico(foto);
                    guardarArchivo(foto, nombreFoto);

                    FotoAnuncio fotoAnuncio = FotoAnuncio.builder()
                            .nombre(nombreFoto)
                            .anuncio(anuncio)
                            .build();

                    fotoAnuncioRepository.save(fotoAnuncio);
                }
            }
        }
    }

    // Validación y generación de nombres únicos para las fotos
    private String generarNombreUnico(MultipartFile file) {
        UUID nombreUnico = UUID.randomUUID();
        String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        return nombreUnico + extension;
    }

    private void guardarArchivo(MultipartFile file, String nombreFoto) {
        Path ruta = Paths.get("uploads/imagesAnuncios/" + nombreFoto);
        try {
            Files.write(ruta, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen", e);
        }
    }
}
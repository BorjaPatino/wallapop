package com.iesjuanbosco.wallapop.controller;

import com.iesjuanbosco.wallapop.entity.Anuncio;
import com.iesjuanbosco.wallapop.entity.FotoAnuncio;
import com.iesjuanbosco.wallapop.repository.AnuncioRepository;
import com.iesjuanbosco.wallapop.repository.FotoAnuncioRepository;
import com.iesjuanbosco.wallapop.service.FotoAnuncioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;

import java.util.Optional;

@Controller
public class FotoAnuncioController {

    @Autowired
    private FotoAnuncioRepository fotoAnuncioRepository;

    @Autowired
    private AnuncioRepository anuncioRepository;

    @Autowired
    private FotoAnuncioService fotoAnuncioService;

    // Eliminar una foto de un anuncio
    @GetMapping("/anuncios/{idAnuncio}/fotos/{idFoto}/delete")
    public String deleteFoto(@PathVariable("idAnuncio") Long idAnuncio,
                             @PathVariable("idFoto") Long idFoto) {
        fotoAnuncioService.deleteFoto(idFoto);
        return "redirect:/anuncios/edit/" + idAnuncio;
    }

    // Agregar una nueva foto a un anuncio existente
    @PostMapping("/anuncios/edit/{id}/addfoto")
    public String addFoto(@PathVariable("id") Long idAnuncio, Model model,
                          @RequestParam(value = "archivoFoto") MultipartFile archivoFoto) {
        Optional<Anuncio> anuncioOptional = anuncioRepository.findById(idAnuncio);
        if (anuncioOptional.isPresent()) {
            fotoAnuncioService.addFoto(archivoFoto, anuncioOptional.get());
        }
        return "redirect:/anuncios/edit/" + idAnuncio;
    }
}

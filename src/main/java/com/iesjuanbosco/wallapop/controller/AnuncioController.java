package com.iesjuanbosco.wallapop.controller;


import com.iesjuanbosco.wallapop.entity.Anuncio;
import com.iesjuanbosco.wallapop.entity.Usuario;
import com.iesjuanbosco.wallapop.service.AnuncioService;
import com.iesjuanbosco.wallapop.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.security.Principal;
import org.springframework.ui.Model;
import java.util.List;

@Controller
@RequestMapping("/anuncios")
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final UsuarioService usuarioService;

    @Autowired
    public AnuncioController(AnuncioService anuncioService, UsuarioService usuarioService) {
        this.anuncioService = anuncioService;
        this.usuarioService = usuarioService;
    }

    // Página principal con todos los anuncios ordenados por fecha
    @GetMapping("/")
    public String listarAnuncios(Model model) {
        model.addAttribute("anuncios", anuncioService.findAllAnuncios());
        model.addAttribute("totalAnuncios", anuncioService.findAllAnuncios().size());
        return "anuncio-list";
    }

    // Página "Mis anuncios" - Listado de anuncios del usuario autenticado
    @GetMapping("/mis-anuncios")
    public String misAnuncios(Model model, Principal principal) {
        Usuario usuario = usuarioService.findByEmail(principal.getName());
        model.addAttribute("anuncios", anuncioService.findAnunciosByUsuario(usuario));
        return "mis-anuncios";
    }

    // Ver detalle de un anuncio
    @GetMapping("/ver/{id}")
    public String verAnuncio(@PathVariable Long id, Model model) {
        Anuncio anuncio = anuncioService.findAllAnuncios().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado"));
        model.addAttribute("anuncio", anuncio);
        return "anuncio-view";
    }

    // Formulario para crear un nuevo anuncio
    @GetMapping("/new")
    public String nuevoAnuncio(Model model) {
        model.addAttribute("anuncio", new Anuncio());
        return "anuncio-new";
    }

    // Guardar un nuevo anuncio
    @PostMapping("/new")
    public String guardarAnuncio(@Valid @ModelAttribute("anuncio") Anuncio anuncio,
                                 BindingResult bindingResult,
                                 @RequestParam("fotos") List<MultipartFile> fotos,
                                 Principal principal,
                                 Model model) {
        Usuario usuario = usuarioService.findByEmail(principal.getName());
        anuncioService.saveAnuncio(anuncio, usuario, fotos);
        return "redirect:/anuncios/";
    }

    // Formulario para editar un anuncio
    @GetMapping("/edit/{id}")
    public String editarAnuncio(@PathVariable Long id, Model model, Principal principal) {
        Anuncio anuncio = anuncioService.findAllAnuncios().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Anuncio no encontrado"));
        if (!anuncio.getUsuario().getEmail().equals(principal.getName())) {
            throw new SecurityException("No tienes permisos para editar este anuncio.");
        }
        model.addAttribute("anuncio", anuncio);
        return "anuncio-edit";
    }

    // Actualizar un anuncio existente
    @PostMapping("/edit/{id}")
    public String actualizarAnuncio(
            @PathVariable Long id,
            @Valid @ModelAttribute("anuncio") Anuncio anuncio,
            BindingResult bindingResult,
            @RequestParam(value = "fotos", required = false) List<MultipartFile> fotos,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            System.out.println("Errores en el formulario: " + bindingResult.getAllErrors());
            return "anuncio-edit"; // Devuelve al formulario con erroresss
        }

        System.out.println("Título: " + anuncio.getTitulo());
        System.out.println("Precio: " + anuncio.getPrecio());
        System.out.println("Descripción: " + anuncio.getDescripcion());

        if (fotos != null) {
            System.out.println("Número de fotos subidas: " + fotos.size());
        } else {
            System.out.println("No se subieron fotos.");
        }

        Usuario usuario = usuarioService.findByEmail(principal.getName());
        anuncio.setId(id);

        anuncioService.updateAnuncio(anuncio, usuario, fotos);

        return "redirect:/anuncios/";
    }






    // Eliminar un anuncio
    @GetMapping("/delete/{id}")
    public String eliminarAnuncio(@PathVariable Long id, Principal principal) {
        Usuario usuario = usuarioService.findByEmail(principal.getName());
        anuncioService.deleteAnuncio(id, usuario);
        return "redirect:/anuncios/";
    }
}

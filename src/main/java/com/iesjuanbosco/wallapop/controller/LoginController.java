package com.iesjuanbosco.wallapop.controller;

import org.springframework.ui.Model;
import com.iesjuanbosco.wallapop.service.AnuncioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @Autowired
    private AnuncioService anuncioService;

    @GetMapping("/login")
    public String login(Model model) {
        // Pasar la lista de anuncios al modelo
        model.addAttribute("anuncios", anuncioService.findAllAnuncios());
        return "login"; // Renderiza login.html
    }
}

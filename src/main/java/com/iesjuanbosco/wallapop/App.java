package com.iesjuanbosco.wallapop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App {

	//Comentario de ejemplo
	public static void main(String[] args) {

		// Extraemos un objeto que va a contener el "contexto de la app", y desde él podemos acceder a todos los beans (controladores, repositorios, etc.)
		ApplicationContext context = SpringApplication.run(App.class, args);

		// El siguiente código se ejecutará cada vez que se ejecute la app.
		// En este caso, no insertaremos datos de ejemplo para asegurar que solo se creen las tablas.

        /*
        // Si decides agregar datos de ejemplo en el futuro, puedes descomentar este bloque
        AnuncioRepository anuncioRepository = context.getBean(AnuncioRepository.class);
        UsuarioRepository usuarioRepository = context.getBean(UsuarioRepository.class);

        // Crear un usuario de ejemplo
        Usuario usuario = Usuario.builder()
                .email("usuario@ejemplo.com")
                .password("$2a$10$Dfj1kJQK3WKH8.Dfjf0E1eKlvfJ98XkLOifJ8dhYt") // Contraseña encriptada (bcrypt)
                .nombre("Juan")
                .telefono("123456789")
                .poblacion("Madrid")
                .anuncios(new ArrayList<>()) // Inicializamos la lista de anuncios
                .build();

        // Crear anuncios de ejemplo
        Anuncio anuncio1 = Anuncio.builder()
                .titulo("iPhone 13")
                .descripcion("iPhone 13 en perfecto estado, poco uso")
                .precio(850.0)
                .usuario(usuario)
                .build();

        Anuncio anuncio2 = Anuncio.builder()
                .titulo("Bicicleta de montaña")
                .descripcion("Bicicleta de montaña nueva, con todos los accesorios")
                .precio(300.0)
                .usuario(usuario)
                .build();

        // Añadimos los anuncios al usuario
        usuario.getAnuncios().add(anuncio1);
        usuario.getAnuncios().add(anuncio2);

        // Guardamos el usuario, y en cascada se guardan los anuncios
        usuarioRepository.save(usuario);
        */
	}
}

package dw.editora.control;

import java.util.Optional;

import dw.editora.entity.Artigo;
import dw.editora.entity.Autor;
import dw.editora.repository.ArtigoRepository;
import dw.editora.repository.AutorRepository;
import dw.editora.service.ArtigoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ArtigoControllerTests {
    private ArtigoRepository artigos;
    private AutorRepository autores;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        artigos = mock(ArtigoRepository.class);
        autores = mock(AutorRepository.class);
        mvc = MockMvcBuilders.standaloneSetup(
                new ArtigoController(new ArtigoService(artigos, autores))).build();
    }

    @Test
    void rejeitaJsonDaCapturaSemAutor() throws Exception {
        mvc.perform(post("/api/artigos").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo":"Meu primeiro artigo","resumo":"Um resumo qualquer","publicado":true}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(
                        "Informe o autor do artigo no formato: autor: { id: ID_DO_AUTOR }."));
        verifyNoInteractions(artigos, autores);
    }

    @Test
    void rejeitaAutorSemId() throws Exception {
        mvc.perform(post("/api/artigos").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo":"Artigo","resumo":"Resumo","publicado":true,"autor":{}}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").exists());
        verifyNoInteractions(artigos, autores);
    }

    @Test
    void rejeitaAutorInexistente() throws Exception {
        when(autores.findById(99L)).thenReturn(Optional.empty());
        mvc.perform(post("/api/artigos").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo":"Artigo","resumo":"Resumo","publicado":true,"autor":{"id":99}}
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(
                        "Autor não encontrado. Cadastre o autor antes de criar o artigo."));
        verifyNoInteractions(artigos);
    }

    @Test
    void criaArtigoComAutorCadastrado() throws Exception {
        Autor autor = new Autor(1L, "Maria", "maria@example.com");
        when(autores.findById(1L)).thenReturn(Optional.of(autor));
        when(artigos.save(any(Artigo.class))).thenAnswer(invocation -> {
            Artigo artigo = invocation.getArgument(0);
            artigo.setId(10L);
            return artigo;
        });
        mvc.perform(post("/api/artigos").contentType(MediaType.APPLICATION_JSON).content("""
                {"titulo":"Meu primeiro artigo","resumo":"Um resumo qualquer",
                 "publicado":true,"autor":{"id":1}}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Meu primeiro artigo"))
                .andExpect(jsonPath("$.publicado").value(true))
                .andExpect(jsonPath("$.autor.id").value(1))
                .andExpect(jsonPath("$.autor.nome").value("Maria"));
        verify(artigos).save(argThat(artigo -> artigo.getAutor() == autor));
    }
}

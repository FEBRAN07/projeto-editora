package dw.editora.control;

import java.util.List;

import dw.editora.entity.Artigo;
import dw.editora.entity.Autor;
import dw.editora.repository.ArtigoRepository;
import dw.editora.repository.AutorRepository;
import dw.editora.service.AutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AutorControllerTests {
    private ArtigoRepository artigos;
    private AutorRepository autores;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        artigos = mock(ArtigoRepository.class);
        autores = mock(AutorRepository.class);

        AutorService service = new AutorService();
        ReflectionTestUtils.setField(service, "autorRepository", autores);
        ReflectionTestUtils.setField(service, "artigoRepository", artigos);

        AutorController controller = new AutorController();
        ReflectionTestUtils.setField(controller, "autorService", service);

        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private Artigo artigo(Long id, String titulo, boolean publicado, Autor autor) {
        Artigo artigo = new Artigo();
        artigo.setId(id);
        artigo.setTitulo(titulo);
        artigo.setResumo("Resumo");
        artigo.setPublicado(publicado);
        artigo.setAutor(autor);
        return artigo;
    }

    @Test
    void retornaArtigosDoAutor() throws Exception {
        Autor autor = new Autor(1L, "Maria", "maria@example.com");
        when(autores.existsById(1L)).thenReturn(true);
        when(artigos.findByAutorId(1L)).thenReturn(List.of(
                artigo(10L, "Primeiro", true, autor),
                artigo(11L, "Rascunho", false, autor)));

        mvc.perform(get("/api/autores/1/artigos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Primeiro"))
                .andExpect(jsonPath("$[0].autor.id").value(1));
    }

    @Test
    void filtraApenasPublicadosQuandoSolicitado() throws Exception {
        Autor autor = new Autor(1L, "Maria", "maria@example.com");
        when(autores.existsById(1L)).thenReturn(true);
        when(artigos.findByAutorIdAndPublicado(1L, true))
                .thenReturn(List.of(artigo(10L, "Primeiro", true, autor)));

        mvc.perform(get("/api/autores/1/artigos").param("apenasPublicados", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].publicado").value(true));
        verify(artigos, never()).findByAutorId(anyLong());
    }

    @Test
    void retornaNoContentQuandoAutorNaoTemArtigos() throws Exception {
        when(autores.existsById(1L)).thenReturn(true);
        when(artigos.findByAutorId(1L)).thenReturn(List.of());

        mvc.perform(get("/api/autores/1/artigos"))
                .andExpect(status().isNoContent());
    }

    @Test
    void retornaNotFoundQuandoAutorNaoExiste() throws Exception {
        when(autores.existsById(99L)).thenReturn(false);

        mvc.perform(get("/api/autores/99/artigos"))
                .andExpect(status().isNotFound());
        verifyNoInteractions(artigos);
    }
}

package dw.editora.control;

import dw.editora.entity.Artigo;
import dw.editora.entity.Autor;
import dw.editora.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/autores")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Autor autor) {
        try {
            Autor novoAutor = autorService.salvar(autor);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoAutor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Autor>> listarTodos() {
        List<Autor> autores = autorService.buscarTodos();
        return ResponseEntity.ok(autores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> buscarPorId(@PathVariable Long id) {
        return autorService.buscarPorId(id)
                .map(autor -> ResponseEntity.ok(autor))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{id}/artigos")
    public ResponseEntity<List<Artigo>> listarArtigosDoAutor(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean apenasPublicados) {
        Optional<List<Artigo>> artigos = autorService.buscarArtigosDoAutor(id, apenasPublicados);
        if (artigos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (artigos.get().isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(artigos.get());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean deletado = autorService.deletar(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
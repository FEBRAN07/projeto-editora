package dw.editora.service;

import dw.editora.entity.Artigo;
import dw.editora.entity.Autor;
import dw.editora.repository.ArtigoRepository;
import dw.editora.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    @Autowired
    private ArtigoRepository artigoRepository;

    public Autor salvar(Autor autor) {
        if (autorRepository.existsByEmail(autor.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }
        return autorRepository.save(autor);
    }

    public List<Autor> buscarTodos() {
        return autorRepository.findAll();
    }

    public Optional<Autor> buscarPorId(Long id) {
        return autorRepository.findById(id);
    }

    // Optional vazio = autor inexistente (o Controller transforma em 404).
    // Se "apenasPublicados" for true, filtra pelo status publicado = true.
    public Optional<List<Artigo>> buscarArtigosDoAutor(Long id, boolean apenasPublicados) {
        if (!autorRepository.existsById(id)) {
            return Optional.empty();
        }
        if (apenasPublicados) {
            return Optional.of(artigoRepository.findByAutorIdAndPublicado(id, true));
        }
        return Optional.of(artigoRepository.findByAutorId(id));
    }

    public boolean deletar(Long id) {
        if (autorRepository.existsById(id)) {
            autorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
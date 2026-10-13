package dw.editora.service;

import dw.editora.entity.Autor;
import dw.editora.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

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

    public boolean deletar(Long id) {
        if (autorRepository.existsById(id)) {
            autorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
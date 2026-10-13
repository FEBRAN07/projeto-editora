package dw.editora.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import dw.editora.entity.Artigo;
import dw.editora.repository.ArtigoRepository;
import dw.editora.repository.AutorRepository;

// @Service diz ao Spring que esta classe contém a lógica de negócio
@Service
public class ArtigoService {

    private final ArtigoRepository repository;
    private final AutorRepository autorRepository;

    public ArtigoService(ArtigoRepository repository, AutorRepository autorRepository) {
        this.repository = repository;
        this.autorRepository = autorRepository;
    }

    public List<Artigo> listarArtigos(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            return repository.findAll();
        }
        return repository.findByTituloContainingIgnoreCase(titulo);
    }

    public Optional<Artigo> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Artigo salvar(Artigo artigo) {
        if (artigo.getAutor() == null || artigo.getAutor().getId() == null) {
            throw new IllegalArgumentException("Informe o autor do artigo no formato: autor: { id: ID_DO_AUTOR }.");
        }
        artigo.setAutor(autorRepository.findById(artigo.getAutor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Autor não encontrado. Cadastre o autor antes de criar o artigo.")));
        return repository.save(artigo);
    }

    public Optional<Artigo> atualizar(Long id, Artigo artigoAtualizado) {
        return repository.findById(id).map(artigo -> {
            artigo.setTitulo(artigoAtualizado.getTitulo());
            artigo.setResumo(artigoAtualizado.getResumo());
            artigo.setPublicado(artigoAtualizado.isPublicado());
            return repository.save(artigo);
        });
    }

    public boolean deletar(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public void deletarTodos() {
        repository.deleteAll();
    }

    public List<Artigo> listarPublicados() {
        return repository.findByPublicado(true);
    }
}

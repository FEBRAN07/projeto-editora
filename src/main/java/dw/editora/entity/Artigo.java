package dw.editora.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "artigo")
@Getter // Lombok: Gera todos os getters
@Setter // Lombok: Gera todos os setters
@NoArgsConstructor // Lombok: Construtor vazio exigido pelo JPA
@AllArgsConstructor // Lombok: Construtor com todos os argumentos
@ToString // Lombok: Gera o método toString()
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Boa prática: equals/hashCode apenas no ID
public class Artigo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Usa apenas o ID para comparar se dois objetos são o mesmo registro
    private Long id; // Boa prática: usar Long (objeto) ao invés de long (primitivo)
    
    @Column(nullable = false, length = 80)
    private String titulo;
    
    @Column(nullable = false)
    private String resumo;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE") 
    private boolean publicado = true; // Já inicializamos como true no objeto

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;
}
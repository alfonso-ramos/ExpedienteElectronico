package rmp.expediente_electronico.modelo;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "pacientes")
public class Paciente {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "idPaciente")
    private Integer idPaciente;
    private String matricula;
    private String nombres;
    private String apellidos;
    private String programaAcademico;
    private Integer edad;
    private String sexo;

    @Override
    public String toString(){
        return matricula.concat(" - ".concat(nombres).concat(" ".concat(apellidos)));
    }
}

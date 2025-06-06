package rmp.expediente_electronico.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Paciente {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "idPaciente")
    private Integer idPaciente;
    private String matricula;
    private String nombres;
    private String apellidos;
    private String programaAcademico;
    private String fechaNacimiento;
}

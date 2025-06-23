package rmp.expediente_electronico.modelo;


import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Consulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idConsulta")
    private Integer idPaciente;
    private Integer edad;
    private String diagnostico;
    private String medicamentos;
    private String observaciones;
    private Integer talla;
    private float altura;
    private float peso;
    private float imc;
    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;
}

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
@Table (name = "consultas")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idConsulta;

    @ManyToOne
    @JoinColumn(name = "idPaciente", referencedColumnName = "idPaciente")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "idDiagnostico", referencedColumnName = "idDiagnostico")
    private Diagnostico diagnosticoKey;

    private String diagnostico;
    private String medicamento;
    private Date fechaReg;
    private String observaciones;
    private String talla;
    private Float altura;
    private Float peso;
    private Float imc;
    private String imc_estado;
    private Integer edad;

}

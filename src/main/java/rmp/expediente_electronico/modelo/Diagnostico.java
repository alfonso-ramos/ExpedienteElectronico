package rmp.expediente_electronico.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "diagnosticos")
public class Diagnostico {

    @Id
    @GeneratedValue
    private Integer idDiagnostico;

    private String diagnostico;

    @Override
    public String toString(){
        return diagnostico;
    }
}

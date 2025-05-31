package Modelo;

import java.util.Objects;

public class Consulta {
    private int idConsulta;
    private int idPaciente;
    private String diagnostico;
    private String medicamento;
    private String fechaReg;
    private String observaciones;
    private String talla;
    private float altura;
    private float peso;
    private float imc;
    private String imc_estado;
    private int edad;

    public Consulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public Consulta(int idConsulta, int idPaciente, String diagnostico, String medicamento, String fechaReg, String observaciones, String talla, float altura, float peso, float imc, String imc_estado, int edad) {
        this.idConsulta = idConsulta;
        this.idPaciente = idPaciente;
        this.diagnostico = diagnostico;
        this.medicamento = medicamento;
        this.fechaReg = fechaReg;
        this.observaciones = observaciones;
        this.talla = talla;
        this.altura = altura;
        this.peso = peso;
        this.imc = imc;
        this.imc_estado = imc_estado;
        this.edad = edad;
    }

    public int getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(int idConsulta) {
        this.idConsulta = idConsulta;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(String medicamento) {
        this.medicamento = medicamento;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getFechaReg() {
        return fechaReg;
    }

    public void setFechaReg(String fechaReg) {
        this.fechaReg = fechaReg;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
    }

    public float getpeso() {
        return peso;
    }

    public void setpeso(float peso) {
        this.peso = peso;
    }

    public float getImc() {
        return imc;
    }

    public void setImc(float imc) {
        this.imc = imc;
    }

    public String getImc_estado() {
        return imc_estado;
    }

    public void setImc_estado(String imc_estado) {
        this.imc_estado = imc_estado;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Consulta consulta = (Consulta) o;
        return idConsulta == consulta.idConsulta && idPaciente == consulta.idPaciente && Float.compare(altura, consulta.altura) == 0 && Float.compare(peso, consulta.peso) == 0 && Float.compare(imc, consulta.imc) == 0 && edad == consulta.edad && Objects.equals(diagnostico, consulta.diagnostico) && Objects.equals(medicamento, consulta.medicamento) && Objects.equals(fechaReg, consulta.fechaReg) && Objects.equals(observaciones, consulta.observaciones) && Objects.equals(talla, consulta.talla) && Objects.equals(imc_estado, consulta.imc_estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idConsulta, idPaciente, diagnostico, medicamento, fechaReg, observaciones, talla, altura, peso, imc, imc_estado, edad);
    }

    @Override
    public String toString() {
        return "Consulta{" +
                "idConsulta=" + idConsulta +
                ", idPaciente=" + idPaciente +
                ", diagnostico='" + diagnostico + '\'' +
                ", medicamento='" + medicamento + '\'' +
                ", fechaReg='" + fechaReg + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", talla='" + talla + '\'' +
                ", altura=" + altura +
                ", peso=" + peso +
                ", imc=" + imc +
                ", imc_estado='" + imc_estado + '\'' +
                ", edad=" + edad +
                '}';
    }
}

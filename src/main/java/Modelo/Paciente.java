package Modelo;

import java.util.List;
import java.util.Objects;

public class Paciente {
    private int idPaciente;
    private String matricula;
    private String nombres;
    private String apellidos;
    private String programaAcademico;
    private String fechaNacimiento;

    public Paciente(int idPaciente){
        this.idPaciente = idPaciente;
    }

    public Paciente(int idPaciente, String fechaNacimiento, String programaAcademico, String apellidos, String nombres, String matricula) {
        this.idPaciente = idPaciente;
        this.fechaNacimiento = fechaNacimiento;
        this.programaAcademico = programaAcademico;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.matricula = matricula;
    }

    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getProgramaAcademico() {
        return programaAcademico;
    }

    public void setProgramaAcademico(String programaAcademico) {
        this.programaAcademico = programaAcademico;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Paciente that = (Paciente) o;
        return idPaciente == that.idPaciente && Objects.equals(matricula, that.matricula) && Objects.equals(nombres, that.nombres) && Objects.equals(apellidos, that.apellidos) && Objects.equals(programaAcademico, that.programaAcademico) && Objects.equals(fechaNacimiento, that.fechaNacimiento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPaciente, matricula, nombres, apellidos, programaAcademico, fechaNacimiento);
    }

    @Override
    public String toString() {
        return "PacienteModelo{" +
                "idPaciente=" + idPaciente +
                ", matricula='" + matricula + '\'' +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", programaAcademico='" + programaAcademico + '\'' +
                ", fechaNacimiento='" + fechaNacimiento + '\'' +
                '}';
    }
}

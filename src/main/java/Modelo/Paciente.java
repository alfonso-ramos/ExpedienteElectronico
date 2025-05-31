package Modelo;

import java.awt.image.PackedColorModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static config.Database.getConnection;

/**
 * Modelo de la tabla paciente
 */
public class Paciente {
    private int idPaciente;
    private String matricula;
    private String nombres;
    private String apellidos;
    private String programaAcademico;
    private String fechaNacimiento;

    /**
     * Constructor vacio de paciente
     */
    public Paciente(){}

    /**
     * Constructor de paciente por id
     * @param idPaciente
     */
    public Paciente(int idPaciente){
        this.idPaciente = idPaciente;
    }

    /**
     * Constructor de paciente con todos los parametros (sin id)
     * @param fechaNacimiento
     * @param programaAcademico
     * @param apellidos
     * @param nombres
     * @param matricula
     */
    public Paciente(String fechaNacimiento, String programaAcademico, String apellidos, String nombres, String matricula) {
        this.fechaNacimiento = fechaNacimiento;
        this.programaAcademico = programaAcademico;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.matricula = matricula;
    }

    // Getters y setters

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

    // Metodos consulta

    /**
     * lista todos los pacientes de la lista
     * @return ArrayList de pacientes
     */
    public List<Paciente> listarPacientes(){
        List<Paciente> pacientes = new ArrayList<>();
        PreparedStatement ps;
        ResultSet rs;

        // Conexion a la base de datos
        Connection conexion = getConnection();

        var sql = "SELECT * FROM pacientes ORDER BY idPaciente;";
        try{
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()){
                var paciente = new Paciente();

                paciente.setMatricula(rs.getString("matricula"));
                paciente.setIdPaciente(rs.getInt("idPaciente"));
                paciente.setNombres(rs.getString("nombres"));
                paciente.setApellidos(rs.getString("apellidos"));
                paciente.setProgramaAcademico(rs.getString("programaAcademico"));
                paciente.setFechaNacimiento(rs.getString("fechaNacimiento"));

                pacientes.add(paciente);
            }
        } catch (Exception e) {
            System.out.println("error al listar pacientes: "+e.getMessage());
        }
        finally {
            try{
                conexion.close();
            }catch (SQLException e){
                System.out.println("error al cerrar la conexion");
            }
        }

        return pacientes;
    }

    /**
     * busca un paciente por id
     * @param paciente idPaciente
     * @return true en caso de exito, false en caso contrario
     */
    public boolean buscarPacientePorId(Paciente paciente){
        PreparedStatement ps;
        ResultSet rs;

        // Conexion a la base de datos
        Connection conexion = getConnection();

        var sql = "SELECT * FROM pacientes WHERE idPaciente = ?;";
        try{
            ps = conexion.prepareStatement(sql);
            ps.setInt(1,paciente.getIdPaciente());
            rs = ps.executeQuery();

            if(rs.next()){
                paciente.setMatricula(rs.getString("matricula"));
                paciente.setIdPaciente(rs.getInt("idPaciente"));
                paciente.setNombres(rs.getString("nombres"));
                paciente.setApellidos(rs.getString("apellidos"));
                paciente.setFechaNacimiento(rs.getString("fechaNacimiento"));

                return true;
            }
        }catch (Exception e){
            System.out.println("error al buscar paciente por id");
        }
        finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("error al cerrar la conexion");
            }
        }
        return false;
    }

    /**
     * agrega un paciente a la base de datos
     * @param paciente (matricula, nombres, apellidos, programaAcademico, fechaNacimiento)
     * @return true en caso de exito, falso en caso contrario
     */
    public boolean agregarPaciente(Paciente paciente){
        PreparedStatement ps;
        var sql = "INSERT INTO pacientes (matricula, nombres, apellidos, programaAcademico, fechaNacimiento) values (?,?,?,?,?);";

        // Conexion a la base de datos
        var conexion = getConnection();

        try{
            ps = conexion.prepareStatement(sql);
            ps.setString(1,paciente.getMatricula());
            ps.setString(2,paciente.getNombres());
            ps.setString(3,paciente.getApellidos());
            ps.setString(4,paciente.getProgramaAcademico());
            ps.setString(5,paciente.getFechaNacimiento());

            ps.execute();
            return true;

        } catch (Exception e) {
            System.out.println("error al agregar paciente");
        }
        finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("error al cerrar la conexion");
            }
        }
        return false;
    }

    /**
     * modifica un paciente de la base de datos
     * @param paciente (idPaciente, matricula, nombres, apellidos, programaAcademico, fecha,Nacimiento)
     * @return true en caso de exito, false en caso contrario
     */
    public boolean modificarPaciente(Paciente paciente){
        PreparedStatement ps;
        var sql = "UPDATE pacientes SET matricula = ?, nombres = ?, apellidos = ?, programaAcademico = ?, fechaNacimiento = ? WHERE idPaciente = ?;";

        // Conexion a la base de datos
        Connection conexion = getConnection();

        try{
            ps = conexion.prepareStatement(sql);
            ps.setString(1,paciente.getMatricula());
            ps.setString(2,paciente.getNombres());
            ps.setString(3,paciente.getApellidos());
            ps.setString(4,paciente.getProgramaAcademico());
            ps.setString(5,paciente.getFechaNacimiento());
            ps.setInt(6,paciente.getIdPaciente());

            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println("error al modificar cliente");
        }
        finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("error al cerrar la conexion");
            }
        }
        return false;
    }

    /**
     * borra un paciente de la base de datos por id
     * @param paciente (idPaciente)
     * @return true en caso de exito, false en caso contrario
     */
    public boolean borrarPaciente(Paciente paciente){
        PreparedStatement ps;
        boolean encontrado = paciente.buscarPacientePorId(paciente);
        if(encontrado) {
            var sql = "DELETE FROM pacientes WHERE idPaciente = ?;";

            // Conexion a la base de datos
            var conexion = getConnection();
            try {
                ps = conexion.prepareStatement(sql);
                ps.setInt(1,paciente.getIdPaciente());
                ps.execute();
                return true;
            } catch (SQLException e) {
                System.out.println("error al borrar paciente: "+e.getMessage());
            }
            finally{
                try {
                    conexion.close();
                } catch (SQLException e) {
                    System.out.println("error al cerrar la conexion");
                }
            }
        }
        return false;
    }

    // equals

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Paciente that = (Paciente) o;
        return idPaciente == that.idPaciente && Objects.equals(matricula, that.matricula) && Objects.equals(nombres, that.nombres) && Objects.equals(apellidos, that.apellidos) && Objects.equals(programaAcademico, that.programaAcademico) && Objects.equals(fechaNacimiento, that.fechaNacimiento);
    }

    // hash

    @Override
    public int hashCode() {
        return Objects.hash(idPaciente, matricula, nombres, apellidos, programaAcademico, fechaNacimiento);
    }

    // toString

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

    public static void main(String[] args) {
        /*
        Paciente paciente = new Paciente(1);
        paciente.buscarPacientePorId(paciente);

        System.out.println(paciente.toString());
         */

        /*
        Paciente paciente = new Paciente("2005-06-20","tecnologias","ramos","poncho","2022030666");
        if(paciente.agregarPaciente(paciente)){
            System.out.println("agregado con exito");
        }
         */

        /*
        Paciente paciente = new Paciente("2005-08-21","tecnologias","venoso","ruin","2023030777");
        paciente.setIdPaciente(3);
        paciente.modificarPaciente(paciente);
         */

        Paciente paciente = new Paciente(5);
        paciente.borrarPaciente(paciente);

        List<Paciente> pacientes;
        pacientes = paciente.listarPacientes();

        pacientes.forEach(System.out::println);
    }
}

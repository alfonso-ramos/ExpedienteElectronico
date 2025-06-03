package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static config.Database.getConnection;

public class PacienteDAO {
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
        boolean encontrado = buscarPacientePorId(paciente);
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

}

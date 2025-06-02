package Modelo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static config.Database.getConnection;

public class ConsultaDAO {

    /**
     * agregar consultas a la base de datos
     * @param consulta - consulta a agregar
     * @return true en caso de exito, false en caso contrario
     */
    public boolean agregarConsulta(Consulta consulta) {
        PreparedStatement ps;
        String sql = "INSERT INTO consultas (idPaciente, diagnostico, medicamento, fechaReg, observaciones, talla, altura, peso, imc, imc_estado, edad) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        var conexion = getConnection();

        try {
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, consulta.getIdPaciente());
            ps.setString(2, consulta.getDiagnostico());
            ps.setString(3, consulta.getMedicamento());
            ps.setString(4, consulta.getFechaReg());
            ps.setString(5, consulta.getObservaciones());
            ps.setString(6, consulta.getTalla());
            ps.setFloat(7, consulta.getAltura());
            ps.setFloat(8, consulta.getpeso());
            ps.setFloat(9, consulta.getImc());
            ps.setString(10, consulta.getImc_estado());
            ps.setInt(11, consulta.getEdad());

            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println("Error al agregar consulta: " + e.getMessage());
        } finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * modifica una consulta por su id
     * @param consulta - consulta con datos nuevos
     * @return true en caso de exito, false en caso contrario
     */
    public boolean modificarConsulta(Consulta consulta) {
        PreparedStatement ps;
        String sql = "UPDATE consultas SET idPaciente=?, diagnostico=?, medicamento=?, fechaReg=?, observaciones=?, talla=?, altura=?, peso=?, imc=?, imc_estado=?, edad=? WHERE idConsulta=?";
        var conexion = getConnection();

        try {
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, consulta.getIdPaciente());
            ps.setString(2, consulta.getDiagnostico());
            ps.setString(3, consulta.getMedicamento());
            ps.setString(4, consulta.getFechaReg());
            ps.setString(5, consulta.getObservaciones());
            ps.setString(6, consulta.getTalla());
            ps.setFloat(7, consulta.getAltura());
            ps.setFloat(8, consulta.getpeso());
            ps.setFloat(9, consulta.getImc());
            ps.setString(10, consulta.getImc_estado());
            ps.setInt(11, consulta.getEdad());
            ps.setInt(12, consulta.getIdConsulta());

            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println("Error al modificar consulta: " + e.getMessage());
        } finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * elimina una consulta de la base de datos
     * @param consulta -consulta a eliminar
     * @return true en caso de exito, false en caso contrario
     */
    public boolean eliminarConsulta(Consulta consulta) {
        PreparedStatement ps;
        String sql = "DELETE FROM consultas WHERE idConsulta=?";
        var conexion = getConnection();

        try {
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, consulta.getIdConsulta());
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println("Error al eliminar consulta: " + e.getMessage());
        } finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * busca una consulta por su id
     * @param consulta - consulta con id
     * @return Consulta con los datos en caso de exito, null en caso contrario
     */
    public Consulta buscarConsultaPorId(Consulta consulta) {
        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM consultas WHERE idConsulta=?";
        var conexion = getConnection();

        try {
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, consulta.getIdConsulta());
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Consulta(
                        rs.getInt("idConsulta"),
                        rs.getInt("idPaciente"),
                        rs.getString("diagnostico"),
                        rs.getString("medicamento"),
                        rs.getString("fechaReg"),
                        rs.getString("observaciones"),
                        rs.getString("talla"),
                        rs.getFloat("altura"),
                        rs.getFloat("peso"),
                        rs.getFloat("imc"),
                        rs.getString("imc_estado"),
                        rs.getInt("edad")
                );
            }
        } catch (Exception e) {
            System.out.println("Error al buscar consulta por ID: " + e.getMessage());
        } finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * lista todas las consultas de la base de datos
     * @return List<Consulta> con todas las consutlas
     */
    public List<Consulta> listarConsultas() {
        PreparedStatement ps;
        ResultSet rs;
        String sql = "SELECT * FROM consultas";
        var conexion = getConnection();
        List<Consulta> lista = new ArrayList<>();

        try {
            ps = conexion.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Consulta consulta = new Consulta(
                        rs.getInt("idConsulta"),
                        rs.getInt("idPaciente"),
                        rs.getString("diagnostico"),
                        rs.getString("medicamento"),
                        rs.getString("fechaReg"),
                        rs.getString("observaciones"),
                        rs.getString("talla"),
                        rs.getFloat("altura"),
                        rs.getFloat("peso"),
                        rs.getFloat("imc"),
                        rs.getString("imc_estado"),
                        rs.getInt("edad")
                );
                lista.add(consulta);
            }
        } catch (Exception e) {
            System.out.println("Error al listar consultas: " + e.getMessage());
        } finally {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }

        return lista;
    }

}


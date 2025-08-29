package rmp.expediente_electronico.gui;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Component
public class ExpElec_Main extends JFrame{

    private JPanel panelPrincipal;
    private JButton pacientesButton;
    private JButton consultasButton;
    private JButton generarReporteButton;
    private JTable table1;

    private ExpElec_PacientesForma pacientesForma;

    @Autowired
    public ExpElec_Main(ExpElec_PacientesForma pacientesForma) {
        this.pacientesForma = pacientesForma;
        pacientesForma.setVistaPrincipal(this);
        pacientesButton.addActionListener(actionEvent -> pacientes());
        iniciarForma();
    }

    private void iniciarForma(){
        setTitle("Expediente Electronico");
        setContentPane(panelPrincipal);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void pacientes(){
        this.setVisible(false);
        pacientesForma.setVisible(true);
    }

}
/*

private VistaPaciente vistaPaciente;
    private VistaConsultas vistaConsultas;
    private ConsultaServicio consultaServicio;
    private DefaultTableModel tablaModelo;

    @Autowired
    public VistaMain(VistaConsultas vistaConsultas, VistaPaciente vistaPaciente, ConsultaServicio consultaServicio) {
        this.vistaConsultas = vistaConsultas;
        this.vistaPaciente = vistaPaciente;
        this.consultaServicio = consultaServicio;


        initComponents();
    }

    public void iniciarTabla(){

        this.tablaModelo = new DefaultTableModel(0, 12){
            @Override
            public boolean isCellEditable(int row,int column){return false;}
        };

        String[] nombresColumnas = {"Id","Paciente","Diagnostico","Medicamento","Fecha de registro","Observaciones","Talla","Imc","Estado","Edad"};

        this.tablaModelo.setColumnIdentifiers(nombresColumnas);
        this.tabla.setModel(tablaModelo);
        this.tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        listar();
    }

    public void listar(){
        this.tablaModelo.setRowCount(0);

        List<Consulta> consultas = consultaServicio.listarConsultas();

        consultas.forEach(consulta -> {

            Object[] renglon ={
                    consulta.getIdConsulta(),
                    consulta.getPaciente(),
                    consulta.getDiagnostico(),
                    consulta.getMedicamento(),
                    consulta.getFechaReg(),
                    consulta.getObservaciones(),
                    consulta.getTalla(),
                    consulta.getAltura(),
                    consulta.getPeso(),
                    consulta.getImc(),
                    consulta.getImc_estado(),
                    consulta.getEdad()
            };
            tablaModelo.addRow(renglon);
        });
    }
 */

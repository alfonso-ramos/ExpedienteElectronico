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

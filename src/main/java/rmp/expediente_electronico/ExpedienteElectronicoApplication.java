package rmp.expediente_electronico;

import com.formdev.flatlaf.FlatLightLaf;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import rmp.expediente_electronico.gui.VistaMain;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class ExpedienteElectronicoApplication {
	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load(); // carga el archivo .env
		// Opcional: setear variables de entorno en tiempo de ejecución
		System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
		System.setProperty("DATABASE_USER", dotenv.get("DATABASE_USER"));
		System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));

		FlatLightLaf.setup();

		final JWindow splash = crearSplash();
		splash.setVisible(true);

		// Instanciar fabrica de Spring
		ConfigurableApplicationContext contextoSpring =
				new SpringApplicationBuilder(ExpedienteElectronicoApplication.class)
						.headless(false)
						.web(WebApplicationType.NONE)
						.run(args);
		// Crear objeto de Swing
		SwingUtilities.invokeLater(() -> {
			VistaMain vistaMain = contextoSpring.getBean(VistaMain.class);
			vistaMain.setLocationRelativeTo(null);
			vistaMain.setVisible(true);
			splash.setVisible(false);
			splash.dispose();
		});
		
	}

	private static JWindow crearSplash(){
		JWindow splash = new JWindow();
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
		panel.setBackground(Color.WHITE);

		JLabel titulo = new JLabel("Expediente electronico - UPSIN", SwingConstants.CENTER);
		Font fuente = titulo.getFont().deriveFont(Font.BOLD, 20f);
		titulo.setFont(fuente);
		panel.add(titulo, BorderLayout.CENTER);

		splash.getContentPane().add(panel);
		splash.setSize(400, 200);
		splash.setLocationRelativeTo(null);
		return splash;
	}

}

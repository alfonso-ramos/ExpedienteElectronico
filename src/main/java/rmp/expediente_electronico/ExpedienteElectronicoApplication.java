package rmp.expediente_electronico;

import com.formdev.flatlaf.FlatLightLaf;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import rmp.expediente_electronico.gui.ExpElec_Main;
import rmp.expediente_electronico.gui.ExpElec_PacientesForma;
import rmp.expediente_electronico.gui.VistaMain;

import javax.swing.*;

@SpringBootApplication
public class ExpedienteElectronicoApplication {
	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load(); // carga el archivo .env
		// Opcional: setear variables de entorno en tiempo de ejecución
		System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
		System.setProperty("DATABASE_USER", dotenv.get("DATABASE_USER"));
		System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));

		FlatLightLaf.setup();
		// Instanciar fabrica de Spring
		ConfigurableApplicationContext contextoSpring =
				new SpringApplicationBuilder(ExpedienteElectronicoApplication.class)
						.headless(false)
						.web(WebApplicationType.NONE)
						.run(args);
		// Crear objeto de Swing
		SwingUtilities.invokeLater(() -> {
			VistaMain vistaMain = contextoSpring.getBean(VistaMain.class);
			vistaMain.setVisible(true);
		});
		
	}

}

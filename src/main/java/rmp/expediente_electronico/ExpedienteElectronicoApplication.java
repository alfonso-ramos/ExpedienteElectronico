package rmp.expediente_electronico;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import rmp.expediente_electronico.gui.ExpElec_PacientesForma;
import rmp.expediente_electronico.servicio.IPacienteServicio;

import javax.swing.*;

@SpringBootApplication
public class ExpedienteElectronicoApplication {

	@Autowired
	private IPacienteServicio pacienteServicio;
	
	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.load(); // carga el archivo .env
		// Opcional: setear variables de entorno en tiempo de ejecución
		System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
		System.setProperty("DATABASE_USER", dotenv.get("DATABASE_USER"));
		System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));

		// Instanciar fabrica de Spring
		ConfigurableApplicationContext contextoSpring =
				new SpringApplicationBuilder(ExpedienteElectronicoApplication.class)
						.headless(false)
						.web(WebApplicationType.NONE)
						.run(args);
		// Crear objeto de Swing
		SwingUtilities.invokeLater(() -> {
			ExpElec_PacientesForma expElecPacientesForma = contextoSpring.getBean(ExpElec_PacientesForma.class);
			expElecPacientesForma.setVisible(true);
		});
		
		SpringApplication.run(ExpedienteElectronicoApplication.class, args);
	}

}

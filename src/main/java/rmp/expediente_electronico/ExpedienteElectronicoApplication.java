package rmp.expediente_electronico;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExpedienteElectronicoApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load(); // carga el archivo .env

		// Opcional: setear variables de entorno en tiempo de ejecución
		System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
		System.setProperty("DATABASE_USER", dotenv.get("DATABASE_USER"));
		System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));

		SpringApplication.run(ExpedienteElectronicoApplication.class, args);
	}

}

package config;

import java.sql.Connection;
import java.sql.DriverManager;
import io.github.cdimascio.dotenv.Dotenv;

// Conexion a la base de datos
public class Database {

    // realizar la conexion
    public static Connection getConnection(){
        Connection database = null;
        Dotenv dotenv = Dotenv.load();

        //    variables de conexion bd
        String baseDatos = dotenv.get("DATABASE");
        String port = dotenv.get("DATABASE_PORT");
        String url = dotenv.get("DATABASE_URL")+port+"/"+baseDatos;
        String usuario = dotenv.get("DATABASE_USER");
        String password = dotenv.get("DATABASE_PASSWORD");

        // try conexion a la base de datos
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            database = DriverManager.getConnection(url, usuario, password);
        }catch (Exception e){
            System.out.println("error al conectarse a la base de datos: "+e);
        }

        return database;
    }

    // main para verificar conexion
    /*
    public static void main(String[] args) {
        var database = Database.getConnection();

        if(database!=null){
            System.out.println("Conexion exitosa");
        }else{
            System.out.println("Conexion fallida");
        }
    }
    */
}

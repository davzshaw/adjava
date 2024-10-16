package odioscala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Timestamp;

public class App {
  
  public static void main(String[] args) {
    String url = "jdbc:mysql://autorack.proxy.rlwy.net:19134/railway";
    String driver = "com.mysql.cj.jdbc.Driver";
    String username = "root";
    String password = "gWngYwjdEGinrtWiDZgHtgVPbEsWvfHj";
    Connection connection = null;

    try {
        Class.forName(driver);
        connection = DriverManager.getConnection(url, username, password);
        Statement statement = connection.createStatement();

        // Crear un producto
        //Database.crearProducto("Pelota de goma", "Juguetes", 5.99, statement);
        
        //Database.crearProducto("Raqueta de tenis", "Juguetes", 29.99, statement);
        
        //Database.crearProducto("Galletas de avena", "Comida", 3.49, statement);
        
        //Database.crearProducto("Te macha", "Comida", 9.99, statement);
        
        //Database.crearProducto("Sofa", "Hogar", 12.99, statement);
        
        //Database.crearProducto("Televisor", "Hogar", 1.99, statement);

        // Crear un usuario
        //Database.crearUsuario("Juan Perez", "  JUan.PereZ@GmAIl.com  ", statement);

        //Database.crearUsuario("maria fernanda Lopez ", "m.lopez@hotmail.com", statement);

        //Database.crearUsuario("Carlos Gomez", "carlos.gomez@example.com", statement);

        //Database.crearUsuario("Ana", "ana123@gmail.com", statement);

        //Database.crearUsuario("Luz Morales", "luz.morales+promo@gmail.com", statement);

        // Comprar un producto
        //Database.comprarProducto(2, 1, new Timestamp(System.currentTimeMillis()), statement);

        //Database.comprarProducto(3, 1, Timestamp.valueOf("2023-09-15 14:30:00"), statement);

        //Database.comprarProducto(2, 1, Timestamp.valueOf("2024-01-10 10:00:00"), statement);

        //Database.comprarProducto(4, 2, new Timestamp(System.currentTimeMillis()), statement);

        //Database.comprarProducto(5, 1, Timestamp.valueOf("2024-12-25 12:00:00"), statement);

        Database.recomendarProductosUsuario(1, statement);






    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

}

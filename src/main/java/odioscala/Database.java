package odioscala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

public class Database {

    public static void crearProducto(String nombre, String categoria, double precio, Statement statement) {
        try {
            String query = String.format("INSERT INTO Producto (nombre, categoria, precio) VALUES ('%s', '%s', %f)", nombre, categoria, precio);
            int rowsInserted = statement.executeUpdate(query);
            if (rowsInserted > 0) {
                System.out.println(rowsInserted + " fila(s) insertada(s) correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void comprarProducto(int id_producto, int id_usuario, Timestamp fecha, Statement statement) {
        try {
            String query = String.format("INSERT INTO Compra (fecha, Usuario_id, Producto_id) VALUES ('%s', %d, %d)", fecha, id_usuario, id_producto);
            int rowsInserted = statement.executeUpdate(query);
            if (rowsInserted > 0) {
                System.out.println(rowsInserted + " fila(s) insertada(s) correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void crearUsuario(String nombre, String email, Statement statement) {
        try {
            String nombre_normalizado = nombre.trim().replaceAll("\\s+", " ").toLowerCase();
            String email_normalizado = email.trim().toLowerCase();
            String query = String.format("INSERT INTO Usuario (nombre, email) VALUES ('%s', '%s')", nombre_normalizado, email_normalizado);
            int rowsInserted = statement.executeUpdate(query);
            if (rowsInserted > 0) {
                System.out.println(rowsInserted + " fila(s) insertada(s) correctamente.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void actualizarProducto(int id, String nombre, String categoria, double precio, Statement statement) {
        try {
            String query = String.format("UPDATE Producto SET nombre = '%s', categoria = '%s', precio = %f WHERE id = %d", nombre, categoria, precio, id);
            int rowsUpdated = statement.executeUpdate(query);
            if (rowsUpdated > 0) {
                System.out.println("Producto con id " + id + " actualizado correctamente.");
            } else {
                System.out.println("No se encontró ningún producto con id " + id + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void eliminarProducto(int id, Statement statement) {
        try {
            String query = String.format("DELETE FROM Producto WHERE id = %d", id);
            int rowsDeleted = statement.executeUpdate(query);
            if (rowsDeleted > 0) {
                System.out.println("Producto con id " + id + " eliminado correctamente.");
            } else {
                System.out.println("No se encontró ningún producto con id " + id + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void historialComprasUsuario(int id, Statement statement) {
        try {
            String query = String.format("SELECT p.nombre FROM Compra c JOIN Producto p on c.Producto_id = p.id WHERE c.Usuario_id = %d", id);
            ResultSet rowsSelected = statement.executeQuery(query);
            while (rowsSelected.next()) {
                String nombreP = rowsSelected.getString("nombre");
                System.out.println("Producto: " + nombreP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void recomendarProductosUsuario(int id, Statement statement) {
        try {
            String query = String.format("SELECT p.categoria FROM Compra c JOIN Producto p on c.Producto_id = p.id WHERE c.Usuario_id = %d GROUP BY p.categoria HAVING count(*) >= 2", id);
            ResultSet rowsSelected = statement.executeQuery(query);
            StringBuilder categorias = new StringBuilder();
            while (rowsSelected.next()) {
                if (categorias.length() > 0) {
                    categorias.append(",");
                }
                categorias.append("'").append(rowsSelected.getString("categoria")).append("'");
            }

            if (categorias.length() > 0) {
                String productoQuery = String.format("SELECT * FROM Producto WHERE categoria IN (%s)", categorias.toString());
                ResultSet productosResult = statement.executeQuery(productoQuery);
                System.out.println("Productos recomendados para ti:");
                while (productosResult.next()) {
                    String productoNombre = productosResult.getString("nombre");
                    String productoCategoria = productosResult.getString("categoria");
                    double productoPrecio = productosResult.getDouble("precio");
                    System.out.println(String.format("Producto: %s, Categoria: %s, Precio: %f", productoNombre, productoCategoria, productoPrecio));
                }
            } else {
                System.out.println("No se encontraron categorías compradas más de 2 veces por el usuario con id " + id + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void crearTablas(Statement statement) {
        try {
            // Tabla Usuario
            String crearTablaUsuario = """
                CREATE TABLE IF NOT EXISTS Usuario (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100),
                    email VARCHAR(100) UNIQUE
                );
            """;
            statement.executeUpdate(crearTablaUsuario);

            // Tabla Producto
            String crearTablaProducto = """
                CREATE TABLE IF NOT EXISTS Producto (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100),
                    categoria VARCHAR(100),
                    precio DECIMAL(10, 2)
                );
            """;
            statement.executeUpdate(crearTablaProducto);

            // Tabla Compra
            String crearTablaCompra = """
                CREATE TABLE IF NOT EXISTS Compra (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    fecha TIMESTAMP,
                    Usuario_id INT,
                    Producto_id INT,
                    FOREIGN KEY (Usuario_id) REFERENCES Usuario(id) ON DELETE CASCADE,
                    FOREIGN KEY (Producto_id) REFERENCES Producto(id) ON DELETE CASCADE
                );
            """;
            statement.executeUpdate(crearTablaCompra);

            System.out.println("Tablas creadas correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package odioscala;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            String queryCompras = String.format("SELECT c.Producto_id FROM Compra c WHERE c.Usuario_id = %d", id);
            ResultSet comprasResult = statement.executeQuery(queryCompras);

            List<Integer> productosCompradosIds = new ArrayList<>();

            while (comprasResult.next()) {
                productosCompradosIds.add(comprasResult.getInt("Producto_id"));
            }

            if (productosCompradosIds.isEmpty()) {
                System.out.println("El usuario con id " + id + " no ha realizado compras.");
                return;
            }

            String queryProductos = "SELECT id, nombre, categoria, precio FROM Producto";
            ResultSet productosResult = statement.executeQuery(queryProductos);

            Map<Integer, Map<String, Object>> todosProductos = new HashMap<>();
            Map<String, Integer> categoriasCount = new HashMap<>();

            while (productosResult.next()) {
                int productoId = productosResult.getInt("id");
                String categoria = productosResult.getString("categoria");
                
                Map<String, Object> producto = new HashMap<>();
                producto.put("id", productoId);
                producto.put("nombre", productosResult.getString("nombre"));
                producto.put("categoria", categoria);
                producto.put("precio", productosResult.getDouble("precio"));
                
                todosProductos.put(productoId, producto);

                if (productosCompradosIds.contains(productoId)) {
                    categoriasCount.put(categoria, categoriasCount.getOrDefault(categoria, 0) + 1);
                }
            }

            List<String> categoriasRecomendadas = categoriasCount.entrySet().stream()
                .filter(entry -> entry.getValue() >= 2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

            if (!categoriasRecomendadas.isEmpty()) {
                System.out.println("Productos recomendados para ti:");
                todosProductos.values().stream()
                    .filter(p -> categoriasRecomendadas.contains(p.get("categoria")))
                    .forEach(p -> System.out.println(String.format("Producto: %s, Categoria: %s, Precio: %.2f", 
                        p.get("nombre"), p.get("categoria"), (Double)p.get("precio"))));
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

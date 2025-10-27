public class Controlador {
//(metodos) Gestión articulos
 private static void añadirArticulo() {
        System.out.print("Código: "); String codigo = sc.nextLine();
        System.out.print("Descripción: "); String descripcion = sc.nextLine();
        double precio = leerDouble("Precio venta: ", 0, Double.MAX_VALUE);
        double envio = leerDouble("Gastos de envío: ", 0, Double.MAX_VALUE);
        int tiempo = leerInt("Tiempo preparación (minutos): ", 1, Integer.MAX_VALUE);

        articulos.add(new Articulo(codigo, descripcion, precio, envio, tiempo));
        System.out.println("Artículo añadido correctamente.");
    }

    private static void mostrarArticulos() {
        if (articulos.isEmpty()) {
            System.out.println("No hay artículos registrados.");
            return;
        }
        System.out.println("--- LISTADO DE ARTÍCULOS ---");
        for (Articulo a : articulos) {
            System.out.println(a);
        }
    }

    // -----(Metodos) Gestión de Clientes -----
    private static void gestionClientes() {
        System.out.println("\n--- 

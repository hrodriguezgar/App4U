package main.java.store;

import java.util.*;
import java.time.LocalDateTime;

public class AppOnlineStore {

    // Colecciones dinámicas
    private static Map<String, Cliente> clientes = new HashMap<>(); // email -> main.java.main.java.store.Cliente
    private static List<Articulo> articulos = new ArrayList<>();
    private static List<Pedido> pedidos = new ArrayList<>();

    private static Scanner sc = new Scanner(System.in);
    private static int contadorPedidos = 1;

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            int opcion = leerInt("Elige una opción: ", 0, 3);

            switch(opcion) {
                case 1: gestionArticulos(); break;
                case 2: gestionClientes(); break;
                case 3: gestionPedidos(); break;
                case 0: salir = true; break;
            }
        }
        System.out.println("¡Hasta luego!");
    }

    private static void mostrarMenu() {
        System.out.println("\n--- MENÚ ONLINE STORE ---");
        System.out.println("1. Gestión de Artículos");
        System.out.println("2. Gestión de Clientes");
        System.out.println("3. Gestión de Pedidos");
        System.out.println("0. Salir");
    }

    // ----- Gestión de Artículos -----
    private static void gestionArticulos() {
        System.out.println("\n--- Gestión de Artículos ---");
        System.out.println("1. Añadir Artículo");
        System.out.println("2. Mostrar Artículos");
        int opcion = leerInt("Elige una opción: ", 1, 2);

        switch(opcion) {
            case 1: añadirArticulo(); break;
            case 2: mostrarArticulos(); break;
        }
    }

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

    // ----- Gestión de Clientes -----
    private static void gestionClientes() {
        System.out.println("\n--- Gestión de Clientes ---");
        System.out.println("1. Añadir Cliente");
        System.out.println("2. Mostrar Todos los Clientes");
        System.out.println("3. Mostrar Clientes Estándar");
        System.out.println("4. Mostrar Clientes Premium");
        int opcion = leerInt("Elige una opción: ", 1, 4);

        switch(opcion) {
            case 1: añadirCliente(); break;
            case 2: mostrarClientes(); break;
            case 3: mostrarClientesEstandar(); break;
            case 4: mostrarClientesPremium(); break;
        }
    }

    private static void añadirCliente() {
        System.out.print("Nombre: "); String nombre = sc.nextLine();
        System.out.print("Domicilio: "); String domicilio = sc.nextLine();
        System.out.print("NIF: "); String nif = sc.nextLine();

        String email;
        while (true) {
            System.out.print("Email: ");
            email = sc.nextLine();
            if (!email.isEmpty() && !clientes.containsKey(email)) break;
            System.out.println("Email vacío o ya registrado. Intenta otro.");
        }

        String tipo;
        do {
            System.out.print("¿Es cliente Premium? (s/n): ");
            tipo = sc.nextLine().toLowerCase();
        } while (!tipo.equals("s") && !tipo.equals("n"));

        Cliente cliente = tipo.equals("s")
                ? new ClientePremium(nombre, domicilio, nif, email)
                : new ClienteEstandar(nombre, domicilio, nif, email);

        clientes.put(email, cliente);
        System.out.println("Cliente añadido correctamente.");
    }

    private static void mostrarClientes() {
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("--- LISTADO DE CLIENTES ---");
        for (Cliente c : clientes.values()) {
            System.out.println(c);
        }
    }

    private static void mostrarClientesEstandar() {
        System.out.println("--- CLIENTES ESTÁNDAR ---");
        for (Cliente c : clientes.values()) {
            if (c instanceof ClienteEstandar) System.out.println(c);
        }
    }

    private static void mostrarClientesPremium() {
        System.out.println("--- CLIENTES PREMIUM ---");
        for (Cliente c : clientes.values()) {
            if (c instanceof ClientePremium) System.out.println(c);
        }
    }

    // ----- Gestión de Pedidos -----
    private static void gestionPedidos() {
        System.out.println("\n--- Gestión de Pedidos ---");
        System.out.println("1. Añadir Pedido");
        System.out.println("2. Eliminar Pedido");
        System.out.println("3. Mostrar Pedidos Pendientes");
        System.out.println("4. Mostrar Pedidos Enviados");
        int opcion = leerInt("Elige una opción: ", 1, 4);

        switch(opcion) {
            case 1: añadirPedido(); break;
            case 2: eliminarPedido(); break;
            case 3: mostrarPedidosPendientes(); break;
            case 4: mostrarPedidosEnviados(); break;
        }
    }

    private static void añadirPedido() {
        System.out.print("Email del cliente: "); String email = sc.nextLine();
        Cliente cliente;

        try {
            cliente = buscarCliente(email);
        } catch (ClienteNoEncontradoException e) {
            System.out.println(e.getMessage());
            System.out.println("Se creará un nuevo cliente.");
            añadirCliente();
            try { cliente = buscarCliente(email); }
            catch (ClienteNoEncontradoException ex) {
                System.out.println("Error al crear el cliente. Pedido cancelado.");
                return;
            }
        }

        System.out.print("Código del artículo: "); String codigo = sc.nextLine();
        Articulo articulo;
        try { articulo = buscarArticulo(codigo); }
        catch (ArticuloNoEncontradoException e) {
            System.out.println(e.getMessage());
            return;
        }

        int cantidad = leerInt("Cantidad: ", 1, Integer.MAX_VALUE);

        Pedido pedido = new Pedido(contadorPedidos++, cliente, articulo, cantidad, LocalDateTime.now());
        pedidos.add(pedido);
        System.out.println("Pedido añadido correctamente.");
    }

    private static void eliminarPedido() {
        int num = leerInt("Número de pedido a eliminar: ", 1, Integer.MAX_VALUE);
        Pedido pedidoEliminar = null;

        for (Pedido p : pedidos) if (p.getNumeroPedido() == num) pedidoEliminar = p;

        if (pedidoEliminar == null) System.out.println("Pedido no encontrado.");
        else {
            try {
                if (!pedidoEliminar.puedeCancelar())
                    throw new PedidoNoCancelableException("No se puede eliminar. Pedido enviado o en preparación.");
                pedidos.remove(pedidoEliminar);
                System.out.println("Pedido eliminado correctamente.");
            } catch (PedidoNoCancelableException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void mostrarPedidosPendientes() {
        System.out.print("Filtrar por cliente (email, vacío para todos): ");
        String email = sc.nextLine();

        System.out.println("--- PEDIDOS PENDIENTES ---");
        for (Pedido p : pedidos) {
            if (p.puedeCancelar() && (email.isEmpty() || p.getCliente().getEmail().equals(email))) {
                System.out.println(p);
            }
        }
    }

    private static void mostrarPedidosEnviados() {
        System.out.print("Filtrar por cliente (email, vacío para todos): ");
        String email = sc.nextLine();

        System.out.println("--- PEDIDOS ENVIADOS ---");
        for (Pedido p : pedidos) {
            if (!p.puedeCancelar() && (email.isEmpty() || p.getCliente().getEmail().equals(email))) {
                System.out.println(p);
            }
        }
    }

    // ----- Métodos de búsqueda con excepciones -----
    private static Cliente buscarCliente(String email) throws ClienteNoEncontradoException {
        Cliente c = clientes.get(email);
        if (c == null) throw new ClienteNoEncontradoException("Cliente con email " + email + " no encontrado.");
        return c;
    }

    private static Articulo buscarArticulo(String codigo) throws ArticuloNoEncontradoException {
        for (Articulo a : articulos) if (a.getCodigo().equals(codigo)) return a;
        throw new ArticuloNoEncontradoException("Artículo con código " + codigo + " no encontrado.");
    }

    // ----- Métodos de validación de entradas -----
    private static int leerInt(String mensaje, int min, int max) {
        int valor = 0;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensaje);
            String input = sc.nextLine();
            try {
                valor = Integer.parseInt(input);
                if (valor < min || valor > max) {
                    System.out.println("Valor fuera de rango. Debe estar entre " + min + " y " + max + ".");
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número entero válido.");
            }
        }
        return valor;
    }

    private static double leerDouble(String mensaje, double min, double max) {
        double valor = 0;
        boolean valido = false;
        while (!valido) {
            System.out.print(mensaje);
            String input = sc.nextLine();
            try {
                valor = Double.parseDouble(input);
                if (valor < min || valor > max) {
                    System.out.println("Valor fuera de rango. Debe estar entre " + min + " y " + max + ".");
                } else {
                    valido = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
            }
        }
        return valor;
    }
}
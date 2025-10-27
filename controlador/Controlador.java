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

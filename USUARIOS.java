import java.util.*;

public class GestionUsuarios {
    static class Usuario {
        String nombreCompleto, idUsuario, username, password;
        Usuario(String n, String id, String u, String p) { nombreCompleto = n; idUsuario = id; username = u; password = p; }
        public String toString() { return "ID: " + idUsuario + " | Nombre: " + nombreCompleto + " | Usuario: " + username; }
    }

    static Map<String, Usuario> usuarios = new LinkedHashMap<>();
    static Scanner sc = new Scanner(System.in);

    static boolean vacio(String s) { return s == null || s.isBlank(); }

    static String crear(String n, String id, String u, String p) {
        if (vacio(n) || vacio(id) || vacio(u) || vacio(p)) return "Todos los campos son obligatorios.";
        if (usuarios.containsKey(id.trim())) return "Ya existe un usuario con el ID " + id.trim() + ".";
        if (buscarPorUsername(u.trim()) != null) return "El nombre de usuario '" + u.trim() + "' ya está en uso.";
        usuarios.put(id.trim(), new Usuario(n.trim(), id.trim(), u.trim(), p)); return null;
    }

    static Usuario buscarPorId(String id) { return usuarios.get(id.trim()); }

    static Usuario buscarPorUsername(String u) { return usuarios.values().stream().filter(x -> x.username.equals(u.trim())).findFirst().orElse(null); }

    static String actualizar(String id, String nuevoNombre, String passActual, String nuevaPass) {
        Usuario u = buscarPorId(id); if (u == null) return "El usuario con ID " + id.trim() + " no existe.";
        if (vacio(nuevoNombre) && vacio(nuevaPass)) return "No se indicó ningún cambio.";
        if (!vacio(nuevaPass) && !u.password.equals(passActual)) return "La contraseña actual es incorrecta.";
        if (!vacio(nuevoNombre)) u.nombreCompleto = nuevoNombre.trim();
        if (!vacio(nuevaPass)) u.password = nuevaPass; return null;
    }

    static boolean eliminar(String id) { return usuarios.remove(id.trim()) != null; }

    static String leerTexto(String msg) { System.out.print(msg); return sc.nextLine(); }

    static Integer leerEntero(String msg) { try { return Integer.parseInt(leerTexto(msg).trim()); } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número entero."); return null; } }

    static void menuCrear() { String err = crear(leerTexto("Nombre completo: "), leerTexto("ID de usuario: "), leerTexto("Nombre de usuario (username): "), leerTexto("Contraseña: ")); System.out.println(err == null ? "Usuario creado correctamente." : "Error: " + err); }

    static void menuBuscar() {
        Integer op = leerEntero("Buscar por 1. ID  2. Nombre de usuario: "); if (op == null) return;
        if (op != 1 && op != 2) { System.out.println("Opción inexistente."); return; }
        Usuario u = op == 1 ? buscarPorId(leerTexto("ID: ")) : buscarPorUsername(leerTexto("Username: "));
        System.out.println(u == null ? "Usuario no encontrado." : u);
    }

    static void menuActualizar() {
        String id = leerTexto("ID del usuario a actualizar: "); if (buscarPorId(id) == null) { System.out.println("Error: el usuario con ID " + id.trim() + " no existe."); return; }
        String nombre = leerTexto("Nuevo nombre completo (vacío para no cambiar): "), nueva = leerTexto("Nueva contraseña (vacío para no cambiar): "), actual = vacio(nueva) ? null : leerTexto("Contraseña actual: ");
        String err = actualizar(id, nombre, actual, nueva); System.out.println(err == null ? "Usuario actualizado correctamente." : "Error: " + err);
    }

    static void menuEliminar() { System.out.println(eliminar(leerTexto("ID del usuario a eliminar: ")) ? "Usuario eliminado." : "Error: el usuario no existe."); }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1. Crear usuario  2. Buscar usuario  3. Actualizar usuario  4. Eliminar usuario  5. Listar usuarios  0. Salir");
            Integer op = leerEntero("Opción: "); if (op == null) continue;
            switch (op) {
                case 1 -> menuCrear();
                case 2 -> menuBuscar();
                case 3 -> menuActualizar();
                case 4 -> menuEliminar();
                case 5 -> { if (usuarios.isEmpty()) System.out.println("No hay usuarios registrados."); else usuarios.values().forEach(System.out::println); }
                case 0 -> { System.out.println("¡Hasta pronto!"); return; }
                default -> System.out.println("Opción inexistente.");
            }
        }
    }
}
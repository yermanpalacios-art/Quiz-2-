import java.util.*;

import java.util.*;

public class GestionRecetas {
    static class Ingrediente {
        String nombre, unidad; double cantidad;
        Ingrediente(String n, double c, String u) { nombre = n; cantidad = c; unidad = u; }
        public String toString() { return cantidad + " " + unidad + " de " + nombre; }
    }

    static class Receta {
        String nombre, instrucciones; int porciones; List<Ingrediente> ingredientes;
        Receta(String n, List<Ingrediente> i, String ins, int p) { nombre = n; ingredientes = i; instrucciones = ins; porciones = p; }
        public String toString() { StringBuilder sb = new StringBuilder("Receta: " + nombre + " (" + porciones + " porciones)\n  Ingredientes:\n"); ingredientes.forEach(i -> sb.append("   - ").append(i).append("\n")); return sb.append("  Instrucciones: ").append(instrucciones).toString(); }
    }

    static class RecetaException extends Exception { RecetaException(String m) { super(m); } }

    static final int MAX_RECETAS = 5;
    static Map<String, Receta> recetas = new LinkedHashMap<>();
    static List<String> eventos = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    static void log(String nivel, String msg) { eventos.add("[" + System.currentTimeMillis() + "] " + nivel + ": " + msg); }

    static void registrar(String nombre, List<Ingrediente> ings, String instr, int porciones) throws RecetaException {
        if (nombre == null || nombre.isBlank()) throw new RecetaException("El nombre no puede estar vacío.");
        if (ings == null || ings.isEmpty()) throw new RecetaException("La receta debe tener al menos un ingrediente.");
        if (instr == null || instr.isBlank()) throw new RecetaException("Las instrucciones no pueden estar vacías.");
        if (porciones <= 0) throw new RecetaException("El número de porciones debe ser mayor que cero.");
        if (recetas.containsKey(nombre.trim().toLowerCase())) throw new RecetaException("Ya existe una receta con ese nombre.");
        if (recetas.size() >= MAX_RECETAS) throw new RecetaException("Capacidad máxima alcanzada (" + MAX_RECETAS + " recetas).");
        recetas.put(nombre.trim().toLowerCase(), new Receta(nombre.trim(), ings, instr.trim(), porciones));
        log("INFO", "Receta registrada: " + nombre.trim());
    }

    static Receta buscar(String nombre) throws RecetaException {
        Receta r = nombre == null ? null : recetas.get(nombre.trim().toLowerCase());
        if (r == null) throw new RecetaException("La receta '" + nombre + "' no existe.");
        log("INFO", "Receta consultada: " + r.nombre); return r;
    }

    static void actualizar(String nombre, String nuevasInstr, Integer nuevasPorciones) throws RecetaException {
        Receta r = buscar(nombre);
        if (nuevasInstr != null && nuevasInstr.isBlank()) throw new RecetaException("Las instrucciones no pueden estar vacías.");
        if (nuevasPorciones != null && nuevasPorciones <= 0) throw new RecetaException("El número de porciones debe ser mayor que cero.");
        if (nuevasInstr != null) r.instrucciones = nuevasInstr.trim();
        if (nuevasPorciones != null) r.porciones = nuevasPorciones;
        log("INFO", "Receta actualizada: " + r.nombre);
    }

    static void listar() { if (recetas.isEmpty()) System.out.println("No hay recetas registradas."); else recetas.values().forEach(r -> System.out.println("- " + r.nombre + " (" + r.porciones + " porciones, " + r.ingredientes.size() + " ingredientes)")); log("INFO", "Listado de recetas (" + recetas.size() + ")"); }

    static String leerTexto(String msg) { System.out.print(msg); return sc.nextLine(); }

    static Integer leerEntero(String msg) { String t = leerTexto(msg).trim(); try { return Integer.parseInt(t); } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número entero."); log("WARN", "Entrada no numérica para entero: '" + t + "'"); return null; } }

    static Double leerDecimal(String msg) { String t = leerTexto(msg).trim().replace(',', '.'); try { return Double.parseDouble(t); } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número."); log("WARN", "Entrada no numérica para decimal: '" + t + "'"); return null; } }

    static void menuRegistrar() {
        String nombre = leerTexto("Nombre: "); List<Ingrediente> ings = new ArrayList<>();
        while (true) {
            String n = leerTexto("Ingrediente (vacío para terminar): "); if (n.isBlank()) break;
            Double c = leerDecimal("  Cantidad: "); if (c == null || c <= 0) { System.out.println("Cantidad inválida, ingrediente omitido."); log("WARN", "Ingrediente omitido por cantidad inválida: " + n); continue; }
            String u = leerTexto("  Unidad (g, ml, tazas...): "); if (u.isBlank()) { System.out.println("Unidad vacía, ingrediente omitido."); log("WARN", "Ingrediente omitido por unidad vacía: " + n); continue; }
            ings.add(new Ingrediente(n.trim(), c, u.trim()));
        }
        String instr = leerTexto("Instrucciones: "); Integer p = leerEntero("Porciones: ");
        if (p == null) { System.out.println("Registro cancelado."); log("ERROR", "Registro fallido '" + nombre + "': porciones no numéricas"); return; }
        try { registrar(nombre, ings, instr, p); System.out.println("Receta registrada correctamente."); }
        catch (RecetaException e) { System.out.println("Error: " + e.getMessage()); log("ERROR", "Registro fallido '" + nombre + "': " + e.getMessage()); }
    }

    static void menuBuscar() { String n = leerTexto("Nombre de la receta: "); try { System.out.println(buscar(n)); } catch (RecetaException e) { System.out.println("Error: " + e.getMessage()); log("ERROR", "Búsqueda fallida: " + e.getMessage()); } }

    static void menuActualizar() {
        String n = leerTexto("Nombre de la receta a actualizar: ");
        try { buscar(n); } catch (RecetaException e) { System.out.println("Error: " + e.getMessage()); log("ERROR", "Actualización fallida: " + e.getMessage()); return; }
        String instr = leerTexto("Nuevas instrucciones (vacío para no cambiar): "); String ps = leerTexto("Nuevas porciones (vacío para no cambiar): ").trim(); Integer p = null;
        if (!ps.isEmpty()) { try { p = Integer.parseInt(ps); } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número entero."); log("ERROR", "Actualización fallida '" + n + "': porciones no numéricas '" + ps + "'"); return; } }
        try { actualizar(n, instr.isBlank() ? null : instr, p); System.out.println("Receta actualizada."); }
        catch (RecetaException e) { System.out.println("Error: " + e.getMessage()); log("ERROR", "Actualización fallida '" + n + "': " + e.getMessage()); }
    }

    public static void main(String[] args) {
        log("INFO", "Sistema iniciado");
        while (true) {
            System.out.println("\n1. Registrar  2. Buscar  3. Actualizar  4. Listar  5. Ver registro de eventos  0. Salir");
            Integer op = leerEntero("Opción: "); if (op == null) continue;
            switch (op) {
                case 1 -> menuRegistrar();
                case 2 -> menuBuscar();
                case 3 -> menuActualizar();
                case 4 -> listar();
                case 5 -> eventos.forEach(System.out::println);
                case 0 -> { log("INFO", "Sistema finalizado"); System.out.println("¡Hasta pronto!"); return; }
                default -> { System.out.println("Opción inexistente."); log("WARN", "Opción de menú inexistente: " + op); }
            }
        }
    }
}
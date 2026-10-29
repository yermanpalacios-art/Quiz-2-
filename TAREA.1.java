import java.util.*;

public class Tarea {
    private String descripcion;
    private int prioridad;
    private boolean completada = false;

    public Tarea(String descripcion) { this(descripcion, 2); }

    public Tarea(String descripcion, int prioridad) { if (descripcion == null || descripcion.isBlank()) throw new IllegalArgumentException("La descripción no puede estar vacía."); validarPrioridad(prioridad); this.descripcion = descripcion.trim(); this.prioridad = prioridad; }

    private static void validarPrioridad(int p) { if (p < 1 || p > 3) throw new IllegalArgumentException("Prioridad inválida: debe estar entre 1 (Alta) y 3 (Baja)."); }

    public String getDescripcion() { return descripcion; }

    public int getPrioridad() { return prioridad; }

    public boolean isCompletada() { return completada; }

    public void setPrioridad(int nuevaPrioridad) { validarPrioridad(nuevaPrioridad); prioridad = nuevaPrioridad; }

    public void marcarComoCompletada() { completada = true; }

    public void mostrarEstado() { System.out.println("Tarea: " + descripcion + " | Prioridad: " + nombrePrioridad() + " (" + prioridad + ") | Estado: " + (completada ? "Completada" : "Pendiente")); }

    private String nombrePrioridad() { return prioridad == 1 ? "Alta" : prioridad == 2 ? "Media" : "Baja"; }

    static List<Tarea> tareas = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    static String leerTexto(String msg) { System.out.print(msg); return sc.nextLine(); }

    static Integer leerEntero(String msg) { try { return Integer.parseInt(leerTexto(msg).trim()); } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número entero."); return null; } }

    static Tarea elegirTarea() { if (tareas.isEmpty()) { System.out.println("No hay tareas registradas."); return null; } listar(); Integer n = leerEntero("Número de la tarea: "); if (n == null) return null; if (n < 1 || n > tareas.size()) { System.out.println("La tarea " + n + " no existe."); return null; } return tareas.get(n - 1); }

    static void listar() { if (tareas.isEmpty()) System.out.println("No hay tareas registradas."); else for (int i = 0; i < tareas.size(); i++) { System.out.print((i + 1) + ". "); tareas.get(i).mostrarEstado(); } }

    static void menuCrear() {
        String d = leerTexto("Descripción: "); String p = leerTexto("Prioridad 1=Alta, 2=Media, 3=Baja (vacío para Media): ").trim();
        try { Tarea t; if (p.isEmpty()) t = new Tarea(d); else t = new Tarea(d, Integer.parseInt(p)); tareas.add(t); System.out.println("Tarea creada."); t.mostrarEstado(); }
        catch (NumberFormatException e) { System.out.println("Entrada no válida: la prioridad debe ser un número entero."); }
        catch (IllegalArgumentException e) { System.out.println("Error: " + e.getMessage()); }
    }

    static void menuPrioridad() { Tarea t = elegirTarea(); if (t == null) return; Integer p = leerEntero("Nueva prioridad (1=Alta, 2=Media, 3=Baja): "); if (p == null) return; try { t.setPrioridad(p); System.out.println("Prioridad actualizada."); t.mostrarEstado(); } catch (IllegalArgumentException e) { System.out.println("Error: " + e.getMessage()); } }

    static void menuCompletar() { Tarea t = elegirTarea(); if (t == null) return; if (t.isCompletada()) System.out.println("La tarea ya estaba completada."); else { t.marcarComoCompletada(); System.out.println("Tarea marcada como completada."); } t.mostrarEstado(); }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n1. Crear tarea  2. Listar tareas  3. Cambiar prioridad  4. Marcar como completada  5. Ver estado de una tarea  0. Salir");
            Integer op = leerEntero("Opción: "); if (op == null) continue;
            switch (op) {
                case 1 -> menuCrear();
                case 2 -> listar();
                case 3 -> menuPrioridad();
                case 4 -> menuCompletar();
                case 5 -> { Tarea t = elegirTarea(); if (t != null) t.mostrarEstado(); }
                case 0 -> { System.out.println("¡Hasta pronto!"); return; }
                default -> System.out.println("Opción inexistente.");
            }
        }
    }
}
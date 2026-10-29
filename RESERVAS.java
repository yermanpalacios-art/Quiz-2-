import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GestionReservas {
    static class Sala {
        int id, capacidad; String nombre;
        Sala(int i, String n, int c) { id = i; nombre = n; capacidad = c; }
        public String toString() { return "Sala " + id + ": " + nombre + " (capacidad: " + capacidad + ")"; }
    }

    static class Reserva {
        int id, salaId, asistentes; String reservante; LocalDateTime inicio;
        Reserva(int i, int s, String r, int a, LocalDateTime ini) { id = i; salaId = s; reservante = r; asistentes = a; inicio = ini; }
        LocalDateTime fin() { return inicio.plusHours(DURACION_HORAS); }
        public String toString() { return "Reserva #" + id + " | Sala " + salaId + " | " + inicio.toLocalDate() + " " + inicio.toLocalTime() + "-" + fin().toLocalTime() + " | " + reservante + " | " + asistentes + " asistentes"; }
    }

    static class ReservaException extends Exception { ReservaException(String m) { super(m); } }

    static final int DURACION_HORAS = 1, MAX_RESERVAS = 10;
    static Map<Integer, Sala> salas = new LinkedHashMap<>();
    static Map<Integer, Reserva> reservas = new LinkedHashMap<>();
    static List<String> eventos = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static int siguienteId = 1;

    static void log(String nivel, String msg) { eventos.add("[" + System.currentTimeMillis() + "] " + nivel + ": " + msg); }

    static Sala obtenerSala(int id) throws ReservaException { Sala s = salas.get(id); if (s == null) throw new ReservaException("La sala " + id + " no existe."); return s; }

    static boolean estaDisponible(int salaId, LocalDateTime inicio) { boolean libre = reservas.values().stream().noneMatch(r -> r.salaId == salaId && inicio.isBefore(r.fin()) && r.inicio.isBefore(inicio.plusHours(DURACION_HORAS))); log("INFO", "Verificación de disponibilidad sala " + salaId + " en " + inicio + ": " + (libre ? "disponible" : "ocupada")); return libre; }

    static Reserva reservar(int salaId, String nombre, int asistentes, LocalDateTime inicio) throws ReservaException {
        Sala s = obtenerSala(salaId);
        if (nombre == null || nombre.isBlank()) throw new ReservaException("El nombre de quien reserva no puede estar vacío.");
        if (asistentes <= 0) throw new ReservaException("El número de asistentes debe ser mayor que cero.");
        if (inicio.isBefore(LocalDateTime.now())) throw new ReservaException("La fecha y hora ya pasaron.");
        if (asistentes > s.capacidad) throw new ReservaException("Capacidad excedida: la sala admite " + s.capacidad + " personas.");
        if (reservas.size() >= MAX_RESERVAS) throw new ReservaException("Capacidad del sistema alcanzada (" + MAX_RESERVAS + " reservas).");
        if (!estaDisponible(salaId, inicio)) throw new ReservaException("La sala ya está reservada en ese horario.");
        Reserva r = new Reserva(siguienteId++, salaId, nombre.trim(), asistentes, inicio); reservas.put(r.id, r);
        log("INFO", "Reserva creada: " + r); return r;
    }

    static void cancelar(int id) throws ReservaException { Reserva r = reservas.remove(id); if (r == null) throw new ReservaException("La reserva " + id + " no existe."); log("INFO", "Reserva cancelada: " + r); }

    static List<Reserva> porSala(int salaId) throws ReservaException { obtenerSala(salaId); log("INFO", "Consulta de reservas de la sala " + salaId); return reservas.values().stream().filter(r -> r.salaId == salaId).collect(Collectors.toList()); }

    static List<Reserva> porFecha(LocalDate f) { log("INFO", "Consulta de reservas del " + f); return reservas.values().stream().filter(r -> r.inicio.toLocalDate().equals(f)).collect(Collectors.toList()); }

    static String leerTexto(String msg) { System.out.print(msg); return sc.nextLine(); }

    static Integer leerEntero(String msg) { String t = leerTexto(msg).trim(); try { return Integer.parseInt(t); } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número entero."); log("WARN", "Entrada no numérica: '" + t + "'"); return null; } }

    static LocalDate leerFecha() { String t = leerTexto("Fecha (aaaa-mm-dd): ").trim(); try { return LocalDate.parse(t); } catch (DateTimeParseException e) { System.out.println("Fecha no válida. Usa el formato aaaa-mm-dd."); log("WARN", "Fecha inválida: '" + t + "'"); return null; } }

    static LocalTime leerHora() { String t = leerTexto("Hora (hh:mm, 24 h): ").trim(); try { return LocalTime.parse(t); } catch (DateTimeParseException e) { System.out.println("Hora no válida. Usa el formato hh:mm."); log("WARN", "Hora inválida: '" + t + "'"); return null; } }

    static void mostrar(List<Reserva> l) { if (l.isEmpty()) System.out.println("No hay reservas."); else l.forEach(System.out::println); }

    static void error(String contexto, ReservaException e) { System.out.println("Error: " + e.getMessage()); log("ERROR", contexto + ": " + e.getMessage()); }

    static void menuReservar() {
        Integer s = leerEntero("ID de la sala: "); if (s == null) return;
        String n = leerTexto("Nombre de quien reserva: ");
        Integer a = leerEntero("Número de asistentes: "); if (a == null) return;
        LocalDate f = leerFecha(); if (f == null) return;
        LocalTime h = leerHora(); if (h == null) return;
        try { System.out.println("Reserva creada: " + reservar(s, n, a, LocalDateTime.of(f, h))); } catch (ReservaException e) { error("Reserva fallida", e); }
    }

    static void menuPorSala() { Integer s = leerEntero("ID de la sala: "); if (s == null) return; try { mostrar(porSala(s)); } catch (ReservaException e) { error("Consulta por sala fallida", e); } }

    static void menuPorFecha() { LocalDate f = leerFecha(); if (f != null) mostrar(porFecha(f)); }

    static void menuCancelar() { Integer id = leerEntero("ID de la reserva a cancelar: "); if (id == null) return; try { cancelar(id); System.out.println("Reserva cancelada."); } catch (ReservaException e) { error("Cancelación fallida", e); } }

    static void menuVerificar() {
        Integer s = leerEntero("ID de la sala: "); if (s == null) return;
        LocalDate f = leerFecha(); if (f == null) return;
        LocalTime h = leerHora(); if (h == null) return;
        try { obtenerSala(s); System.out.println(estaDisponible(s, LocalDateTime.of(f, h)) ? "La sala está disponible." : "La sala está ocupada en ese horario."); } catch (ReservaException e) { error("Verificación fallida", e); }
    }

    public static void main(String[] args) {
        salas.put(1, new Sala(1, "Sala Andes", 6)); salas.put(2, new Sala(2, "Sala Caribe", 10)); salas.put(3, new Sala(3, "Sala Pacífico", 20));
        log("INFO", "Sistema iniciado con " + salas.size() + " salas");
        while (true) {
            System.out.println("\n1. Ver salas  2. Reservar  3. Reservas por sala  4. Reservas por fecha  5. Cancelar reserva  6. Verificar disponibilidad  7. Ver registro de eventos  0. Salir");
            Integer op = leerEntero("Opción: "); if (op == null) continue;
            switch (op) {
                case 1 -> salas.values().forEach(System.out::println);
                case 2 -> menuReservar();
                case 3 -> menuPorSala();
                case 4 -> menuPorFecha();
                case 5 -> menuCancelar();
                case 6 -> menuVerificar();
                case 7 -> eventos.forEach(System.out::println);
                case 0 -> { log("INFO", "Sistema finalizado"); System.out.println("¡Hasta pronto!"); return; }
                default -> { System.out.println("Opción inexistente."); log("WARN", "Opción de menú inexistente: " + op); }
            }
        }
    }
}

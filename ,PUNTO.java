import java.util.*;

public class Punto {
    private double x, y;

    public Punto() { this(0, 0); }

    public Punto(double x, double y) { this.x = x; this.y = y; }

    public double getX() { return x; }

    public double getY() { return y; }

    public void setX(double nuevoX) { x = nuevoX; }

    public void setY(double nuevoY) { y = nuevoY; }

    public void mover(double deltaX, double deltaY) { x += deltaX; y += deltaY; }

    public double calcularDistanciaDesdeOrigen() { return Math.sqrt(x * x + y * y); }

    public void mostrarCoordenadas() { System.out.println("(" + x + ", " + y + ")"); }

    static Scanner sc = new Scanner(System.in);

    static Integer leerEntero(String msg) { System.out.print(msg); try { return Integer.parseInt(sc.nextLine().trim()); } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número entero."); return null; } }

    static Double leerDecimal(String msg) { System.out.print(msg); try { double d = Double.parseDouble(sc.nextLine().trim().replace(',', '.')); if (Double.isNaN(d) || Double.isInfinite(d)) throw new NumberFormatException(); return d; } catch (NumberFormatException e) { System.out.println("Entrada no válida: se esperaba un número."); return null; } }

    public static void main(String[] args) {
        Punto p = new Punto();
        System.out.print("Punto inicial en el origen: "); p.mostrarCoordenadas();
        while (true) {
            System.out.println("\n1. Crear punto en el origen  2. Crear punto con coordenadas  3. Ver coordenadas  4. Ver X e Y (getX/getY)  5. Cambiar X  6. Cambiar Y  7. Mover  8. Calcular distancia al origen  0. Salir");
            Integer op = leerEntero("Opción: "); if (op == null) continue;
            switch (op) {
                case 1 -> { p = new Punto(); System.out.print("Nuevo punto: "); p.mostrarCoordenadas(); }
                case 2 -> { Double x = leerDecimal("X: "); if (x == null) break; Double y = leerDecimal("Y: "); if (y == null) break; p = new Punto(x, y); System.out.print("Nuevo punto: "); p.mostrarCoordenadas(); }
                case 3 -> { System.out.print("Coordenadas: "); p.mostrarCoordenadas(); }
                case 4 -> System.out.println("X = " + p.getX() + " | Y = " + p.getY());
                case 5 -> { Double x = leerDecimal("Nuevo X: "); if (x == null) break; p.setX(x); System.out.print("Coordenadas: "); p.mostrarCoordenadas(); }
                case 6 -> { Double y = leerDecimal("Nuevo Y: "); if (y == null) break; p.setY(y); System.out.print("Coordenadas: "); p.mostrarCoordenadas(); }
                case 7 -> { Double dx = leerDecimal("Desplazamiento en X: "); if (dx == null) break; Double dy = leerDecimal("Desplazamiento en Y: "); if (dy == null) break; p.mover(dx, dy); System.out.print("Coordenadas: "); p.mostrarCoordenadas(); }
                case 8 -> System.out.println("Distancia al origen: " + p.calcularDistanciaDesdeOrigen());
                case 0 -> { System.out.println("¡Hasta pronto!"); return; }
                default -> System.out.println("Opción inexistente.");
            }
        }
    }
}
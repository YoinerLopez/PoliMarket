package com.polimarket.presentation.console;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.polimarket.application.dto.AdquisicionDto;
import com.polimarket.application.dto.LoginRequestDto;
import com.polimarket.application.dto.PaginaDTO;
import com.polimarket.application.dto.ProveedorDto;
import com.polimarket.application.dto.SesionUsuarioDto;
import com.polimarket.application.service.ServicioAutenticacion;
import com.polimarket.application.service.ServicioBodega;

/**
 * Cliente de Consola (Terminal de Inventario).
 *
 * <p>Requiere inicio de sesión con usuario y contraseña antes de permitir
 * el acceso a las operaciones del menú de inventario.</p>
 */
@Component
public class ClienteConsola {

    private static final int TAMANO_BLOQUE_BD = 495;
    private static final int TAMANO_PANTALLA_CONSOLA = 15;

    private final ServicioBodega servicioBodega;
    private final ServicioAutenticacion servicioAutenticacion;

    public ClienteConsola(ServicioBodega servicioBodega, ServicioAutenticacion servicioAutenticacion) {
        this.servicioBodega = servicioBodega;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    /**
     * Inicia el ciclo interactivo de la terminal con login obligatorio.
     */
    public void ejecutar(InputStream in, PrintStream out) {
        Scanner scanner = new Scanner(in);
        out.println("=============================================================");
        out.println("               POLIMARKET - TERMINAL DE BODEGA               ");
        out.println("=============================================================");

        SesionUsuarioDto sesion = solicitarLogin(scanner, out);
        if (sesion == null) {
            out.println("\nSesión cancelada o credenciales incorrectas. Saliendo.");
            return;
        }

        out.printf("\n[✓] ¡Bienvenido %s! Sesión iniciada con éxito.\n\n", sesion.nombreUsuario());

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal(out, sesion.nombreUsuario());
            out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1" -> ejecutarConsultaAdquisicionesPaginada(scanner, out);
                case "2" -> ejecutarIdentificarProveedorReposicion(scanner, out);
                case "3" -> {
                    out.println("\nCerrando sesión y terminal. ¡Hasta pronto!");
                    continuar = false;
                }
                default -> out.println("\n[!] Opción inválida. Por favor seleccione 1, 2 o 3.\n");
            }
        }
    }

    private SesionUsuarioDto solicitarLogin(Scanner scanner, PrintStream out) {
        int intentos = 0;
        while (intentos < 3) {
            out.println("\n--- INICIO DE SESIÓN ---");
            out.print("Usuario (o 'salir' para cancelar): ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String usuario = scanner.nextLine().trim();
            if (usuario.equalsIgnoreCase("salir")) {
                return null;
            }

            out.print("Contraseña: ");
            if (!scanner.hasNextLine()) {
                return null;
            }
            String password = scanner.nextLine().trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                out.println("[!] Debe ingresar usuario y contraseña.");
                intentos++;
                continue;
            }

            try {
                return servicioAutenticacion.autenticar(new LoginRequestDto(usuario, password));
            } catch (Exception ex) {
                out.printf("[X] Error de autenticación: %s\n", ex.getMessage());
                intentos++;
                if (intentos < 3) {
                    out.printf("Intentos restantes: %d\n", 3 - intentos);
                } else {
                    out.println("[X] Número máximo de intentos alcanzado.");
                }
            }
        }
        return null;
    }

    public void mostrarMenuPrincipal(PrintStream out, String usuario) {
        out.println("=============================================================");
        out.printf("   POLIMARKET - MENÚ DE INVENTARIO (Usuario: %s)             \n", usuario);
        out.println("=============================================================");
        out.println("  1. Consultar adquisiciones históricas y proveedores         ");
        out.println("  2. Identificar proveedor para reposición de stock agotado  ");
        out.println("  3. Cerrar sesión y salir                                    ");
        out.println("=============================================================");
    }

    public void ejecutarConsultaAdquisicionesPaginada(Scanner scanner, PrintStream out) {
        out.println("\n-------------------------------------------------------------");
        out.println("              HISTÓRICO DE ADQUISICIONES Y PROVEEDORES       ");
        out.println("-------------------------------------------------------------");

        int paginaBdActual = 0;
        PaginaDTO<AdquisicionDto> bloqueBd = servicioBodega.consultarProductosAdquiridosPaginado(paginaBdActual, TAMANO_BLOQUE_BD);

        if (bloqueBd.contenido().isEmpty()) {
            out.println("[i] No se encontraron registros de adquisición en el sistema.");
            out.println("Presione ENTER para volver al menú...");
            scanner.nextLine();
            return;
        }

        List<AdquisicionDto> bufferLocal = new ArrayList<>(bloqueBd.contenido());
        long totalRegistros = bloqueBd.totalElementos();
        int indiceGlobal = 0;
        boolean enPaginacion = true;

        while (enPaginacion) {
            int finIndice = Math.min(indiceGlobal + TAMANO_PANTALLA_CONSOLA, bufferLocal.size());
            int paginaTerminalActual = (indiceGlobal / TAMANO_PANTALLA_CONSOLA) + 1;
            int totalPaginasTerminal = (int) Math.ceil((double) totalRegistros / TAMANO_PANTALLA_CONSOLA);

            out.printf("\n--- Pantalla %d de %d (Mostrando %d al %d de %d registros) ---\n",
                    paginaTerminalActual, totalPaginasTerminal, indiceGlobal + 1, finIndice, totalRegistros);
            out.printf("%-4s | %-12s | %-28s | %-10s | %s\n", "#", "CÓDIGO", "PRODUCTO", "PROV. COD", "PROVEEDOR");
            out.println("-----------------------------------------------------------------------------------------");

            for (int i = indiceGlobal; i < finIndice; i++) {
                AdquisicionDto item = bufferLocal.get(i);
                out.printf("%-4d | %-12s | %-28s | %-10s | %s\n",
                        i + 1, item.codigoProducto(), recortar(item.nombreProducto(), 28),
                        item.codigoProveedor(), item.nombreProveedor());
            }
            out.println("-----------------------------------------------------------------------------------------");

            boolean tieneSiguiente = (indiceGlobal + TAMANO_PANTALLA_CONSOLA) < totalRegistros;
            boolean tieneAnterior = indiceGlobal > 0;

            out.print("Opciones: ");
            if (tieneSiguiente) out.print("[S] Siguiente página | ");
            if (tieneAnterior) out.print("[A] Anterior página | ");
            out.println("[M] Volver al Menú Principal");
            out.print("Seleccione: ");

            String comando = scanner.nextLine().trim().toUpperCase();

            switch (comando) {
                case "S" -> {
                    if (tieneSiguiente) {
                        indiceGlobal += TAMANO_PANTALLA_CONSOLA;
                        if (indiceGlobal >= bufferLocal.size() && !bloqueBd.esUltima()) {
                            paginaBdActual++;
                            bloqueBd = servicioBodega.consultarProductosAdquiridosPaginado(paginaBdActual, TAMANO_BLOQUE_BD);
                            bufferLocal.addAll(bloqueBd.contenido());
                        }
                    } else {
                        out.println("[!] Ya se encuentra en la última pantalla.");
                    }
                }
                case "A" -> {
                    if (tieneAnterior) {
                        indiceGlobal = Math.max(0, indiceGlobal - TAMANO_PANTALLA_CONSOLA);
                    } else {
                        out.println("[!] Ya se encuentra en la primera pantalla.");
                    }
                }
                case "M" -> enPaginacion = false;
                default -> out.println("[!] Comando no reconocido.");
            }
        }
    }

    public void ejecutarIdentificarProveedorReposicion(Scanner scanner, PrintStream out) {
        out.println("\n-------------------------------------------------------------");
        out.println("     IDENTIFICAR PROVEEDOR PARA REPOSICIÓN DE STOCK          ");
        out.println("-------------------------------------------------------------");
        out.print("Ingrese el código del producto agotado (ej. PROD-003): ");
        String codigo = scanner.nextLine().trim();

        if (codigo.isEmpty()) {
            out.println("[!] El código no puede estar vacío.");
            return;
        }

        try {
            ProveedorDto proveedor = servicioBodega.identificarProveedorReposicion(codigo);
            out.println("\n[✓] PROVEEDOR IDENTIFICADO CON ÉXITO:");
            out.printf("  - Código de Proveedor: %s\n", proveedor.codigo());
            out.printf("  - Nombre de Proveedor: %s\n", proveedor.nombre());
            out.printf("  - Acción recomendada: Solicitar reposición de unidades para el producto '%s'.\n", codigo);
        } catch (Exception ex) {
            out.printf("\n[X] Error: %s\n", ex.getMessage());
        }

        out.println("\nPresione ENTER para volver al menú principal...");
        scanner.nextLine();
    }

    private String recortar(String texto, int longitudMax) {
        if (texto == null) return "";
        return texto.length() <= longitudMax ? texto : texto.substring(0, longitudMax - 3) + "...";
    }

}

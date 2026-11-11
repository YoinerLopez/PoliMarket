package com.polimarket.domain.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa un vendedor o usuario registrado en el sistema de PoliMarket con credenciales y permisos.
 */
public class Vendedor {

    private final String nombreUsuario;
    private final String password;
    private boolean autorizado;
    private final String rol;
    private final String permisos;

    public Vendedor(String nombreUsuario, String password, boolean autorizado, String rol, String permisos) {
        this.nombreUsuario = Objects.requireNonNull(nombreUsuario, "El nombre de usuario no puede ser nulo");
        this.password = password != null ? password : "password123";
        this.autorizado = autorizado;
        this.rol = rol != null ? rol : "VENDEDOR";
        this.permisos = permisos != null ? permisos : ("RECURSOS_HUMANOS".equalsIgnoreCase(this.rol) ? "GESTION_RRHH,CONSULTA_GENERAL" : "VENTAS,BODEGA");
    }

    public Vendedor(String nombreUsuario, String password, boolean autorizado) {
        this(nombreUsuario, password, autorizado, "VENDEDOR", "VENTAS,BODEGA");
    }

    public Vendedor(String nombreUsuario, boolean autorizado) {
        this(nombreUsuario, "password123", autorizado, "VENDEDOR", "VENTAS,BODEGA");
    }

    /**
     * Autoriza al vendedor para interactuar con los sistemas de PoliMarket.
     */
    public void autorizar() {
        this.autorizado = true;
    }

    /**
     * Revoca la autorización al vendedor, impidiendo su acceso a los sistemas.
     */
    public void desautorizar() {
        this.autorizado = false;
    }

    /**
     * Valida si la contraseña ingresada coincide con la del vendedor.
     */
    public boolean validarPassword(String intentoPassword) {
        return this.password.equals(intentoPassword);
    }

    /**
     * Verifica si el vendedor está autorizado.
     *
     * @return {@code true} si el vendedor ha sido autorizado por RRHH
     */
    public boolean estaAutorizado() {
        return this.autorizado;
    }

    /**
     * Determina si el usuario tiene rol de Recursos Humanos.
     */
    public boolean esRecursosHumanos() {
        return "RECURSOS_HUMANOS".equalsIgnoreCase(this.rol);
    }

    /**
     * Verifica si el usuario cuenta con un permiso específico.
     */
    public boolean tienePermiso(String permisoBuscado) {
        if (permisoBuscado == null || this.permisos == null) {
            return false;
        }
        return Arrays.stream(this.permisos.split(","))
                .map(String::trim)
                .anyMatch(p -> p.equalsIgnoreCase(permisoBuscado.trim()));
    }

    public List<String> getListaPermisos() {
        if (this.permisos == null || this.permisos.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(this.permisos.split(","))
                .map(String::trim)
                .toList();
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }

    public String getPermisos() {
        return permisos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vendedor vendedor = (Vendedor) o;
        return Objects.equals(nombreUsuario, vendedor.nombreUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombreUsuario);
    }

    @Override
    public String toString() {
        return "Vendedor{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", autorizado=" + autorizado +
                ", rol='" + rol + '\'' +
                ", permisos='" + permisos + '\'' +
                '}';
    }
}

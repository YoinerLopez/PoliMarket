package com.polimarket.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA que mapea la tabla de vendedores con credenciales, roles y permisos.
 */
@Entity
@Table(name = "vendedores")
public class VendedorEntity {

    @Id
    @Column(name = "nombre_usuario", nullable = false, unique = true)
    private String nombreUsuario;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "autorizado", nullable = false)
    private boolean autorizado;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "permisos", nullable = false)
    private String permisos;

    protected VendedorEntity() {
        // Constructor requerido por JPA
    }

    public VendedorEntity(String nombreUsuario, String password, boolean autorizado, String rol, String permisos) {
        this.nombreUsuario = nombreUsuario;
        this.password = password;
        this.autorizado = autorizado;
        this.rol = rol != null ? rol : "VENDEDOR";
        this.permisos = permisos != null ? permisos : ("RECURSOS_HUMANOS".equalsIgnoreCase(this.rol) ? "GESTION_RRHH,CONSULTA_GENERAL" : "VENTAS,BODEGA");
    }

    public VendedorEntity(String nombreUsuario, String password, boolean autorizado) {
        this(nombreUsuario, password, autorizado, "VENDEDOR", "VENTAS,BODEGA");
    }

    public VendedorEntity(String nombreUsuario, boolean autorizado) {
        this(nombreUsuario, "password123", autorizado, "VENDEDOR", "VENTAS,BODEGA");
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getPermisos() {
        return permisos;
    }

    public void setPermisos(String permisos) {
        this.permisos = permisos;
    }
}

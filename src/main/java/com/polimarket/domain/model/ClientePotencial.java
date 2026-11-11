package com.polimarket.domain.model;

import java.util.Objects;

/**
 * Representa un posible cliente que el vendedor puede contactar.
 *
 * <p>Los clientes potenciales están precargados en el sistema de Ventas (RF-02).
 * Incluye información de contacto para que el vendedor pueda comunicarse efectivamente.</p>
 */
public class ClientePotencial {

    private final Long id;
    private final String nombre;
    private final String correo;
    private final String telefono;
    private final String ciudad;

    public ClientePotencial(Long id, String nombre, String correo, String telefono, String ciudad) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "El nombre del cliente no puede ser nulo");
        this.correo = correo != null ? correo : "";
        this.telefono = telefono != null ? telefono : "";
        this.ciudad = ciudad != null ? ciudad : "";
    }

    public ClientePotencial(Long id, String nombre) {
        this(id, nombre, "contacto@" + nombre.toLowerCase().replaceAll("[^a-z0-9]", "") + ".com", "+57 300 000 0000", "Colombia");
    }

    /**
     * Consulta el nombre del cliente potencial (especificado en el diagrama de clases).
     *
     * @return nombre del cliente
     */
    public String consultarNombre() {
        return this.nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCiudad() {
        return ciudad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientePotencial that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}

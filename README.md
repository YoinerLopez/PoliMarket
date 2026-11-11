# PoliMarket API

> Sistema computacional para la compañía PoliMarket  
> Maestría en Arquitectura de Software — Politécnico Grancolombiano

## 📋 Descripción

API REST desarrollada con **Spring Boot 4.1.2** y **Java 24** que implementa el sistema de gestión de PoliMarket, cubriendo los módulos de Recursos Humanos, Ventas, Bodega, Proveedores y Entregas.

## 👥 Autores

| Nombre | Rol |
|---|---|
| Adriana Rasch Mendoza | Desarrollador |
| Yoiner Alexis Lopez Quintana | Desarrollador |
| Juan Pablo Estupiñan Cipaguata | Desarrollador |
| Kevin Quiñones Baldion | Desarrollador |

**Materia:** Temas Avanzados de Diseño de Software  
**Tutor:** John Alexander Ardila

## 🏗️ Arquitectura

El proyecto sigue los principios de **Clean Architecture** y **SOLID**, organizado en las siguientes capas:

```
com.polimarket
├── domain/              → Entidades y reglas de negocio puras
│   ├── model/           → Clases de dominio (Vendedor, Producto, etc.)
│   ├── repository/      → Interfaces de repositorio (puertos)
│   └── exception/       → Excepciones de dominio
├── application/         → Casos de uso y lógica de aplicación
│   ├── service/         → Servicios de aplicación
│   └── dto/             → Objetos de transferencia de datos
├── infrastructure/      → Implementaciones técnicas
│   ├── persistence/     → JPA entities, repositorios Spring Data
│   │   ├── entity/      → Entidades JPA
│   │   ├── repository/  → Repositorios Spring Data JPA
│   │   └── mapper/      → Mapeadores entidad ↔ dominio
│   └── config/          → Configuración de Spring
└── presentation/        → Capa de presentación
    ├── controller/      → Controladores REST
    └── dto/             → Request/Response DTOs
```

## 📦 Requisitos Funcionales

| RF | Nombre | Descripción |
|---|---|---|
| RF-01 | Autorizar vendedores | Autorizar a un vendedor registrado |
| RF-02 | Consultar clientes potenciales | Listar posibles clientes |
| RF-03 | Consultar productos disponibles | Listar productos con stock > 0 |
| RF-04 | Consultar adquisiciones | Listar productos adquiridos con proveedor |
| RF-05 | Identificar proveedor reposición | Obtener proveedor de producto agotado |
| RF-06 | Consultar productos por entregar | *(Opcional)* Listar entregas pendientes |
| RF-07 | Registrar salida de productos | *(Opcional)* Descontar inventario |

## 🛠️ Stack Tecnológico

- **Java 24**
- **Spring Boot 4.1.2**
- **Spring Data JPA**
- **H2 Database** (en memoria)
- **Maven 3.9+**

## 🚀 Cómo ejecutar

```bash
# Clonar el repositorio
git clone <url-del-repositorio>
cd polimarket-api

# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Ejecutar tests
mvn test
```

La API estará disponible en `http://localhost:8080`

### Consola H2

Disponible en `http://localhost:8080/h2-console`  
- **JDBC URL:** `jdbc:h2:mem:polimarketdb`
- **Usuario:** `sa`
- **Contraseña:** *(vacía)*

## 📄 Licencia

Proyecto académico — Politécnico Grancolombiano © 2026

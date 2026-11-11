/**
 * Diccionario centralizado de internacionalización (i18n) para el Cliente Web de PoliMarket.
 */
const I18N_DICTIONARY = {
    es: {
        // Cabecera y Navegación
        "app.title": "PoliMarket",
        "app.subtitle": "Plataforma de Gestión Comercial y Bodega",
        "lang.label": "🌐 Idioma / Language:",
        "nav.rrhh": "Recursos Humanos",
        "nav.clientes": "Clientes Potenciales",
        "nav.productos": "Productos Disponibles",
        "nav.bodega": "Bodega e Inventario",

        // Pantalla de Inicio de Sesión (Login)
        "login.title": "Iniciar Sesión",
        "login.subtitle": "Ingrese sus credenciales para acceder a la plataforma.",
        "login.user.label": "Usuario:",
        "login.user.placeholder": "Ej: acastro o admin.rrhh",
        "login.pass.label": "Contraseña:",
        "login.pass.placeholder": "Ingrese su contraseña",
        "login.btn": "Ingresar a la Plataforma",
        "login.quick": "Cuentas de demostración disponibles:",
        "login.quick.note": "(Contraseña para todas: password123)",
        "login.btn.logout": "Cerrar Sesión",
        "login.welcome": "Sesión activa:",

        // Módulo RRHH
        "rrhh.panel.title": "Gestión y Autorización de Vendedores",
        "rrhh.panel.desc": "Administre las autorizaciones y permisos de acceso para los vendedores y personal comercial.",
        "rrhh.filter.placeholder": "🔍 Filtrar usuario por nombre...",
        "rrhh.btn.refrescar": "🔄 Actualizar Lista",
        "rrhh.th.user": "Usuario",
        "rrhh.th.role": "Rol",
        "rrhh.th.permissions": "Permisos Asignados",
        "rrhh.th.status": "Estado de Autorización",
        "rrhh.th.actions": "Acciones",
        "rrhh.btn.autorizar": "✓ Autorizar",
        "rrhh.btn.revocar": "✕ Revocar",
        "rrhh.status.authorized": "AUTORIZADO",
        "rrhh.status.pending": "PENDIENTE / REVOCADO",
        "rrhh.restricted.title": "🔒 Acceso Exclusivo de Recursos Humanos",
        "rrhh.restricted.desc": "Su cuenta actual no tiene asignado el permiso de Recursos Humanos. Para gestionar y autorizar vendedores, inicie sesión con la cuenta <strong>admin.rrhh</strong> (Contraseña: password123).",

        // Módulo Ventas - Clientes
        "ventas.clientes.title": "Cartera de Clientes Potenciales y Contacto",
        "ventas.clientes.desc": "Listado de posibles clientes y datos de contacto asignados a su cuenta comercial.",
        "ventas.clientes.btn.consultar": "🔍 Consultar Cartera",
        "ventas.clientes.placeholder": "Presione \"Consultar Cartera\" para desplegar sus contactos.",
        "ventas.clientes.loading": "Cargando cartera de clientes...",
        "ventas.clientes.empty": "No se encontraron clientes potenciales registrados.",
        "ventas.clientes.card.id": "ID Cliente: #",
        "ventas.clientes.card.phone": "Tel:",
        "ventas.clientes.card.email": "Email:",
        "ventas.clientes.card.city": "Ciudad:",

        // Módulo Ventas - Productos
        "ventas.productos.title": "Catálogo de Productos Disponibles para Venta",
        "ventas.productos.desc": "Muestra los artículos con existencias en bodega mayores a cero.",
        "ventas.productos.btn.consultar": "📦 Consultar Productos Disponibles",
        "ventas.productos.placeholder": "Presione \"Consultar Productos Disponibles\" para cargar el catálogo.",
        "ventas.productos.loading": "Consultando existencias en Bodega...",
        "ventas.productos.empty": "No hay productos disponibles en bodega en este momento.",

        // Módulo Bodega e Inventario
        "bodega.panel.title": "Gestión de Bodega e Inventario",
        "bodega.panel.desc": "Control de existencias: consulta el inventario completo, identifica proveedores para reposición y registra movimientos de salida o recarga mediante modales.",
        "bodega.btn.refrescar": "🔄 Refrescar Inventario",
        "bodega.th.code": "Código",
        "bodega.th.name": "Producto",
        "bodega.th.stock": "Stock Actual",
        "bodega.th.status": "Estado",
        "bodega.th.actions": "Acciones de Inventario",
        "bodega.status.available": "DISPONIBLE",
        "bodega.status.low": "STOCK BAJO",
        "bodega.status.out": "AGOTADO (0)",
        "bodega.btn.retirar": "➖ Disminuir Stock",
        "bodega.btn.recargar": "➕ Aumentar Stock",
        "bodega.btn.proveedor": "🏢 Proveedor Reposición",
        "bodega.modal.prov.title": "Proveedor Asignado para Reposición",
        "bodega.modal.prov.code": "Código de Proveedor:",
        "bodega.modal.prov.name": "Nombre / Razón Social:",

        // Modales de Inventario
        "modal.recargar.title": "Aumentar Stock / Abastecer Inventario",
        "modal.recargar.label": "Cantidad de unidades a ingresar:",
        "modal.recargar.btn": "✓ Confirmar Ingreso",
        "modal.salida.title": "Disminuir Stock / Registrar Salida",
        "modal.salida.label": "Cantidad de unidades a retirar:",
        "modal.salida.btn": "✓ Confirmar Salida",
        "modal.btn.cancel": "Cancelar",
        "modal.product": "Producto:",
        "modal.current.stock": "Stock Actual:",
        "modal.units": "unidades",

        // Tablas
        "table.th.code": "Código",
        "table.th.name": "Nombre del Producto",
        "table.th.stock": "Stock Disponible",
        "table.th.status": "Estado",
        "table.units": "unidades",

        // Mensajes Generales
        "msg.lang.changed": "Idioma cambiado a: Español",
        "msg.conn.error": "Error de conexión con el servidor",
        "msg.enter.user": "Por favor ingrese un nombre de usuario",
        "msg.access.denied": "Acceso Denegado:",
        "msg.processing": "Procesando..."
    },
    en: {
        // Header & Navigation
        "app.title": "PoliMarket",
        "app.subtitle": "Commercial Management & Warehouse Platform",
        "lang.label": "🌐 Language / Idioma:",
        "nav.rrhh": "Human Resources",
        "nav.clientes": "Potential Clients",
        "nav.productos": "Available Products",
        "nav.bodega": "Warehouse & Inventory",

        // Login Screen
        "login.title": "Sign In",
        "login.subtitle": "Enter your credentials to access the platform.",
        "login.user.label": "Username:",
        "login.user.placeholder": "E.g.: acastro or admin.rrhh",
        "login.pass.label": "Password:",
        "login.pass.placeholder": "Enter your password",
        "login.btn": "Sign In to Platform",
        "login.quick": "Quick demo accounts:",
        "login.quick.note": "(Password for all: password123)",
        "login.btn.logout": "Sign Out",
        "login.welcome": "Active user:",

        // Module HR
        "rrhh.panel.title": "Seller Management & Authorization",
        "rrhh.panel.desc": "Manage authorization and access permissions for sellers and commercial staff.",
        "rrhh.filter.placeholder": "🔍 Filter user by name...",
        "rrhh.btn.refrescar": "🔄 Refresh List",
        "rrhh.th.user": "Username",
        "rrhh.th.role": "Role",
        "rrhh.th.permissions": "Permissions",
        "rrhh.th.status": "Authorization Status",
        "rrhh.th.actions": "Actions",
        "rrhh.btn.autorizar": "✓ Authorize",
        "rrhh.btn.revocar": "✕ Revoke",
        "rrhh.status.authorized": "AUTHORIZED",
        "rrhh.status.pending": "PENDING / REVOKED",
        "rrhh.restricted.title": "🔒 Human Resources Restricted Access",
        "rrhh.restricted.desc": "Your current account does not have Human Resources privileges. To manage and authorize sellers, please sign in with <strong>admin.rrhh</strong> (Password: password123).",

        // Module Sales - Clients
        "ventas.clientes.title": "Potential Clients Portfolio & Contact",
        "ventas.clientes.desc": "List of potential clients and contact information assigned to your commercial account.",
        "ventas.clientes.btn.consultar": "🔍 Query Portfolio",
        "ventas.clientes.placeholder": "Click \"Query Portfolio\" to display your contacts.",
        "ventas.clientes.loading": "Loading clients portfolio...",
        "ventas.clientes.empty": "No potential clients found.",
        "ventas.clientes.card.id": "Client ID: #",
        "ventas.clientes.card.phone": "Phone:",
        "ventas.clientes.card.email": "Email:",
        "ventas.clientes.card.city": "City:",

        // Module Sales - Products
        "ventas.productos.title": "Products Available for Sale",
        "ventas.productos.desc": "Displays items with warehouse inventory greater than zero.",
        "ventas.productos.btn.consultar": "📦 Query Available Products",
        "ventas.productos.placeholder": "Click \"Query Available Products\" to load the sales catalog.",
        "ventas.productos.loading": "Querying warehouse stock...",
        "ventas.productos.empty": "No products currently available in warehouse.",

        // Module Warehouse & Inventory
        "bodega.panel.title": "Warehouse & Inventory Management",
        "bodega.panel.desc": "Inventory control: inspect full stock, identify restocking suppliers and record inventory movements using modals.",
        "bodega.btn.refrescar": "🔄 Refresh Inventory",
        "bodega.th.code": "Code",
        "bodega.th.name": "Product",
        "bodega.th.stock": "Current Stock",
        "bodega.th.status": "Status",
        "bodega.th.actions": "Inventory Actions",
        "bodega.status.available": "AVAILABLE",
        "bodega.status.low": "LOW STOCK",
        "bodega.status.out": "OUT OF STOCK (0)",
        "bodega.btn.retirar": "➖ Decrease Stock",
        "bodega.btn.recargar": "➕ Increase Stock",
        "bodega.btn.proveedor": "🏢 Restock Supplier",
        "bodega.modal.prov.title": "Assigned Restock Supplier",
        "bodega.modal.prov.code": "Supplier Code:",
        "bodega.modal.prov.name": "Company Name:",

        // Inventory Modals
        "modal.recargar.title": "Increase Stock / Restock Inventory",
        "modal.recargar.label": "Units to add/restock:",
        "modal.recargar.btn": "✓ Confirm Restock",
        "modal.salida.title": "Decrease Stock / Register Exit",
        "modal.salida.label": "Units to withdraw:",
        "modal.salida.btn": "✓ Confirm Exit",
        "modal.btn.cancel": "Cancel",
        "modal.product": "Product:",
        "modal.current.stock": "Current Stock:",
        "modal.units": "units",

        // Common Tables
        "table.th.code": "Code",
        "table.th.name": "Product Name",
        "table.th.stock": "Available Stock",
        "table.th.status": "Status",
        "table.units": "units",

        // General Messages
        "msg.lang.changed": "Language changed to: English",
        "msg.conn.error": "Server connection error",
        "msg.enter.user": "Please enter a username",
        "msg.access.denied": "Access Denied:",
        "msg.processing": "Processing..."
    }
};

function t(key, lang = 'es') {
    const langDict = I18N_DICTIONARY[lang] || I18N_DICTIONARY['es'];
    return langDict[key] || key;
}

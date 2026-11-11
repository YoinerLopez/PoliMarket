/**
 * PoliMarket - Cliente Web (JavaScript)
 * Implementa Clean Architecture, Internacionalización (i18n), Control de Acceso por Roles y Modales.
 */

const API_BASE = '/api/v1';

// Estado global reactivo de la aplicación
const state = {
    idioma: localStorage.getItem('polimarket_lang') || 'es',
    usuarioActivo: sessionStorage.getItem('polimarket_user') || null,
    rolActivo: sessionStorage.getItem('polimarket_role') || null,
    permisosActivos: JSON.parse(sessionStorage.getItem('polimarket_permisos') || '[]'),
    vendedores: [],
    ultimosClientes: null,
    ultimosProductos: null,
    ultimoInventario: null,
    productoSeleccionadoModal: null
};

// Elementos del DOM - Autenticación y Layout
const loginView = document.getElementById('loginView');
const appView = document.getElementById('appView');
const userSessionHeader = document.getElementById('userSessionHeader');
const sessionUserName = document.getElementById('sessionUserName');
const btnLogout = document.getElementById('btnLogout');
const loginForm = document.getElementById('loginForm');
const loginUsername = document.getElementById('loginUsername');
const loginPassword = document.getElementById('loginPassword');
const btnLoginSubmit = document.getElementById('btnLoginSubmit');
const loginErrorBox = document.getElementById('loginErrorBox');
const loginQuickBtns = document.querySelectorAll('.login-quick-btn');

// Elementos Header y Navegación
const langSelect = document.getElementById('langSelect');
const navTabs = document.querySelectorAll('.nav-tab');
const tabPanels = document.querySelectorAll('.tab-panel');

// Elementos RRHH (Gestión de Vendedores con Roles y Permisos)
const rrhhRestrictedNotice = document.getElementById('rrhhRestrictedNotice');
const rrhhAdminContent = document.getElementById('rrhhAdminContent');
const filtroVendedor = document.getElementById('filtroVendedor');
const btnRefrescarVendedores = document.getElementById('btnRefrescarVendedores');
const tablaVendedoresContainer = document.getElementById('tablaVendedoresContainer');

// Elementos Ventas Clientes
const btnConsultarClientes = document.getElementById('btnConsultarClientes');
const clientesContainer = document.getElementById('clientesContainer');

// Elementos Ventas Productos
const btnConsultarProductos = document.getElementById('btnConsultarProductos');
const productosContainer = document.getElementById('productosContainer');

// Elementos Bodega e Inventario
const btnRefrescarInventario = document.getElementById('btnRefrescarInventario');
const inventarioContainer = document.getElementById('inventarioContainer');
const proveedorCard = document.getElementById('proveedorCard');
const provCodigo = document.getElementById('provCodigo');
const provNombre = document.getElementById('provNombre');

// Elementos Modales de Inventario
const modalRecargarStock = document.getElementById('modalRecargarStock');
const formRecargarStock = document.getElementById('formRecargarStock');
const modalRecargarProductoNombre = document.getElementById('modalRecargarProductoNombre');
const modalRecargarProductoCodigo = document.getElementById('modalRecargarProductoCodigo');
const modalRecargarStockActual = document.getElementById('modalRecargarStockActual');
const inputCantidadRecargar = document.getElementById('inputCantidadRecargar');

const modalSalidaStock = document.getElementById('modalSalidaStock');
const formSalidaStock = document.getElementById('formSalidaStock');
const modalSalidaProductoNombre = document.getElementById('modalSalidaProductoNombre');
const modalSalidaProductoCodigo = document.getElementById('modalSalidaProductoCodigo');
const modalSalidaStockActual = document.getElementById('modalSalidaStockActual');
const inputCantidadSalida = document.getElementById('inputCantidadSalida');

// =========================================================================
// Ciclo de Vida e Inicialización
// =========================================================================

document.addEventListener('DOMContentLoaded', () => {
    configurarIdioma();
    configurarTabs();
    configurarLogin();
    configurarRrhh();
    configurarVentasClientes();
    configurarVentasProductos();
    configurarBodegaInventario();
    configurarModales();

    langSelect.value = state.idioma;
    aplicarTraduccion(state.idioma);

    evaluarEstadoSesion();
});

function evaluarEstadoSesion() {
    if (state.usuarioActivo) {
        mostrarAplicacion();
    } else {
        mostrarLogin();
    }
}

function mostrarLogin() {
    loginView.classList.remove('hidden');
    appView.classList.add('hidden');
    userSessionHeader.classList.add('hidden');
    loginErrorBox.classList.add('hidden');
    loginUsername.focus();
}

function mostrarAplicacion() {
    loginView.classList.add('hidden');
    appView.classList.remove('hidden');
    userSessionHeader.classList.remove('hidden');
    sessionUserName.textContent = `${state.usuarioActivo} (${state.rolActivo || 'USUARIO'})`;

    evaluarPermisosRrhh();

    // Cargar automáticamente clientes comerciales al ingresar
    if (!state.ultimosClientes) {
        consultarClientes(state.usuarioActivo);
    }
}

function evaluarPermisosRrhh() {
    const esRRHH = state.rolActivo === 'RECURSOS_HUMANOS' || state.permisosActivos.includes('GESTION_RRHH');
    if (esRRHH) {
        rrhhRestrictedNotice.classList.add('hidden');
        rrhhAdminContent.classList.remove('hidden');
        cargarVendedores();
    } else {
        rrhhRestrictedNotice.classList.remove('hidden');
        rrhhAdminContent.classList.add('hidden');
    }
}

function cerrarSesion(mensaje = null) {
    state.usuarioActivo = null;
    state.rolActivo = null;
    state.permisosActivos = [];
    sessionStorage.clear();

    state.vendedores = [];
    state.ultimosClientes = null;
    state.ultimosProductos = null;
    state.ultimoInventario = null;

    mostrarLogin();
    if (mensaje) {
        mostrarToast(mensaje, 'error');
    }
}

// =========================================================================
// Módulo de Autenticación (Login)
// =========================================================================

function configurarLogin() {
    loginQuickBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            loginUsername.value = btn.getAttribute('data-user');
            loginPassword.value = 'password123';
        });
    });

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = loginUsername.value.trim();
        const password = loginPassword.value.trim();

        if (!username || !password) return;

        try {
            btnLoginSubmit.disabled = true;
            btnLoginSubmit.textContent = t('msg.processing', state.idioma);
            loginErrorBox.classList.add('hidden');

            const response = await fetch(`${API_BASE}/auth/login`, {
                method: 'POST',
                headers: getHeaders(),
                body: JSON.stringify({ nombreUsuario: username, password: password })
            });

            const data = await response.json();

            if (response.ok && data.exitoso) {
                state.usuarioActivo = data.datos.nombreUsuario;
                state.rolActivo = data.datos.rol;
                state.permisosActivos = (data.datos.permisos || '')
                    .split(',')
                    .map(p => p.trim())
                    .filter(Boolean);

                sessionStorage.setItem('polimarket_user', state.usuarioActivo);
                sessionStorage.setItem('polimarket_role', state.rolActivo);
                sessionStorage.setItem('polimarket_permisos', JSON.stringify(state.permisosActivos));

                mostrarAplicacion();
                mostrarToast(data.mensaje, 'success');
            } else {
                loginErrorBox.textContent = data.mensaje;
                loginErrorBox.classList.remove('hidden');
                mostrarToast(data.mensaje, 'error');
            }
        } catch (error) {
            loginErrorBox.textContent = t('msg.conn.error', state.idioma);
            loginErrorBox.classList.remove('hidden');
        } finally {
            btnLoginSubmit.disabled = false;
            btnLoginSubmit.textContent = t('login.btn', state.idioma);
        }
    });

    btnLogout.addEventListener('click', () => {
        cerrarSesion('Sesión cerrada correctamente');
    });
}

// =========================================================================
// Motor de Internacionalización (i18n)
// =========================================================================

function configurarIdioma() {
    langSelect.addEventListener('change', (e) => {
        state.idioma = e.target.value;
        localStorage.setItem('polimarket_lang', state.idioma);

        aplicarTraduccion(state.idioma);

        if (state.vendedores && state.vendedores.length) renderizarTablaVendedores(state.vendedores);
        if (state.ultimosClientes) renderizarClientes(state.ultimosClientes);
        if (state.ultimosProductos) renderizarProductos(state.ultimosProductos);
        if (state.ultimoInventario) renderizarInventario(state.ultimoInventario);

        mostrarToast(t('msg.lang.changed', state.idioma), 'success');
    });
}

function aplicarTraduccion(lang) {
    document.documentElement.lang = lang;

    document.querySelectorAll('[data-i18n]').forEach(el => {
        const key = el.getAttribute('data-i18n');
        el.textContent = t(key, lang);
    });

    document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
        const key = el.getAttribute('data-i18n-placeholder');
        el.setAttribute('placeholder', t(key, lang));
    });
}

function getHeaders() {
    return {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Accept-Language': state.idioma
    };
}

// =========================================================================
// Pestañas (Tabs)
// =========================================================================

function configurarTabs() {
    navTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetTab = tab.getAttribute('data-tab');

            navTabs.forEach(t => t.classList.remove('active'));
            tabPanels.forEach(p => p.classList.remove('active'));

            tab.classList.add('active');
            const panel = document.getElementById(`panel-${targetTab}`);
            if (panel) panel.classList.add('active');

            if (targetTab === 'bodega-inventario' && !state.ultimoInventario) {
                cargarInventarioBodega();
            } else if (targetTab === 'rrhh') {
                evaluarPermisosRrhh();
            }
        });
    });
}

// =========================================================================
// Módulo: Recursos Humanos (Tabla de Vendedores con Permisos y Filtro)
// =========================================================================

function configurarRrhh() {
    btnRefrescarVendedores.addEventListener('click', cargarVendedores);

    filtroVendedor.addEventListener('input', () => {
        renderizarTablaVendedores(state.vendedores);
    });
}

async function cargarVendedores() {
    try {
        tablaVendedoresContainer.innerHTML = `<p class="placeholder-text">${t('msg.processing', state.idioma)}</p>`;
        const response = await fetch(`${API_BASE}/recursos-humanos/vendedores`, {
            method: 'GET',
            headers: getHeaders()
        });

        const data = await response.json();
        if (response.ok && data.exitoso) {
            state.vendedores = data.datos;
            renderizarTablaVendedores(data.datos);
        } else {
            tablaVendedoresContainer.innerHTML = `<p class="placeholder-text" style="color:red;">${data.mensaje}</p>`;
        }
    } catch (error) {
        tablaVendedoresContainer.innerHTML = `<p class="placeholder-text" style="color:red;">${t('msg.conn.error', state.idioma)}</p>`;
    }
}

function renderizarTablaVendedores(vendedores) {
    if (!vendedores || vendedores.length === 0) {
        tablaVendedoresContainer.innerHTML = '<p class="placeholder-text">No se encontraron usuarios registrados.</p>';
        return;
    }

    const filtro = filtroVendedor.value.trim().toLowerCase();
    const filtrados = vendedores.filter(v => v.nombreUsuario.toLowerCase().includes(filtro));

    if (filtrados.length === 0) {
        tablaVendedoresContainer.innerHTML = `<p class="placeholder-text">No se encontraron resultados para "${filtro}".</p>`;
        return;
    }

    tablaVendedoresContainer.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>${t('rrhh.th.user', state.idioma)}</th>
                    <th>${t('rrhh.th.role', state.idioma)}</th>
                    <th>${t('rrhh.th.permissions', state.idioma)}</th>
                    <th>${t('rrhh.th.status', state.idioma)}</th>
                    <th>${t('rrhh.th.actions', state.idioma)}</th>
                </tr>
            </thead>
            <tbody>
                ${filtrados.map(v => {
                    const badgeClass = v.autorizado ? 'badge-success' : 'badge-danger';
                    const badgeText = v.autorizado ? t('rrhh.status.authorized', state.idioma) : t('rrhh.status.pending', state.idioma);
                    const esAdmin = v.rol === 'RECURSOS_HUMANOS';

                    return `
                        <tr>
                            <td>
                                <div style="display:flex; align-items:center; gap:0.5rem;">
                                    <span style="font-size:1.3rem;">${esAdmin ? '👔' : '👤'}</span>
                                    <strong><code>${v.nombreUsuario}</code></strong>
                                </div>
                            </td>
                            <td>
                                <span class="badge ${esAdmin ? 'badge-primary' : 'badge-secondary'}">
                                    ${v.rol || 'VENDEDOR'}
                                </span>
                            </td>
                            <td>
                                <small class="text-muted">${v.permisos || 'VENTAS,BODEGA'}</small>
                            </td>
                            <td>
                                <span class="badge ${badgeClass}">${badgeText}</span>
                            </td>
                            <td>
                                ${!esAdmin ? `
                                    ${v.autorizado ? `
                                        <button class="btn btn-danger btn-sm" onclick="accionRevocarVendedor('${v.nombreUsuario}')">
                                            ${t('rrhh.btn.revocar', state.idioma)}
                                        </button>
                                    ` : `
                                        <button class="btn btn-success btn-sm" onclick="accionAutorizarVendedor('${v.nombreUsuario}')">
                                            ${t('rrhh.btn.autorizar', state.idioma)}
                                        </button>
                                    `}
                                ` : `
                                    <small class="text-muted">Protegido</small>
                                `}
                            </td>
                        </tr>
                    `;
                }).join('')}
            </tbody>
        </table>
    `;
}

window.accionAutorizarVendedor = async function(nombreUsuario) {
    try {
        const response = await fetch(`${API_BASE}/recursos-humanos/vendedores/${encodeURIComponent(nombreUsuario)}/autorizar`, {
            method: 'PATCH',
            headers: getHeaders()
        });

        const data = await response.json();
        if (response.ok && data.exitoso) {
            mostrarToast(data.mensaje, 'success');
            cargarVendedores();
        } else {
            mostrarToast(data.mensaje, 'error');
        }
    } catch (error) {
        mostrarToast(t('msg.conn.error', state.idioma), 'error');
    }
};

window.accionRevocarVendedor = async function(nombreUsuario) {
    try {
        const response = await fetch(`${API_BASE}/recursos-humanos/vendedores/${encodeURIComponent(nombreUsuario)}/revocar`, {
            method: 'PATCH',
            headers: getHeaders()
        });

        const data = await response.json();
        if (response.ok && data.exitoso) {
            mostrarToast(data.mensaje, 'success');
            cargarVendedores();

            // Si el usuario conectado fue revocado, cerrar su sesión
            if (state.usuarioActivo === nombreUsuario) {
                setTimeout(() => {
                    cerrarSesion('Su cuenta ha sido desautorizada/revocada por Recursos Humanos.');
                }, 1500);
            }
        } else {
            mostrarToast(data.mensaje, 'error');
        }
    } catch (error) {
        mostrarToast(t('msg.conn.error', state.idioma), 'error');
    }
};

// =========================================================================
// Módulo: Ventas - Clientes Potenciales
// =========================================================================

function configurarVentasClientes() {
    btnConsultarClientes.addEventListener('click', () => {
        if (!state.usuarioActivo) return;
        consultarClientes(state.usuarioActivo);
    });
}

async function consultarClientes(username) {
    try {
        btnConsultarClientes.disabled = true;
        clientesContainer.innerHTML = `<p class="placeholder-text">${t('ventas.clientes.loading', state.idioma)}</p>`;

        const response = await fetch(`${API_BASE}/ventas/clientes-potenciales?nombreUsuario=${encodeURIComponent(username)}`, {
            method: 'GET',
            headers: getHeaders()
        });

        const data = await response.json();

        if (response.status === 403) {
            cerrarSesion('Sesión no autorizada o revocada.');
            return;
        }

        if (response.ok && data.exitoso) {
            state.ultimosClientes = data.datos;
            renderizarClientes(data.datos, data.mensaje);
        } else {
            state.ultimosClientes = null;
            clientesContainer.innerHTML = `
                <div class="resultado-box error" style="grid-column: 1 / -1;">
                    <strong>${t('msg.access.denied', state.idioma)}</strong> ${data.mensaje}
                </div>
            `;
            mostrarToast(data.mensaje, 'error');
        }
    } catch (error) {
        clientesContainer.innerHTML = `<p class="placeholder-text" style="color:red;">${t('msg.conn.error', state.idioma)}</p>`;
    } finally {
        btnConsultarClientes.disabled = false;
    }
}

function renderizarClientes(clientes, mensaje = null) {
    if (!clientes || clientes.length === 0) {
        clientesContainer.innerHTML = `<p class="placeholder-text">${t('ventas.clientes.empty', state.idioma)}</p>`;
        return;
    }

    clientesContainer.innerHTML = clientes.map(cliente => `
        <div class="client-card">
            <div class="client-header">
                <div class="client-avatar">${cliente.nombre.charAt(0)}</div>
                <div class="client-name-box">
                    <h4>${cliente.nombre}</h4>
                    <span class="client-id">${t('ventas.clientes.card.id', state.idioma)}${cliente.id}</span>
                </div>
            </div>
            <div class="client-info-list">
                <div class="client-info-row">
                    <span class="info-icon">📧</span>
                    <span class="info-label">${t('ventas.clientes.card.email', state.idioma)}</span>
                    <a href="mailto:${cliente.correoElectronico}" class="info-value email-link">${cliente.correoElectronico}</a>
                </div>
                <div class="client-info-row">
                    <span class="info-icon">📞</span>
                    <span class="info-label">${t('ventas.clientes.card.phone', state.idioma)}</span>
                    <a href="tel:${cliente.telefono}" class="info-value phone-link">${cliente.telefono}</a>
                </div>
                <div class="client-info-row">
                    <span class="info-icon">📍</span>
                    <span class="info-label">${t('ventas.clientes.card.city', state.idioma)}</span>
                    <span class="info-value city-tag">${cliente.ciudad}</span>
                </div>
            </div>
        </div>
    `).join('');

    if (mensaje) mostrarToast(mensaje, 'success');
}

// =========================================================================
// Módulo: Ventas - Productos Disponibles
// =========================================================================

function configurarVentasProductos() {
    btnConsultarProductos.addEventListener('click', () => {
        if (!state.usuarioActivo) return;
        consultarProductosDisponibles(state.usuarioActivo);
    });
}

async function consultarProductosDisponibles(username) {
    try {
        btnConsultarProductos.disabled = true;
        productosContainer.innerHTML = `<p class="placeholder-text">${t('ventas.productos.loading', state.idioma)}</p>`;

        const response = await fetch(`${API_BASE}/ventas/productos-disponibles?nombreUsuario=${encodeURIComponent(username)}`, {
            method: 'GET',
            headers: getHeaders()
        });

        const data = await response.json();

        if (response.status === 403) {
            cerrarSesion('Sesión no autorizada o revocada.');
            return;
        }

        if (response.ok && data.exitoso) {
            state.ultimosProductos = data.datos;
            renderizarProductos(data.datos, data.mensaje);
        } else {
            state.ultimosProductos = null;
            productosContainer.innerHTML = `
                <div class="resultado-box error">
                    <strong>${t('msg.access.denied', state.idioma)}</strong> ${data.mensaje}
                </div>
            `;
            mostrarToast(data.mensaje, 'error');
        }
    } catch (error) {
        productosContainer.innerHTML = `<p class="placeholder-text" style="color:red;">${t('msg.conn.error', state.idioma)}</p>`;
    } finally {
        btnConsultarProductos.disabled = false;
    }
}

function renderizarProductos(productos, mensaje = null) {
    if (!productos || productos.length === 0) {
        productosContainer.innerHTML = `<p class="placeholder-text">${t('ventas.productos.empty', state.idioma)}</p>`;
        return;
    }

    productosContainer.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>${t('table.th.code', state.idioma)}</th>
                    <th>${t('table.th.name', state.idioma)}</th>
                    <th>${t('table.th.stock', state.idioma)}</th>
                    <th>${t('table.th.status', state.idioma)}</th>
                </tr>
            </thead>
            <tbody>
                ${productos.map(p => `
                    <tr>
                        <td><code>${p.codigoProducto}</code></td>
                        <td><strong>${p.nombreProducto}</strong></td>
                        <td><span class="stock-pill in-stock">📦 ${p.cantidadDisponible} ${t('table.units', state.idioma)}</span></td>
                        <td><span class="badge badge-success">${t('bodega.status.available', state.idioma)}</span></td>
                    </tr>
                `).join('')}
            </tbody>
        </table>
    `;

    if (mensaje) mostrarToast(mensaje, 'success');
}

// =========================================================================
// Módulo: Bodega e Inventario
// =========================================================================

function configurarBodegaInventario() {
    btnRefrescarInventario.addEventListener('click', cargarInventarioBodega);
}

async function cargarInventarioBodega() {
    try {
        inventarioContainer.innerHTML = '<p class="placeholder-text">Cargando inventario completo...</p>';
        proveedorCard.classList.add('hidden');

        const response = await fetch(`${API_BASE}/bodega/existencias?incluirAgotados=true`, {
            method: 'GET',
            headers: getHeaders()
        });

        const data = await response.json();
        if (response.ok && data.exitoso) {
            state.ultimoInventario = data.datos;
            renderizarInventario(data.datos);
        } else {
            inventarioContainer.innerHTML = `<p class="placeholder-text" style="color:red;">${data.mensaje}</p>`;
        }
    } catch (error) {
        inventarioContainer.innerHTML = '<p class="placeholder-text" style="color:red;">Error de conexión con la API.</p>';
    }
}

function renderizarInventario(inventario) {
    if (!inventario || inventario.length === 0) {
        inventarioContainer.innerHTML = '<p class="placeholder-text">No se encontraron productos en bodega.</p>';
        return;
    }

    inventarioContainer.innerHTML = `
        <table>
            <thead>
                <tr>
                    <th>${t('bodega.th.code', state.idioma)}</th>
                    <th>${t('bodega.th.name', state.idioma)}</th>
                    <th>${t('bodega.th.stock', state.idioma)}</th>
                    <th>${t('bodega.th.status', state.idioma)}</th>
                    <th>${t('bodega.th.actions', state.idioma)}</th>
                </tr>
            </thead>
            <tbody>
                ${inventario.map(item => {
                    const esAgotado = item.cantidadDisponible === 0;
                    const esBajo = item.cantidadDisponible > 0 && item.cantidadDisponible < 10;
                    const badgeClass = esAgotado ? 'badge-danger' : (esBajo ? 'badge-warning' : 'badge-success');
                    const badgeText = esAgotado ? t('bodega.status.out', state.idioma) : (esBajo ? t('bodega.status.low', state.idioma) : t('bodega.status.available', state.idioma));
                    const pillClass = esAgotado ? 'out-stock' : 'in-stock';

                    const safeNombre = item.nombreProducto.replace(/'/g, "\\'");

                    return `
                        <tr>
                            <td><code>${item.codigoProducto}</code></td>
                            <td><strong>${item.nombreProducto}</strong></td>
                            <td><span class="stock-pill ${pillClass}">📦 ${item.cantidadDisponible} ${t('table.units', state.idioma)}</span></td>
                            <td><span class="badge ${badgeClass}">${badgeText}</span></td>
                            <td>
                                <div class="stock-actions">
                                    ${!esAgotado ? `
                                        <button class="btn btn-secondary btn-sm" onclick="abrirModalSalida('${item.codigoProducto}', '${safeNombre}', ${item.cantidadDisponible})">
                                            ${t('bodega.btn.retirar', state.idioma)}
                                        </button>
                                    ` : ''}
                                    <button class="btn btn-primary btn-sm" onclick="abrirModalRecargar('${item.codigoProducto}', '${safeNombre}', ${item.cantidadDisponible})">
                                        ${t('bodega.btn.recargar', state.idioma)}
                                    </button>
                                    ${esAgotado ? `
                                        <button class="btn btn-danger btn-sm" onclick="accionIdentificarProveedor('${item.codigoProducto}')">
                                            ${t('bodega.btn.proveedor', state.idioma)}
                                        </button>
                                    ` : ''}
                                </div>
                            </td>
                        </tr>
                    `;
                }).join('')}
            </tbody>
        </table>
    `;
}

window.accionIdentificarProveedor = async function(codigoProducto) {
    try {
        const response = await fetch(`${API_BASE}/bodega/productos/${encodeURIComponent(codigoProducto)}/proveedor-reposicion`, {
            method: 'GET',
            headers: getHeaders()
        });

        const data = await response.json();
        if (response.ok && data.exitoso) {
            provCodigo.textContent = data.datos.codigo;
            provNombre.textContent = data.datos.nombre;
            proveedorCard.classList.remove('hidden');
            proveedorCard.scrollIntoView({ behavior: 'smooth' });
            mostrarToast(data.mensaje, 'success');
        } else {
            mostrarToast(data.mensaje, 'error');
        }
    } catch (error) {
        mostrarToast(t('msg.conn.error', state.idioma), 'error');
    }
};

// =========================================================================
// Módulo: Modales para Aumentar y Disminuir Stock
// =========================================================================

function configurarModales() {
    // Cerrar al pulsar botón 'X' o botón 'Cancelar'
    document.querySelectorAll('.btn-cerrar-modal').forEach(btn => {
        btn.addEventListener('click', cerrarModales);
    });

    // Cerrar al hacer clic en el fondo oscuro
    [modalRecargarStock, modalSalidaStock].forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) cerrarModales();
        });
    });

    // Cerrar al presionar tecla Escape
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') cerrarModales();
    });

    // Formulario Aumentar Stock (Ingreso de Mercancía)
    formRecargarStock.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!state.productoSeleccionadoModal) return;

        const cantidad = parseInt(inputCantidadRecargar.value, 10);
        if (isNaN(cantidad) || cantidad <= 0) {
            mostrarToast('Por favor ingrese una cantidad válida mayor a cero', 'error');
            return;
        }

        try {
            const codigo = state.productoSeleccionadoModal.codigo;
            const response = await fetch(`${API_BASE}/bodega/productos/${encodeURIComponent(codigo)}/entrada?cantidad=${cantidad}`, {
                method: 'POST',
                headers: getHeaders()
            });

            const data = await response.json();
            if (response.ok && data.exitoso) {
                mostrarToast(data.mensaje, 'success');
                proveedorCard.classList.add('hidden');
                cerrarModales();
                cargarInventarioBodega();
            } else {
                mostrarToast(data.mensaje, 'error');
            }
        } catch (error) {
            mostrarToast(t('msg.conn.error', state.idioma), 'error');
        }
    });

    // Formulario Disminuir Stock (Salida de Mercancía)
    formSalidaStock.addEventListener('submit', async (e) => {
        e.preventDefault();
        if (!state.productoSeleccionadoModal) return;

        const cantidad = parseInt(inputCantidadSalida.value, 10);
        if (isNaN(cantidad) || cantidad <= 0) {
            mostrarToast('Por favor ingrese una cantidad válida mayor a cero', 'error');
            return;
        }

        try {
            const codigo = state.productoSeleccionadoModal.codigo;
            const response = await fetch(`${API_BASE}/bodega/productos/${encodeURIComponent(codigo)}/salida?cantidad=${cantidad}`, {
                method: 'POST',
                headers: getHeaders()
            });

            const data = await response.json();
            if (response.ok && data.exitoso) {
                mostrarToast(data.mensaje, 'success');
                cerrarModales();
                cargarInventarioBodega();
            } else {
                mostrarToast(data.mensaje, 'error');
            }
        } catch (error) {
            mostrarToast(t('msg.conn.error', state.idioma), 'error');
        }
    });
}

window.abrirModalRecargar = function(codigo, nombre, stock) {
    state.productoSeleccionadoModal = { codigo, nombre, stock };
    modalRecargarProductoCodigo.textContent = codigo;
    modalRecargarProductoNombre.textContent = nombre;
    modalRecargarStockActual.textContent = `${stock} ${t('table.units', state.idioma)}`;
    inputCantidadRecargar.value = '25';

    modalRecargarStock.classList.remove('hidden');
    inputCantidadRecargar.focus();
};

window.abrirModalSalida = function(codigo, nombre, stock) {
    state.productoSeleccionadoModal = { codigo, nombre, stock };
    modalSalidaProductoCodigo.textContent = codigo;
    modalSalidaProductoNombre.textContent = nombre;
    modalSalidaStockActual.textContent = `${stock} ${t('table.units', state.idioma)}`;
    inputCantidadSalida.value = '1';
    inputCantidadSalida.max = stock;

    modalSalidaStock.classList.remove('hidden');
    inputCantidadSalida.focus();
};

function cerrarModales() {
    modalRecargarStock.classList.add('hidden');
    modalSalidaStock.classList.add('hidden');
    state.productoSeleccionadoModal = null;
}

// =========================================================================
// Mensajes Flotantes (Toasts)
// =========================================================================

function mostrarToast(mensaje, tipo = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast ${tipo}`;
    toast.innerHTML = `<span>${tipo === 'success' ? '✓' : '✕'}</span> ${mensaje}`;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(-10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

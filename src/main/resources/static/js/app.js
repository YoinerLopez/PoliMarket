/**
 * PoliMarket - Cliente Web (JavaScript)
 * Consume los componentes REST de Autenticación, Ventas, Bodega y Recursos Humanos.
 */

const API_BASE = '/api/v1';
const STOCK_BAJO = 10;

const state = {
    idioma: leerStorage(localStorage, 'polimarket_lang') || 'es',
    usuarioActivo: leerStorage(sessionStorage, 'polimarket_user'),
    rolActivo: leerStorage(sessionStorage, 'polimarket_role'),
    permisosActivos: JSON.parse(leerStorage(sessionStorage, 'polimarket_permisos') || '[]'),
    vendedores: null,
    ultimosClientes: null,
    ultimosProductos: null,
    ultimoInventario: null,
    filtroEstado: 'todos',
    productoSeleccionadoModal: null
};

const $ = (id) => document.getElementById(id);

// Layout y sesión
const loginView = $('loginView');
const appView = $('appView');
const userSessionHeader = $('userSessionHeader');
const loginForm = $('loginForm');
const loginUsername = $('loginUsername');
const loginPassword = $('loginPassword');
const btnLoginSubmit = $('btnLoginSubmit');
const loginErrorBox = $('loginErrorBox');
const langSelect = $('langSelect');
const navTabs = document.querySelectorAll('.nav-tab[data-tab]');
const tabPanels = document.querySelectorAll('.tab-panel');

// Contenedores
const clientesContainer = $('clientesContainer');
const productosContainer = $('productosContainer');
const inventarioContainer = $('inventarioContainer');
const tablaVendedoresContainer = $('tablaVendedoresContainer');
const proveedorCard = $('proveedorCard');

// Modales
const modalRecargarStock = $('modalRecargarStock');
const modalSalidaStock = $('modalSalidaStock');
const inputCantidadRecargar = $('inputCantidadRecargar');
const inputCantidadSalida = $('inputCantidadSalida');

// =========================================================================
// Utilidades
// =========================================================================

function leerStorage(storage, clave) {
    try { return storage.getItem(clave); } catch { return null; }
}

function guardarStorage(storage, clave, valor) {
    try { storage.setItem(clave, valor); } catch { /* almacenamiento no disponible */ }
}

const tr = (key) => t(key, state.idioma);

function esc(valor) {
    return String(valor ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function normalizar(texto) {
    return String(texto ?? '').toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');
}

function getHeaders() {
    return {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Accept-Language': state.idioma
    };
}

async function api(ruta, opciones = {}) {
    const response = await fetch(`${API_BASE}${ruta}`, { headers: getHeaders(), ...opciones });
    const data = await response.json().catch(() => ({ exitoso: false, mensaje: tr('msg.conn.error') }));
    return { ok: response.ok && data.exitoso, status: response.status, data };
}

function placeholder(contenedor, texto, esError = false) {
    contenedor.innerHTML = `<p class="placeholder-text${esError ? ' error' : ''}">${esc(texto)}</p>`;
}

function estadoStock(cantidad) {
    if (cantidad === 0) return 'agotado';
    if (cantidad < STOCK_BAJO) return 'bajo';
    return 'disponible';
}

const ESTADO_UI = {
    disponible: { badge: 'badge-success', texto: 'bodega.status.available', barra: '' },
    bajo: { badge: 'badge-warning', texto: 'bodega.status.low', barra: 'low' },
    agotado: { badge: 'badge-danger', texto: 'bodega.status.out', barra: 'out' }
};

function celdaStock(cantidad, maximo) {
    const estado = estadoStock(cantidad);
    const pct = maximo > 0 ? Math.max(4, Math.round((cantidad / maximo) * 100)) : 0;
    return `
        <div class="stock-cell">
            <span class="stock-bar ${ESTADO_UI[estado].barra} hide-sm"><i style="width:${pct}%"></i></span>
            <b>${cantidad}</b>
        </div>`;
}

// =========================================================================
// Inicio
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

    if (state.usuarioActivo) mostrarAplicacion(); else mostrarLogin();
});

function esRrhh() {
    return state.rolActivo === 'RECURSOS_HUMANOS' || state.permisosActivos.includes('GESTION_RRHH');
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

    $('sessionUserName').textContent = state.usuarioActivo;
    $('sessionUserRole').textContent = (state.rolActivo || 'VENDEDOR').replace(/_/g, ' ').toLowerCase();
    $('sessionUserAvatar').textContent = state.usuarioActivo.charAt(0);

    // Solo RRHH ve el módulo de administración de vendedores
    $('navTabRrhh').classList.toggle('hidden', !esRrhh());
    $('navSectionAdmin').classList.toggle('hidden', !esRrhh());

    activarTab('ventas-clientes');
}

function cerrarSesion(mensaje = null, tipo = 'error') {
    state.usuarioActivo = null;
    state.rolActivo = null;
    state.permisosActivos = [];
    try { sessionStorage.clear(); } catch { /* sin almacenamiento */ }

    state.vendedores = null;
    state.ultimosClientes = null;
    state.ultimosProductos = null;
    state.ultimoInventario = null;
    loginPassword.value = '';
    proveedorCard.classList.add('hidden');
    cerrarModales();

    mostrarLogin();
    if (mensaje) mostrarToast(mensaje, tipo);
}

// =========================================================================
// Autenticación
// =========================================================================

function configurarLogin() {
    document.querySelectorAll('.login-quick-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.login-quick-btn').forEach(b => b.classList.remove('selected'));
            btn.classList.add('selected');
            loginUsername.value = btn.dataset.user;
            loginPassword.value = 'password123';
            btnLoginSubmit.focus();
        });
    });

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = loginUsername.value.trim();
        const password = loginPassword.value.trim();
        if (!username || !password) return;

        try {
            btnLoginSubmit.disabled = true;
            btnLoginSubmit.textContent = tr('msg.processing');
            loginErrorBox.classList.add('hidden');

            const { ok, data } = await api('/auth/login', {
                method: 'POST',
                body: JSON.stringify({ nombreUsuario: username, password })
            });

            if (ok) {
                state.usuarioActivo = data.datos.nombreUsuario;
                state.rolActivo = data.datos.rol;
                state.permisosActivos = (data.datos.permisos || '').split(',').map(p => p.trim()).filter(Boolean);

                guardarStorage(sessionStorage, 'polimarket_user', state.usuarioActivo);
                guardarStorage(sessionStorage, 'polimarket_role', state.rolActivo);
                guardarStorage(sessionStorage, 'polimarket_permisos', JSON.stringify(state.permisosActivos));

                mostrarAplicacion();
                mostrarToast(data.mensaje, 'success');
            } else {
                loginErrorBox.textContent = data.mensaje;
                loginErrorBox.classList.remove('hidden');
            }
        } catch {
            loginErrorBox.textContent = tr('msg.conn.error');
            loginErrorBox.classList.remove('hidden');
        } finally {
            btnLoginSubmit.disabled = false;
            btnLoginSubmit.textContent = tr('login.btn');
        }
    });

    $('btnLogout').addEventListener('click', () => cerrarSesion(tr('login.logout.msg'), 'success'));
}

// =========================================================================
// Idioma
// =========================================================================

function configurarIdioma() {
    langSelect.addEventListener('change', (e) => {
        state.idioma = e.target.value;
        guardarStorage(localStorage, 'polimarket_lang', state.idioma);
        aplicarTraduccion(state.idioma);

        if (state.vendedores) renderizarTablaVendedores();
        if (state.ultimosClientes) renderizarClientes();
        if (state.ultimosProductos) renderizarProductos();
        if (state.ultimoInventario) renderizarInventario();
    });
}

function aplicarTraduccion(lang) {
    document.documentElement.lang = lang;
    document.querySelectorAll('[data-i18n]').forEach(el => {
        el.textContent = t(el.getAttribute('data-i18n'), lang);
    });
    document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
        el.setAttribute('placeholder', t(el.getAttribute('data-i18n-placeholder'), lang));
    });
}

// =========================================================================
// Navegación
// =========================================================================

function configurarTabs() {
    navTabs.forEach(tab => tab.addEventListener('click', () => activarTab(tab.dataset.tab)));
}

function activarTab(destino) {
    navTabs.forEach(t => t.classList.toggle('active', t.dataset.tab === destino));
    tabPanels.forEach(p => p.classList.toggle('active', p.id === `panel-${destino}`));

    // Cada módulo se carga solo la primera vez que se abre
    if (destino === 'ventas-clientes' && !state.ultimosClientes) consultarClientes();
    if (destino === 'ventas-productos' && !state.ultimosProductos) consultarProductosDisponibles();
    if (destino === 'bodega-inventario' && !state.ultimoInventario) cargarInventarioBodega();
    if (destino === 'rrhh') evaluarPermisosRrhh();
}

// =========================================================================
// Recursos Humanos
// =========================================================================

function configurarRrhh() {
    $('btnRefrescarVendedores').addEventListener('click', cargarVendedores);
    $('filtroVendedor').addEventListener('input', renderizarTablaVendedores);

    tablaVendedoresContainer.addEventListener('click', (e) => {
        const btn = e.target.closest('button[data-accion]');
        if (btn) cambiarAutorizacion(btn.dataset.usuario, btn.dataset.accion, btn);
    });
}

function evaluarPermisosRrhh() {
    const permitido = esRrhh();
    $('rrhhRestrictedNotice').classList.toggle('hidden', permitido);
    $('rrhhAdminContent').classList.toggle('hidden', !permitido);
    $('btnRefrescarVendedores').classList.toggle('hidden', !permitido);
    if (permitido && !state.vendedores) cargarVendedores();
}

async function cargarVendedores() {
    placeholder(tablaVendedoresContainer, tr('msg.processing'));
    try {
        const { ok, data } = await api('/recursos-humanos/vendedores');
        if (ok) {
            state.vendedores = data.datos;
            renderizarTablaVendedores();
        } else {
            placeholder(tablaVendedoresContainer, data.mensaje, true);
        }
    } catch {
        placeholder(tablaVendedoresContainer, tr('msg.conn.error'), true);
    }
}

function renderizarTablaVendedores() {
    const vendedores = state.vendedores || [];
    const comerciales = vendedores.filter(v => v.rol !== 'RECURSOS_HUMANOS');
    const autorizados = comerciales.filter(v => v.autorizado).length;

    $('rrhhStats').innerHTML = `
        <div class="stat"><small>${tr('rrhh.stat.total')}</small><b>${comerciales.length}</b></div>
        <div class="stat"><small>${tr('rrhh.stat.ok')}</small><b>${autorizados}</b></div>
        <div class="stat"><small>${tr('rrhh.stat.pending')}</small><b>${comerciales.length - autorizados}</b></div>`;

    if (vendedores.length === 0) return placeholder(tablaVendedoresContainer, tr('rrhh.empty'));

    const filtro = normalizar($('filtroVendedor').value.trim());
    const filtrados = vendedores.filter(v => normalizar(v.nombreUsuario).includes(filtro));
    if (filtrados.length === 0) return placeholder(tablaVendedoresContainer, tr('common.no.results'));

    tablaVendedoresContainer.innerHTML = `
        <table>
            <thead><tr>
                <th>${tr('rrhh.th.user')}</th>
                <th class="hide-sm">${tr('rrhh.th.permissions')}</th>
                <th>${tr('rrhh.th.status')}</th>
                <th></th>
            </tr></thead>
            <tbody>
                ${filtrados.map(v => {
                    const esAdmin = v.rol === 'RECURSOS_HUMANOS';
                    const usuario = esc(v.nombreUsuario);
                    const estado = esAdmin
                        ? `<span class="badge badge-primary">${esc((v.rol || '').replace(/_/g, ' '))}</span>`
                        : `<span class="badge ${v.autorizado ? 'badge-success' : 'badge-neutral'}">${tr(v.autorizado ? 'rrhh.status.authorized' : 'rrhh.status.pending')}</span>`;
                    const accion = esAdmin
                        ? `<small class="text-muted">${tr('rrhh.protected')}</small>`
                        : v.autorizado
                            ? `<button class="btn-link danger" data-accion="revocar" data-usuario="${usuario}">${tr('rrhh.btn.revocar')}</button>`
                            : `<button class="btn btn-primary btn-sm" data-accion="autorizar" data-usuario="${usuario}">${tr('rrhh.btn.autorizar')}</button>`;
                    return `
                        <tr>
                            <td>
                                <div class="cell-main">
                                    <span class="user-avatar ${v.autorizado || esAdmin ? '' : 'muted'}">${esc(v.nombreUsuario.charAt(0))}</span>
                                    <div><strong>${usuario}</strong><small>${esc((v.rol || 'VENDEDOR').replace(/_/g, ' ').toLowerCase())}</small></div>
                                </div>
                            </td>
                            <td class="hide-sm"><small class="text-muted">${esc((v.permisos || '').split(',').join(' · '))}</small></td>
                            <td>${estado}</td>
                            <td class="actions">${accion}</td>
                        </tr>`;
                }).join('')}
            </tbody>
        </table>`;
}

async function cambiarAutorizacion(nombreUsuario, accion, boton) {
    boton.disabled = true;
    try {
        const { ok, data } = await api(`/recursos-humanos/vendedores/${encodeURIComponent(nombreUsuario)}/${accion}`, { method: 'PATCH' });
        mostrarToast(data.mensaje, ok ? 'success' : 'error');
        if (!ok) return;

        await cargarVendedores();
        if (accion === 'revocar' && state.usuarioActivo === nombreUsuario) {
            setTimeout(() => cerrarSesion(tr('rrhh.revoked.self')), 1500);
        }
    } catch {
        mostrarToast(tr('msg.conn.error'), 'error');
    } finally {
        boton.disabled = false;
    }
}

// =========================================================================
// Ventas - Clientes Potenciales
// =========================================================================

function configurarVentasClientes() {
    $('btnConsultarClientes').addEventListener('click', consultarClientes);
    $('filtroCliente').addEventListener('input', renderizarClientes);
    $('filtroCiudad').addEventListener('change', renderizarClientes);

    clientesContainer.addEventListener('click', (e) => {
        const btn = e.target.closest('button[data-copiar]');
        if (!btn) return;
        navigator.clipboard?.writeText(btn.dataset.copiar)
            .then(() => mostrarToast(tr('ventas.clientes.copied'), 'success'))
            .catch(() => {});
    });
}

async function consultarClientes() {
    if (!state.usuarioActivo) return;
    const boton = $('btnConsultarClientes');
    boton.disabled = true;
    placeholder(clientesContainer, tr('ventas.clientes.loading'));

    try {
        const { ok, data } = await api(`/ventas/clientes-potenciales?nombreUsuario=${encodeURIComponent(state.usuarioActivo)}`);
        if (ok) {
            state.ultimosClientes = data.datos;
            llenarCiudades(data.datos);
            renderizarClientes();
        } else {
            state.ultimosClientes = null;
            placeholder(clientesContainer, `${tr('msg.access.denied')} ${data.mensaje}`, true);
        }
    } catch {
        placeholder(clientesContainer, tr('msg.conn.error'), true);
    } finally {
        boton.disabled = false;
    }
}

function llenarCiudades(clientes) {
    const select = $('filtroCiudad');
    const actual = select.value;
    const ciudades = [...new Set(clientes.map(c => c.ciudad).filter(Boolean))].sort();
    select.innerHTML = `<option value="" data-i18n="ventas.clientes.allCities">${tr('ventas.clientes.allCities')}</option>`
        + ciudades.map(c => `<option value="${esc(c)}">${esc(c)}</option>`).join('');
    select.value = ciudades.includes(actual) ? actual : '';
}

function renderizarClientes() {
    const clientes = state.ultimosClientes || [];
    if (clientes.length === 0) return placeholder(clientesContainer, tr('ventas.clientes.empty'));

    const filtro = normalizar($('filtroCliente').value.trim());
    const ciudad = $('filtroCiudad').value;
    const filtrados = clientes.filter(c => {
        const email = c.correo || c.correoElectronico || '';
        const texto = normalizar(`${c.nombre} ${email} ${c.telefono}`);
        return texto.includes(filtro) && (!ciudad || c.ciudad === ciudad);
    });
    if (filtrados.length === 0) return placeholder(clientesContainer, tr('common.no.results'));

    clientesContainer.innerHTML = `
        <table>
            <thead><tr>
                <th>${tr('ventas.clientes.th.client')}</th>
                <th>${tr('ventas.clientes.th.email')}</th>
                <th>${tr('ventas.clientes.th.phone')}</th>
                <th>${tr('ventas.clientes.th.city')}</th>
                <th></th>
            </tr></thead>
            <tbody>
                ${filtrados.map(c => {
                    const email = c.correo || c.correoElectronico || '';
                    const contacto = `${c.nombre} · ${email} · ${c.telefono || ''}`;
                    return `
                        <tr>
                            <td>
                                <div class="cell-main">
                                    <span class="user-avatar">${esc(c.nombre.charAt(0))}</span>
                                    <div><strong>${esc(c.nombre)}</strong><small>#${esc(c.id)}</small></div>
                                </div>
                            </td>
                            <td><a class="contact-link" href="mailto:${esc(email)}">${esc(email)}</a></td>
                            <td style="white-space:nowrap"><a class="contact-link" href="tel:${esc((c.telefono || '').replace(/\s/g, ''))}">${esc(c.telefono)}</a></td>
                            <td><span class="badge badge-neutral">${esc(c.ciudad)}</span></td>
                            <td class="actions"><button class="btn-link" data-copiar="${esc(contacto)}">${tr('ventas.clientes.copy')}</button></td>
                        </tr>`;
                }).join('')}
            </tbody>
        </table>
        <div class="table-footer">${filtrados.length} / ${clientes.length} ${tr('ventas.clientes.count')}</div>`;
}

// =========================================================================
// Ventas - Productos Disponibles
// =========================================================================

function configurarVentasProductos() {
    $('btnConsultarProductos').addEventListener('click', consultarProductosDisponibles);
    $('filtroProducto').addEventListener('input', renderizarProductos);
}

async function consultarProductosDisponibles() {
    if (!state.usuarioActivo) return;
    const boton = $('btnConsultarProductos');
    boton.disabled = true;
    placeholder(productosContainer, tr('ventas.productos.loading'));

    try {
        const { ok, data } = await api(`/ventas/productos-disponibles?nombreUsuario=${encodeURIComponent(state.usuarioActivo)}`);
        if (ok) {
            state.ultimosProductos = data.datos;
            renderizarProductos();
        } else {
            state.ultimosProductos = null;
            placeholder(productosContainer, `${tr('msg.access.denied')} ${data.mensaje}`, true);
        }
    } catch {
        placeholder(productosContainer, tr('msg.conn.error'), true);
    } finally {
        boton.disabled = false;
    }
}

function renderizarProductos() {
    const productos = state.ultimosProductos || [];
    if (productos.length === 0) return placeholder(productosContainer, tr('ventas.productos.empty'));

    const filtro = normalizar($('filtroProducto').value.trim());
    const filtrados = productos.filter(p => normalizar(`${p.codigoProducto} ${p.nombreProducto}`).includes(filtro));
    if (filtrados.length === 0) return placeholder(productosContainer, tr('common.no.results'));

    const maximo = Math.max(...productos.map(p => p.cantidadDisponible));
    productosContainer.innerHTML = `
        <table>
            <thead><tr>
                <th>${tr('table.th.name')}</th>
                <th>${tr('table.th.status')}</th>
                <th class="num">${tr('table.th.stock')}</th>
            </tr></thead>
            <tbody>
                ${filtrados.map(p => {
                    const ui = ESTADO_UI[estadoStock(p.cantidadDisponible)];
                    return `
                        <tr>
                            <td><div class="cell-main"><div><strong>${esc(p.nombreProducto)}</strong><small>${esc(p.codigoProducto)}</small></div></div></td>
                            <td><span class="badge ${ui.badge}">${tr(ui.texto)}</span></td>
                            <td class="num">${celdaStock(p.cantidadDisponible, maximo)}</td>
                        </tr>`;
                }).join('')}
            </tbody>
        </table>`;
}

// =========================================================================
// Bodega e Inventario
// =========================================================================

function configurarBodegaInventario() {
    $('btnRefrescarInventario').addEventListener('click', cargarInventarioBodega);
    $('filtroInventario').addEventListener('input', renderizarInventario);
    $('btnCerrarProveedor').addEventListener('click', () => proveedorCard.classList.add('hidden'));

    document.querySelectorAll('#filtroEstadoInventario .seg').forEach(seg => {
        seg.addEventListener('click', () => {
            state.filtroEstado = seg.dataset.estado;
            document.querySelectorAll('#filtroEstadoInventario .seg').forEach(s => s.classList.toggle('active', s === seg));
            renderizarInventario();
        });
    });

    inventarioContainer.addEventListener('click', (e) => {
        const btn = e.target.closest('button[data-accion]');
        if (!btn) return;
        const item = (state.ultimoInventario || []).find(i => i.codigoProducto === btn.dataset.codigo);
        if (!item) return;
        if (btn.dataset.accion === 'entrada') abrirModal('entrada', item);
        if (btn.dataset.accion === 'salida') abrirModal('salida', item);
        if (btn.dataset.accion === 'proveedor') identificarProveedor(item);
    });
}

async function cargarInventarioBodega() {
    placeholder(inventarioContainer, tr('bodega.loading'));
    try {
        const { ok, data } = await api('/bodega/existencias?incluirAgotados=true');
        if (ok) {
            state.ultimoInventario = data.datos;
            state.ultimosProductos = null; // el catálogo de ventas se recarga con el nuevo stock
            renderizarInventario();
        } else {
            placeholder(inventarioContainer, data.mensaje, true);
        }
    } catch {
        placeholder(inventarioContainer, tr('msg.conn.error'), true);
    }
}

function renderizarInventario() {
    const inventario = state.ultimoInventario || [];

    const conteo = { todos: inventario.length, disponible: 0, bajo: 0, agotado: 0 };
    inventario.forEach(i => conteo[estadoStock(i.cantidadDisponible)]++);
    // "Disponibles" incluye todo lo que tiene stock, también lo que está bajo
    conteo.disponible += conteo.bajo;
    document.querySelectorAll('#filtroEstadoInventario [data-count]').forEach(b => { b.textContent = conteo[b.dataset.count]; });

    if (inventario.length === 0) return placeholder(inventarioContainer, tr('bodega.empty'));

    const filtro = normalizar($('filtroInventario').value.trim());
    const filtrados = inventario.filter(i => {
        const estado = estadoStock(i.cantidadDisponible);
        const coincideEstado = state.filtroEstado === 'todos'
            || estado === state.filtroEstado
            || (state.filtroEstado === 'disponible' && estado === 'bajo');
        return coincideEstado && normalizar(`${i.codigoProducto} ${i.nombreProducto}`).includes(filtro);
    });
    if (filtrados.length === 0) return placeholder(inventarioContainer, tr('common.no.results'));

    const maximo = Math.max(...inventario.map(i => i.cantidadDisponible));
    inventarioContainer.innerHTML = `
        <table>
            <thead><tr>
                <th>${tr('bodega.th.name')}</th>
                <th>${tr('bodega.th.status')}</th>
                <th class="num">${tr('bodega.th.stock')}</th>
                <th></th>
            </tr></thead>
            <tbody>
                ${filtrados.map(item => {
                    const estado = estadoStock(item.cantidadDisponible);
                    const ui = ESTADO_UI[estado];
                    const codigo = esc(item.codigoProducto);
                    return `
                        <tr>
                            <td><div class="cell-main"><div><strong>${esc(item.nombreProducto)}</strong><small>${codigo}</small></div></div></td>
                            <td><span class="badge ${ui.badge}">${tr(ui.texto)}</span></td>
                            <td class="num">${celdaStock(item.cantidadDisponible, maximo)}</td>
                            <td class="actions">
                                ${estado === 'agotado' ? `<button class="btn-link" data-accion="proveedor" data-codigo="${codigo}">${tr('bodega.btn.proveedor')}</button>` : ''}
                                ${estado !== 'agotado' ? `<button class="btn-link danger" data-accion="salida" data-codigo="${codigo}">${tr('bodega.btn.retirar')}</button>` : ''}
                                <button class="btn btn-secondary btn-sm" data-accion="entrada" data-codigo="${codigo}">${tr('bodega.btn.recargar')}</button>
                            </td>
                        </tr>`;
                }).join('')}
            </tbody>
        </table>`;
}

async function identificarProveedor(item) {
    try {
        const { ok, data } = await api(`/bodega/productos/${encodeURIComponent(item.codigoProducto)}/proveedor-reposicion`);
        if (!ok) return mostrarToast(data.mensaje, 'error');

        $('provCodigo').textContent = data.datos.codigo;
        $('provNombre').textContent = data.datos.nombre;
        $('provProducto').textContent = `${item.nombreProducto} (${item.codigoProducto})`;
        proveedorCard.classList.remove('hidden');
        proveedorCard.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    } catch {
        mostrarToast(tr('msg.conn.error'), 'error');
    }
}

// =========================================================================
// Modales de entrada / salida de stock
// =========================================================================

function configurarModales() {
    document.querySelectorAll('.btn-cerrar-modal').forEach(btn => btn.addEventListener('click', cerrarModales));
    [modalRecargarStock, modalSalidaStock].forEach(modal => {
        modal.addEventListener('click', (e) => { if (e.target === modal) cerrarModales(); });
    });
    document.addEventListener('keydown', (e) => { if (e.key === 'Escape') cerrarModales(); });

    document.querySelectorAll('.step').forEach(btn => {
        btn.addEventListener('click', () => {
            const input = $(btn.dataset.target);
            const valor = (parseInt(input.value, 10) || 0) + Number(btn.dataset.step);
            input.value = Math.min(Number(input.max), Math.max(1, valor));
            actualizarVistaPrevia();
        });
    });
    [inputCantidadRecargar, inputCantidadSalida].forEach(i => i.addEventListener('input', actualizarVistaPrevia));

    $('formRecargarStock').addEventListener('submit', (e) => { e.preventDefault(); registrarMovimiento('entrada', inputCantidadRecargar); });
    $('formSalidaStock').addEventListener('submit', (e) => { e.preventDefault(); registrarMovimiento('salida', inputCantidadSalida); });
}

function abrirModal(tipo, item) {
    const esEntrada = tipo === 'entrada';
    const pre = esEntrada ? 'modalRecargar' : 'modalSalida';
    const input = esEntrada ? inputCantidadRecargar : inputCantidadSalida;

    state.productoSeleccionadoModal = { codigo: item.codigoProducto, nombre: item.nombreProducto, stock: item.cantidadDisponible, tipo };
    $(`${pre}ProductoNombre`).textContent = item.nombreProducto;
    $(`${pre}ProductoCodigo`).textContent = item.codigoProducto;
    $(`${pre}StockActual`).textContent = item.cantidadDisponible;
    input.value = esEntrada ? 25 : 1;
    if (!esEntrada) input.max = item.cantidadDisponible;
    actualizarVistaPrevia();

    (esEntrada ? modalRecargarStock : modalSalidaStock).classList.remove('hidden');
    input.focus();
    input.select();
}

function actualizarVistaPrevia() {
    const p = state.productoSeleccionadoModal;
    if (!p) return;
    const esEntrada = p.tipo === 'entrada';
    const cantidad = parseInt((esEntrada ? inputCantidadRecargar : inputCantidadSalida).value, 10) || 0;
    const nuevo = esEntrada ? p.stock + cantidad : p.stock - cantidad;
    $(esEntrada ? 'modalRecargarStockNuevo' : 'modalSalidaStockNuevo').textContent = nuevo < 0 ? '—' : nuevo;
}

async function registrarMovimiento(tipo, input) {
    const p = state.productoSeleccionadoModal;
    if (!p) return;

    const cantidad = parseInt(input.value, 10);
    if (isNaN(cantidad) || cantidad <= 0) return mostrarToast(tr('modal.invalid'), 'error');
    if (tipo === 'salida' && cantidad > p.stock) return mostrarToast(tr('modal.too.many'), 'error');

    try {
        const { ok, data } = await api(`/bodega/productos/${encodeURIComponent(p.codigo)}/${tipo}?cantidad=${cantidad}`, { method: 'POST' });
        mostrarToast(data.mensaje, ok ? 'success' : 'error');
        if (!ok) return;
        if (tipo === 'entrada') proveedorCard.classList.add('hidden');
        cerrarModales();
        cargarInventarioBodega();
    } catch {
        mostrarToast(tr('msg.conn.error'), 'error');
    }
}

function cerrarModales() {
    modalRecargarStock.classList.add('hidden');
    modalSalidaStock.classList.add('hidden');
    state.productoSeleccionadoModal = null;
}

// =========================================================================
// Notificaciones
// =========================================================================

function mostrarToast(mensaje, tipo = 'success') {
    if (!mensaje) return;
    const toast = document.createElement('div');
    toast.className = `toast ${tipo}`;
    toast.setAttribute('role', tipo === 'error' ? 'alert' : 'status');
    toast.innerHTML = `<span class="dot"></span><span>${esc(mensaje)}</span>`;
    $('toastContainer').appendChild(toast);

    setTimeout(() => {
        toast.style.transition = 'opacity 0.25s ease';
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 250);
    }, 3500);
}

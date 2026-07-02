# SocialMediaDashboard

Dashboard web full-stack para explorar de forma visual datos de uso de redes sociales y su relación con indicadores de bienestar declarados en una encuesta.

[Demo en producción](https://socialmedia.jabejarano.tech) · [Repositorio](https://github.com/JABejaranoVela/SocialMediaDashboard) · [Demo original en Netlify](https://jabejaranosocialmediadashboard.netlify.app/)

## 1. Resumen del proyecto

SocialMediaDashboard transforma un conjunto de respuestas de encuesta en un panel comprensible, con indicadores agregados y visualizaciones sobre hábitos digitales, características demográficas y métricas de bienestar. El dashboard es público, mientras que la creación, consulta, edición y eliminación de registros propios requiere autenticación.

Es un proyecto académico y de demostración. Su valor está tanto en la visualización de los datos como en el recorrido técnico completo: modelado relacional, SPA responsive, API REST protegida, despliegue con contenedores y automatización de producción.

## 2. Problema que resuelve

Una tabla de respuestas en bruto dificulta identificar distribuciones y comparar variables. La aplicación ofrece una capa visual que permite:

- resumir el volumen de respuestas y el uso declarado de redes sociales;
- comparar el tiempo medio de uso por grupos de edad;
- observar la distribución por ocupación y plataformas utilizadas;
- presentar promedios de indicadores como distracción, preocupación, comparación social, concentración, sueño o búsqueda de validación;
- gestionar nuevas respuestas sin trabajar directamente sobre la base de datos.

El objetivo es facilitar una exploración descriptiva. La aplicación no realiza diagnóstico médico, predicción clínica ni demuestra relaciones causales entre el uso de redes sociales y la salud mental.

## 3. Datos utilizados

El proyecto parte del dataset público de Kaggle **Social Media and Mental Health**, incorporado como datos de demostración en `db/dashboard.sql`. El dump crea `dashboarddb`, carga las respuestas iniciales y las distribuye en un modelo relacional.

Las entidades principales son:

- **Respondent**: fecha, edad y género de la respuesta.
- **Demographics**: situación sentimental y ocupacional.
- **SocialMediaUsage**: uso de redes, tiempo diario, uso sin objetivo, distracción e inquietud.
- **MentalHealthMetrics**: escalas declaradas de distracción, preocupación, concentración, comparación social, validación, estado de ánimo, interés y sueño.
- **Platform** y **Organization**: relaciones muchos-a-muchos con las respuestas.
- **Users**: cuentas de aplicación utilizadas para autenticar y asignar la propiedad de nuevos registros.

Los datos se emplean únicamente con fines educativos y analíticos. Son respuestas autodeclaradas, contienen categorías heterogéneas y no deben interpretarse como una muestra clínica ni necesariamente representativa de la población. El dump versionado no incluye cuentas de acceso ni credenciales productivas.

## 4. Decisiones técnicas

### Frontend

- **Vue 3 y Vue Router** para construir una SPA dividida en vistas y componentes reutilizables.
- **Vite** para desarrollo local y generación del bundle de producción.
- **CoreUI** para disponer de una base visual consistente.
- **Chart.js y vue-chartjs** para representar barras, burbujas, distribución ocupacional e indicadores de bienestar.
- **Diseño responsive mobile-first** para adaptar navegación, cards, gráficos, formularios y listados a móvil, tablet y escritorio.
- Peticiones relativas a **`/api`**, lo que permite usar el mismo origen público y simplifica proxy, CORS y despliegue.

El dashboard agregado permanece accesible sin iniciar sesión. Las vistas de alta y gestión consumen endpoints protegidos y envían el JWT en las peticiones.

### Backend

- **Java 21 y Spring Boot** para estructurar la API REST y separar controladores, servicios y repositorios.
- **Spring Data JPA/Hibernate** para mapear el modelo relacional y las asociaciones entre respuestas, métricas, plataformas y usuarios.
- **MySQL 8.4** para almacenar información estructurada y ejecutar las consultas agregadas del dashboard.
- **Spring Security y JWT** para mantener una API sin sesión y desacoplar la autenticación del frontend.
- **Spring Boot Actuator** para health checks limitados a `health` e `info`.

### Seguridad

La seguridad se ha reforzado con decisiones concretas:

- los endpoints públicos se limitan al login, Actuator controlado y datos agregados del dashboard;
- `/api/respondents/**` requiere autenticación;
- las respuestas de respondents usan DTOs y no serializan directamente las entidades JPA;
- el campo de contraseña está excluido de la serialización como defensa adicional;
- las operaciones por ID buscan el registro junto con el usuario autenticado, evitando que una cuenta gestione registros ajenos;
- el login aplica un límite básico en memoria por IP y usuario normalizado;
- MySQL no publica ningún puerto en el host;
- frontend y backend solo se enlazan a loopback y quedan detrás del Nginx del VPS.

Estas medidas reducen la superficie de exposición, pero no convierten el proyecto en un sistema exento de riesgos. Las limitaciones pendientes se detallan más adelante.

### Despliegue y operación

- **Docker Compose** ejecuta frontend, backend y MySQL en una red privada.
- El frontend se compila con Node y se sirve desde un contenedor Nginx; el backend se construye y ejecuta con Java 21.
- **Nginx en el host** actúa como reverse proxy y termina HTTPS con certificados de Let’s Encrypt.
- **GitHub Actions** valida ambos proyectos, construye las imágenes y las publica en GHCR.
- El despliegue por SSH usa imágenes etiquetadas con el SHA completo del commit.
- Antes de actualizar servicios se genera un backup obligatorio de MySQL; el proceso se detiene si el dump falla o queda vacío.
- Después del arranque se comprueban los health checks locales, el frontend público y un endpoint agregado de la API.

## 5. Arquitectura

### Aplicación

```text
Usuario
  ↓ HTTPS
Nginx del VPS
  ├── /      → Frontend Vue servido por Nginx interno
  └── /api   → Backend Spring Boot
                         ↓
                      MySQL
```

MySQL permanece únicamente en la red de Docker. En el host, frontend y backend se publican sobre `127.0.0.1`, sin exposición directa a Internet.

### CI/CD

```text
Push a main / ejecución manual
  ↓
GitHub Actions
  ├── tests y package del backend
  └── npm ci y build del frontend
  ↓
Build y publicación de imágenes en GHCR
  ↓
Deploy por SSH al VPS
  ↓
Backup MySQL → Docker Compose pull/up → health checks
```

La primera versión del pipeline no realiza rollback automático. Un fallo deja el workflow marcado como fallido y conserva datos, backups e imágenes anteriores para facilitar el diagnóstico manual.

## 6. Funcionalidades principales

- Dashboard público con KPIs y métricas agregadas.
- Gráficos de uso por edad, plataformas, ocupación e indicadores de bienestar.
- Login mediante JWT.
- Alta de nuevas respuestas autenticadas.
- Listado, edición y eliminación de registros propios.
- Control de propiedad aplicado en el backend, no únicamente en la interfaz.
- Navegación y formularios adaptados a móvil, tablet y escritorio.
- Health checks de frontend y backend.
- Despliegue automatizado con backup previo de la base de datos.

## 7. Demo

La versión desplegada está disponible en:

**https://socialmedia.jabejarano.tech**

El dashboard agregado puede consultarse públicamente. Las operaciones de gestión requieren una cuenta autenticada; el repositorio no publica usuarios ni contraseñas de demostración.

La antigua versión en Netlify se conserva temporalmente como referencia o respaldo de la demo, pero el despliegue completo actual se ejecuta en el VPS.

## 8. Ejecución local

### Requisitos

- Java 21
- Node.js 22 y npm
- MySQL 8, o Docker con Docker Compose

### Opción A: stack completo con Docker Compose

Desde la raíz del repositorio:

```powershell
Copy-Item .env.prod.example .env.prod
```

Sustituye los valores de ejemplo por credenciales locales y ejecuta:

```powershell
docker compose --env-file .env.prod `
  -f docker-compose.prod.yml `
  -f docker-compose.prod.override.example.yml `
  config --quiet

docker compose --env-file .env.prod `
  -f docker-compose.prod.yml `
  -f docker-compose.prod.override.example.yml `
  up -d --build
```

Servicios locales:

- Frontend: `http://127.0.0.1:8082`
- Backend Actuator: `http://127.0.0.1:9091/actuator/health`
- API a través del frontend: `http://127.0.0.1:8082/api/dashboard/respondent/count`

Para detenerlos sin eliminar la base de datos:

```powershell
docker compose --env-file .env.prod `
  -f docker-compose.prod.yml `
  -f docker-compose.prod.override.example.yml `
  down
```

No utilices `down -v` si quieres conservar el volumen local de MySQL. `db/dashboard.sql` solo se ejecuta al inicializar un volumen vacío.

### Opción B: desarrollo nativo

1. Importa `db/dashboard.sql` en un MySQL local.
2. Crea `dashboard-backend/app/.env` —ignorado por Git— con las variables de desarrollo, sin reutilizar secretos de producción:

```properties
SPRING_PROFILES_ACTIVE=dev
DB_URL=jdbc:mysql://localhost:3306/dashboarddb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=usuario_local
DB_PASSWORD=contraseña_local
JWT_SECRET=clave_local_de_desarrollo_de_al_menos_32_bytes
```

3. Arranca el backend desde la raíz:

```powershell
.\dashboard-backend\app\mvnw.cmd -f .\dashboard-backend\app\pom.xml spring-boot:run
```

4. En otra terminal, arranca el frontend:

```powershell
cd dashboard-frontend
npm ci
npm run dev
```

Vite sirve la SPA normalmente en `http://localhost:5173` y redirige `/api` a `http://localhost:9090` durante el desarrollo.

El dump no crea usuarios. El dashboard público funcionará después de la importación, pero las vistas protegidas necesitan una cuenta local creada mediante un procedimiento controlado y fuera de Git.

## 9. Seguridad aplicada y limitaciones

Además de JWT, DTOs y ownership, el despliegue utiliza secretos fuera del repositorio, credenciales Docker temporales, verificación estricta de la clave SSH del VPS, imágenes inmutables por SHA y backups MySQL con permisos restringidos.

Aspectos pendientes:

- reforzar el rate limiting en el Nginx del host; el actual es local al proceso y se reinicia con la aplicación;
- rotar o eliminar manualmente cualquier cuenta antigua que hubiera sido cargada en una base ya inicializada;
- ampliar la validación de entrada de los DTOs;
- adoptar Flyway o Liquibase para versionar cambios de esquema;
- revisar periódicamente dependencias y cabeceras de seguridad;
- definir retención automatizada y pruebas periódicas de restauración de backups.

## 10. Conclusiones

El proyecto demuestra cómo convertir respuestas de encuesta normalizadas en una aplicación full-stack operable: una SPA responsive, una API REST protegida, persistencia relacional y un flujo de despliegue reproducible sobre un VPS.

Las decisiones más relevantes fueron separar los agregados públicos de las operaciones privadas, tratar el backend como autoridad de permisos, utilizar DTOs en los límites de la API y desplegar imágenes inmutables con backup y health checks. Esto evita que la calidad del proyecto dependa únicamente de la interfaz visual.

Desde el punto de vista de los datos, el dashboard permite comparar patrones declarados de uso, demografía y bienestar de forma más clara que el dump original. Sus resultados deben entenderse como descriptivos: la procedencia de encuesta, la falta de control experimental y la calidad irregular de algunas categorías impiden extraer causalidad o conclusiones clínicas.

## 11. Próximas mejoras

- Migraciones reproducibles con Flyway o Liquibase.
- Bean Validation y mensajes de error estructurados en la API.
- Tests end-to-end para login, formularios y dashboard.
- Observabilidad centralizada de logs y métricas.
- Rate limiting y cabeceras CSP/HSTS reforzadas en el perímetro.
- Automatización de actualizaciones con Dependabot o Renovate.
- Roles y permisos más granulares.
- Filtros analíticos, exportación de datos y mejoras adicionales de UX.
- Estrategia probada de restauración y rollback.

## Licencia

Este proyecto se distribuye bajo los términos de la licencia MIT.

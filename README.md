# Sistema de Gestión y Monitoreo — Empresa S.P.S.

Este proyecto es una aplicación de escritorio desarrollada en **JavaFX** con arquitectura **MVC** (Modelo-Vista-Controlador) y gestionada con **Maven**. Su objetivo es solucionar la administración de jornadas de trabajo, solicitudes de servicios, control de pagos y el monitoreo en tiempo real de sensores de seguridad y rastreo GPS[cite: 1].

---

## 📋 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado en tu equipo:

1. **Java Development Kit (JDK) 21** o superior.
2. **Git** para el control de versiones.
3. **IntelliJ IDEA** (recomendado, ya cuenta con soporte nativo para Maven y JavaFX).

---

## 🚀 1. Cómo Clonar y Abrir el Proyecto

1. **Clonar el repositorio:**
   Abre tu terminal o Git Bash y ejecuta:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd sps-security-system
   ```

2. **Abrir en IntelliJ IDEA:**
   * Abre IntelliJ IDEA.
   * Selecciona **File > Open...** y elige la carpeta del proyecto.
   * Espera a que IntelliJ reconozca el proyecto Maven y descargue automáticamente las dependencias (puedes ver la barra de progreso en la esquina inferior derecha).

3. **Verificar la versión de Java:**
   * Ve a **File > Project Structure... > Project**.
   * Asegúrate de que el **SDK** esté configurado en **Java 21** (o tu versión de JDK 21+ instalada).

---

## 🔄 2. Flujo de Trabajo

> ⚠️ **REGLA DE ORO:** Queda estrictamente prohibido hacer `git push` directo a las ramas `main` o `develop`. Todo el trabajo se realiza en ramas de funcionalidad (*feature branches*).

### Paso A: Preparar tu rama de trabajo
Antes de empezar a codificar cualquier pantalla o módulo, asegúrate de partir de la versión más reciente de `develop`:

```bash
# 1. Cambia a la rama develop
git checkout develop

# 2. Descarga los últimos cambios del equipo
git pull origin develop

# 3. Crea tu rama de trabajo (usa un nombre descriptivo)
git checkout -b feature/pantalla-guardias
```

### Paso B: Probar la aplicación localmente
Puedes ejecutar el sistema desde la terminal de IntelliJ (`Alt + F12`) ejecutando:

```bash
mvn clean compile javafx:run
```

O ejecutando directamente el método `main` dentro de la clase principal (`HelloApplication.java` o `MainApp.java`).

---

## 🛠️ 3. Subir Cambios y Crear Pull Request (PR)

Cuando termines tu módulo o un avance importante:

1. **Guarda tus cambios localmente:**
   ```bash
   git add .
   git commit -m "feat: implementada la vista FXML de asignación de guardias"
   ```

2. **Sube tu rama a GitHub:**
   ```bash
   git push -u origin feature/pantalla-guardias
   ```

3. **Crear el Pull Request en GitHub:**
   * Ve al repositorio en **GitHub**.
   * Haz clic en **Compare & pull request**.
   * **Verifica el destino:** La fusión debe ser desde tu rama `feature/...` HACIA la rama **`develop`** (`base: develop <- compare: feature/pantalla-guardias`).
   * Asigna a un compañero como revisor y haz clic en **Create Pull Request**.

---

## 📁 4. Estructura General del Código (MVC)

Para mantener el código organizado, respeta la siguiente jerarquía:

```text
src/main/java/com/sps/security/
├── model/        # Clases de datos (Guardia, Sensor, AlarmaEvento, UbicacionGPS)
├── controller/   # Lógica de las vistas (GuardiasController, AlarmasController)
├── service/      # Procesos en segundo plano, hilos o conectores de datos
└── util/         # Clases auxiliares y herramientas

src/main/resources/com/sps/security/
├── views/        # Archivos de interfaz gráfica (.fxml)
└── styles/       # Hojas de estilo (.css)
```

---

## ❓ 5. Solución a Problemas Comunes

* **Error `Location is not set` al abrir la ventana:**
  Asegúrate de que la ruta al archivo `.fxml` en tu controlador/app comience con `/` y coincida exactamente con la ubicación dentro de `src/main/resources`.

* **Las dependencias no cargan:**
  Abre la pestaña de **Maven** a la derecha de IntelliJ y haz clic en el botón **Reload All Maven Projects** (icono de dos flechas en círculo).

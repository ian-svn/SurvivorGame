# Changelog SurvivorGame

## [UNRELEASED]
- Cambio de nombre del proyecto
- Version Pre Alfa

## [0.1.0] - 2025/05/06
### Added
- Creación del proyecto con libGDX
## Fixed
- Corregido el formato de CHANGELOG.md


El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/).

## [0.1.0] - 2025-08-06
### Added
- Clase principal **`MyGame`** con `SpriteBatch` y `FitViewport` (1280x720).
- Pantalla **MenuScreen** con botones:
    - **Jugar** → abre `GameScreen`.
    - **Opciones** → abre `OptionsScreen`.
    - **Salir** → cierra la aplicación.
- Pantalla **OptionsScreen** con botones:
    - **Pantalla Completa** → activa modo fullscreen.
    - **Ventana** → cambia a 1280x720 en modo ventana.
    - **Volver** → regresa al `MenuScreen`.
- Pantalla **GameScreen** inicial:
    - Jugador (`Jugador`) renderizado en el escenario.
    - Sistema de objetos (`Objeto`), empezando con `PocionDeAmatista`.
    - Método `spawnear(x, y)` para colocar objetos.
- Skins personalizados para los botones (`JugarButton.json`, `OpcionesButton.json`, `SalirButton.json`).
- Fondo para la pantalla de opciones (`background.json`).

### Changed
- `GameScreen` corregido: ahora implementa `Screen` en lugar de extender de `Actor`.

### Fixed
- Manejo correcto de recursos: todos los `Stage` y `Skin` se liberan en `dispose()` de cada pantalla.

## [0.2.0] - 2025-08-06
### Added
- **GameScreen**
    - Integración de `TooltipManager` para manejar tooltips en el juego.
    - Parámetros de configuración del tooltip:
        - `initialTime = 1f`
        - `subsequentTime = 0.2f`
        - `resetTime = 0f`
        - `animations = false`
        - `maxWidth = 200f`
    - Ahora `Jugador` recibe una referencia a `TooltipManager` para mostrar información contextual.
- **MyGame**
    - Se agregó una variable estática `TooltipManager tm` accesible desde todo el juego.
    - Inicialización de `TooltipManager` en `create()`.
- **MenuScreen**
    - Refactor: uso de la variable de clase `tableMenu` para organizar mejor el layout.

### Changed
- `GameScreen` ahora carga la textura del jugador usando **`PathManager.JUGADOR`** en lugar de la ruta hardcodeada (`sprites/jugador.png`).
- Ajustada la posición inicial del jugador a `(100, 100)` en lugar de `(50, 50)`.
- `MenuScreen` reorganizado: la `Table` se instancia antes que el `Stage` para mayor claridad.

### Fixed
- Limpieza automática de tooltips al inicializar `TooltipManager` con `hideAll()`.

## [0.3.0] - 2025-09-11

### Added
- Sistema de inventario en `Jugador` con detección y adquisición de objetos.
- Interfaz `Consumible` y clase `Pocion` (permite curar vida al jugador).
- Sistema de audio (`AudioControler`, `AudioManager`, `AudioService`) con soporte de música y efectos.
- Nuevos paths y assets para texturas, sonidos y música en `Assets` y `PathManager`.
- Pantalla de menú rápido (`FastMenuScreen`) con opciones de reanudar, reiniciar y volver al menú principal.
- Pantalla de carga (`LoadingScreen`) con barra de progreso animada y label de estado.
- Nuevas clases estándar en `standards/`:
    - `TextButtonStandard`: botón reutilizable con cursor dinámico y sonido de click integrado.
    - `CheckBoxStandard`: checkbox estándar con manejo de cursor y soporte para callbacks.
    - `TooltipStandard`: tooltip estándar con configuración de tiempos y skin personalizada.
    - `LabelStandard`: label estándar con skin unificada.

### Changed
- `MenuScreen`: reemplazo de botones por `TextButtonStandard` para un manejo más simple y consistente.
- Ahora es más fácil asignar funciones a botones usando `setClickListener(Runnable)`.

## [0.4.0] - 2025-09-11

### Added
- Clase abstracta `Bloque` y subclase `BloqueDeBarro` con texturas y colisiones.
- Interfaz `Colisionable` para unificar detección de colisiones.
- Clase `Jugador`:
    - Implementación de inventario con soporte para adquirir `Objeto`s.
    - Animaciones de movimiento (arriba, abajo, izquierda, derecha, diagonales).
    - Sonido al recoger objetos integrado con `AudioManager`.
- Clase base `Personaje` con atributos de vida, tamaño y métodos para alterarla.

### Changed
- El jugador ahora utiliza `Rectangle hitbox` para detectar colisiones con `Bloque`s.
- Colisiones diferenciadas: bloques atravesables y no atravesables.
- Integración de tooltips estándar para el jugador (`TooltipStandard`).

## [0.5.0] - 2025-10-07

### Changed
- Cambio de compilador de `JDK 23` a `JDK 17` debido a la estabilidad.

### Fixed
- Eliminacion de `import`s innecesarios.

## [0.6.0] - 2025-10-12

### Added
- Se muestra la vida del `Jugador` mediante una `Barra de vida`.
- Tooltip para `Bloques`, `Pociones` y `Barra de vida`.
- Al `Jugador` es perseguido por la `Camara` .
- `Mapa` fijo que puede ser recorrido por el `Jugador`.
- Las `Pociones` curan vida del `Jugador`.

### Fixed
- Arreglo de viewport mal cargado en la pantalla `OptionScreen` y en `GameScreen`.

### Changed
- Cambio de compilador de `JDK 23` a `JDK 17` debido a la estabilidad.}

## [0.7.0] - 2025-10-24

### Added
- Se agrega daño del `Enemigo` más el sonido q genera.
- Efecto de mouse al precionar el la `GameScreen` con el click derecho.
- Menu de perder y ganar el juego.
- Refactorizacion de codigo.

### Changed
- Cambio de método para moverse y colisionarse para los `Personajes` del juego.

## [0.8.0] - 2025-10-31

### Added
- Sistema de inventario mejorado en `Jugador` para almacenar `PocionDeAmatista` y otros consumibles.
- Implementación de consumo de pociones en orden de llegada mediante la tecla **E**.
- Clases base `SerVivo` y `Entidad` para unificar atributos y métodos de enemigos y jugadores.
- Nuevos tipos de enemigos (`InvasorArquero`, `InvasorDeLaLuna`, `InvasorMago`) con herencia de `Enemigo`.
- Actualización del `GameScreen` para soportar la interacción de teclas con inventario y efectos visuales.
- Efecto visual al recibir daño el `Jugador`.

### Changed
- Refactor de clases de enemigos y jugador para reducir código repetido usando herencia.
- `Jugador` ahora procesa objetos consumibles solo al presionar **E**, en lugar de automáticamente.
- El jugador ya no puede moverse ni recoger items mientras el juego está terminado.

### Fixed
- Corrección de errores en recolección de objetos y consumo de pociones.

## [0.9.0] - 2025-11-07

### Added
- `Animales` y `Enemigos` con diferentes tamaños, daños y vida.
- Nuevo sprite de `Invasor de La Luna` y `Animales`.
- Mapa `Tiled` con su `Tileset`.
- Diseño mejorado ampliamente.

### Changed
- Sin más `Puntos` al agarrar objetos.
- `Barra de vida` con distinto diseño.

 ## [0.9.1] - 2025-11-11

 ### Added
 - `Shaders` para controlar el brillo de los elementos del escenario y conseguir el efecto de día y noche
 -  El `Jugador` ahora puede moverse tanto con el click secundario del ratón como con las teclas 'WASD' del teclado

### Observaciones
- Se produce un bug con los sprites al moverse en diagonal
- Error con los shaders y la barra de vida

## [0.9.2] - 2025-11-18

### Added
- `Gestor de animaciones` que centraliza codigo de animaciones de las entidades.
- Animacion del `Invasor de la Luna`.

[0.9.3] - 2025-11-20
### Added
- Sistema de ciclo día/noche con `GestorTiempo` y actualización dinámica de `brillo`.
- `Interfaz UI` independiente con reloj y contador de días (fija en pantalla).
- Interfaz `IMundoJuego` para mejorar la arquitectura y desacoplar entidades.
- Tecla 'T' para acelerar el tiempo (Debug).

### Added
- Optimización de memoria en sistema de colisiones y vectores de movimiento.

[0.10.0] - 2025-11-20
### Added
- Sistema de ciclo día/noche con `GestorTiempo` y actualización dinámica de `brillo`.
- `Interfaz UI` independiente con reloj y contador de días (fija en pantalla).
- Interfaz `IMundoJuego` para mejorar la arquitectura y desacoplar entidades.
- Tecla 'T' para acelerar el tiempo (Debug).
- Optimización de memoria en sistema de colisiones y vectores de movimiento.

### Added
- Optimización de memoria en sistema de colisiones y vectores de movimiento.

## [0.10.1] - 2025-11-21

### Fixed
- Error de pantalla de juego terminado.

## [0.10.2] - 2025-11-24
### Changed
- Implementación de la clase `Hud` para gestionar la interfaz de usuario en una capa superior independiente.
- La interfaz (barra de vida y reloj) ahora se renderiza después del shader de luz, evitando que se oscurezca durante la noche.
- Reubicación de la barra de vida a la esquina superior derecha de la pantalla.
- Refactorización de la clase `Jugador`: eliminada toda lógica interna de renderizado de UI.

## [0.11.0] - 2025-11-26
### Added
- Sistema de combate modular (`IAtaque`) con mecánicas de casteo y cooldown.
- Clase `EfectoVisual` para animaciones temporales y feedback de daño (color rojo).
- Soporte para texturas .png en Animal con rotación automática (flip) sin necesidad de Atlas.
- El `jugador` ahora ataca con la tecla Q hacia la posición del mouse.

### Changed
- Implementación de `Safe Knockback` en SerVivo para evitar atravesar paredes al recibir empuje.
- Rebalanceo de enemigos (`Mago`, `Arquero`, `Luna`) con rangos de detección de IA dinámicos.

### Fixed
- Solucionado el bug de "enemigos fantasma" que seguían atacando tras morir.
- Corregido NullPointerException en el `Jugador` al intentar acceder al Stage después de morir.

## [0.11.1] - 2025-11-26
### Changed
- Las `pociones` curan inmediatamente al recogerlas.
- La animación de `ataque` ahora aparece sobre el enemigo si acierta, o cerca del `jugador` si falla.

## [0.11.2] - 2025-11-26
### Fixed
- El `menu` del final del juego se arreglo.

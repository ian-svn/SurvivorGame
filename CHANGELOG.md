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

## [0.1.0] - 2024-08-06
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

## [0.2.0] - 2024-08-06
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


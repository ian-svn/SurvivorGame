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

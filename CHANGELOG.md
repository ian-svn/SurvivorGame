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


## [0.11.3] - 2025-11-26
### Fixed
- Se soluciona el error en donde los enemigos ya derrotados siguen pudiendo interactuar con el `Jugador`.

## [0.12.0] - 2025-11-26
### Added
- Sistema de inventario para consumibles implementado en el `HUD` y el consumo de los mismos.
- Ahora las `Vacas` y `Jabalis` tiran carne en el piso para recoger al igual que las `Pociones`.
- `Carne Podrida` que saca vida al comer y la tiran los enemigos.

## [0.13.0] - 2025-11-30
### Added
- Sistema de `estadísticas` dinámicas para el `Jugador` (Daño y Velocidad) con límites máximos.
- Tabla de `estadísticas` en el `HUD` visible manteniendo la tecla TAB.
- Progresión de dificultad: los `Enemigos` ganan +20 de Vida Máxima por cada día que pasa.
- Mecánica de la `Cama`: permite saltar a la noche (20:00) si se interactúa con E, pero solo si es de día.
- Despawn de `objetos`: los ítems en el suelo desaparecen a los 60 segundos (titilan los últimos 10s).

### Changed
- Balanceo del ciclo de tiempo: el `Día` dura 1 minuto real y la `Noche` 2 minutos reales.
- Reducción de la hitbox de movimiento del `Jugador` a solo los pies para evitar trabarse en bloques.
- Los `Enemigos` ahora se queman al amanecer hasta morir y no sueltan loot si mueren por el sol.
- Ajuste de probabilidad de drops: 50% nada, 37.5% `CarnePodrida`, 12.5% Carne.
- Los `consumibles` ahora otorgan bonificaciones de estadísticas (+Daño, +Velocidad, +VidaMáxima).
- Ciclo Día/Noche Ajustado:
    Día: 1 minuto real.
    Noche: 2 minutos reales (mayor duración de combate).
    Sistema de Oleadas: Los enemigos aparecen en 5 oleadas distribuidas equitativamente durante la noche.

### Fixed
- Implementación de `Z-Sorting` para que los `actores` se dibujen correctamente según su profundidad (eje Y).
- Spawn seguro: el juego inicia el Día 1 sin `enemigos`, solo con `animales` y recursos cercanos.
- Corrección de spawneo: `enemigos` y `objetos` ya no aparecen fuera del mapa ni sobre bloques sólidos.

## [0.14.0] - 2025-12-14
### Added
- Sistema de Calor: Implementada lógica de proximidad en `Hoguera`. El jugador ahora detecta si está dentro de un radio de 4 bloques (128px).
- UI de Estado: Agregado icono de fuego en la interfaz (arriba a la derecha) que se activa/desactiva según el estado `sintiendoCalor` del jugador.
- Parser de Tiled: Agregado método estático `verificarCreacion` en `BloqueAnimado` para instanciar automáticamente la clase `Hoguera` al leer el mapa.
- Assets: Generado archivo `tornado.atlas` para futura implementación de la animación de viento/tornado.

### Changed
- Hoguera: Ahora aplica daño por segundo (DPS) en lugar de daño por frame, y utiliza `Animation.PlayMode.LOOP`.
- BloqueAnimado: Modificado el método `draw` para forzar el escalado del sprite a las dimensiones del actor (32x32), solucionando el problema de texturas gigantes.
- Escenario: Implementada lógica "Reset & Set" para el estado de calor antes del `act()`, solucionando el parpadeo del icono de la UI al moverse.
- Jugador: Eliminado el reinicio interno de variables de estado en `act()` para delegar el control al `Escenario` (estabilidad del render).

### Fixed
- Core: Agregada la llamada a `stage.act(delta)` en el render loop de `Escenario` (las animaciones no se reproducían).
- Renderizado: Corrección de orden de dibujado y visibilidad de actores animados sobre el mapa estático.

## [0.15.0] - 2025-12-15
### Added
- Core: Sistema de desatasco automático en `Jugador` al detectar colisión inicial.
- Algoritmo: Distribución por grilla (12x12) para el evento `Inundacion`, asegurando cobertura equitativa.
- Mecánica: Daño fijo por tick (4 HP) implementado en `EfectoVentisca`.

### Changed
- Refactor: Aplicado SRP en `GestorDesastres` y `GestorSpawneo`. La UI se centralizó en `Hud`.
- Hoguera: Migración a TextureAtlas, reducción de hitbox a la base y lógica de daño por contacto (-10 HP).
- Terremoto: Modificado para limpiar items del suelo preservando `Cama` y `Hoguera`.
- GestorTiempo: La condición de victoria se movió al amanecer (05:00) del día final.
- UI: Reducción de escala en alertas y reubicación de la tabla de estadísticas.

### Fixed
- Compilación: Resuelto error de tipos incompatibles entre `Hoguera` y `BloqueAnimado`.
- Lógica: Corregida inyección de dependencias nulas entre `Escenario` y `Hud`.
- Spawneo: Eliminada la aparición errónea de hogueras como loot aleatorio.
- Assets: Añadida referencia faltante a `HOGUERA_TEXTURE` en `PathManager`.

## [0.15.0] - 2025-12-15
### Added
- Core: Sistema de desatasco automático en `Jugador` al detectar colisión inicial.
- Algoritmo: Distribución por grilla (12x12) para el evento `Inundacion`, asegurando cobertura equitativa.
- Mecánica: Daño fijo por tick (4 HP) implementado en `EfectoVentisca`.

### Changed
- Refactor: Aplicado SRP en `GestorDesastres` y `GestorSpawneo`. La UI se centralizó en `Hud`.
- Hoguera: Migración a TextureAtlas, reducción de hitbox a la base y lógica de daño por contacto (-10 HP).
- Terremoto: Modificado para limpiar items del suelo preservando `Cama` y `Hoguera`.
- GestorTiempo: La condición de victoria se movió al amanecer (05:00) del día final.
- UI: Reducción de escala en alertas y reubicación de la tabla de estadísticas.

### Fixed
- Compilación: Resuelto error de tipos incompatibles entre `Hoguera` y `BloqueAnimado`.
- Lógica: Corregida inyección de dependencias nulas entre `Escenario` y `Hud`.
- Spawneo: Eliminada la aparición errónea de hogueras como loot aleatorio.
- Assets: Añadida referencia faltante a `HOGUERA_TEXTURE` en `PathManager`.

## [1.0.0] - 2025-12-16

## Added
- Sistema de combate "Snapshot" (permite esquivar ataques).
- IA de Tornado con estados (Apuntar -> Embestir -> Esperar).
- Evento de "Doble Desastre" en la Noche 4.

## Changed
- Ciclo Día/Noche ajustado: 30s Día / 90s Noche.
- Victoria al amanecer del Día 5.
- Hitbox del jugador reducida (solo pies) para mejor movimiento.
- Límites de stats aumentados.
- Tornados ahora dañan a enemigos (40 dmg) y al jugador (20 dmg + empuje).
- Menús detienen sonidos ambiente al cambiar de pantalla.

## Fixed
- Crash por archivos de audio faltantes solucionado.
- Buffs de velocidad (Carne Podrida) ahora funcionan correctamente.
- Tooltips arreglados para desastres y objetos.
- Tooltips de enemigos muestran vida en tiempo real.
- Errores de compilación y dependencias circulares resueltos.

# Guion de sustentación - FocusSense

## 1. Presentación breve (3 minutos)

### 0:00 - 0:45 | Problema
“FocusSense resuelve un problema simple: durante una sesión de estudio cambian la iluminación y el estado de batería, pero el usuario no debería detenerse a reconfigurar la app. Por eso usamos contexto real del dispositivo para adaptar automáticamente la experiencia.”

### 0:45 - 1:30 | Solución
“Es un temporizador de enfoque de 25 minutos. Detecta luminosidad, porcentaje de batería y si el equipo está cargando. Con poca luz activa modo noche; con luz intensa aumenta contraste; y con batería baja entra en modo ECO, deshabilitando animaciones y ocultando contenido secundario.”

### 1:30 - 2:15 | Pipeline
“Seguimos el pipeline obligatorio: contexto, procesamiento, decisión y adaptación. `LightSensorService` y `BatteryService` capturan; `ContextManager` procesa; `AdaptationEngine` decide; y `MainActivity` aplica el cambio visible.”

### 2:15 - 3:00 | Metodología y calidad
“Nos apoyamos en ASD: especular, colaborar y aprender. Como aprendizaje técnico incluimos histéresis para evitar cambios constantes cerca de los umbrales y dejamos el motor independiente de Android para probar seis escenarios.”

## 2. Demostración (5 minutos)

1. Abrir la app en modo normal y mostrar valores actuales.
2. Iniciar el temporizador; señalar la animación de pulso.
3. Cubrir el sensor de luz: comprobar **Modo noche**.
4. Iluminar con linterna: comprobar **Modo exterior**.
5. En emulador, poner batería en 15% y sin cargar: comprobar **Modo ECO**.
6. Señalar que desaparece la tarjeta de consejo y se detiene la animación, pero el temporizador sigue funcional.
7. Mostrar rápidamente `AdaptationEngine.kt` y los umbrales.
8. Volver a valores normales y demostrar recuperación automática.

## 3. Respuestas rápidas para la revisión técnica

**¿Dónde se captura el contexto?**  
`context/LightSensorService.kt` y `context/BatteryService.kt`.

**¿Dónde se procesa?**  
`context/ContextManager.kt`, que mantiene el último estado de cada variable y crea `ContextSnapshot`.

**¿Dónde se toma la decisión?**  
`adaptation/AdaptationEngine.kt`.

**¿Dónde ocurre la adaptación visible?**  
`ui/MainActivity.kt`, método `renderContext()` y `applyPalette()`.

**¿Por qué hay dos umbrales para entrar/salir de un modo?**  
Para implementar histéresis y evitar oscilaciones de la interfaz cuando el sensor fluctúa cerca de un límite.

**¿Qué pasa si el equipo no tiene sensor de luz?**  
La aplicación lo muestra como no disponible y sigue adaptándose por batería, por lo que mantiene una variable de contexto real.

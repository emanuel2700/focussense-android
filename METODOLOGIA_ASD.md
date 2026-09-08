# Aplicación de Adaptive Software Development (ASD) en FocusSense

## Base teórica

El material **Metodologías de Desarrollo de Software**, de Maida y Pacienzia (2015), presenta **Adaptive Software Development (ASD)** como un enfoque que asume que las necesidades cambian durante el proyecto. Su ciclo es iterativo y se organiza como **especular - colaborar - aprender**, con énfasis en tolerancia al cambio, trabajo por componentes, riesgos y aprendizaje continuo.

FocusSense aplica esa lógica porque el objetivo del taller no es solo construir una pantalla, sino una solución capaz de reaccionar a condiciones cambiantes del contexto.

## 1. Especular

### Misión
Construir una aplicación móvil que detecte variables reales del entorno/dispositivo y modifique automáticamente su comportamiento de forma observable.

### Riesgos identificados
- Algunos dispositivos no poseen sensor de luz.
- Los valores de luminosidad pueden oscilar y provocar cambios visuales demasiado frecuentes.
- El modo de ahorro no debe eliminar la funcionalidad principal del temporizador.
- La lógica adaptativa no debe quedar mezclada con la interfaz.

### Iteraciones definidas

**Iteración 1 - Captura del contexto**
- Lectura del sensor de luminosidad.
- Lectura del nivel y estado de carga de batería.
- Modelo `ContextSnapshot`.

**Iteración 2 - Decisión y adaptación**
- Motor `AdaptationEngine`.
- Reglas para noche, exterior y ECO.
- Interfaz que cambia automáticamente.
- Histéresis para estabilizar decisiones.

**Iteración 3 - Calidad y demostración**
- Manejo de dispositivos sin sensor de luz.
- Separación modular de responsabilidades.
- Validación de seis escenarios del motor.
- README, documento técnico y guía de sustentación.

## 2. Colaborar

El proyecto se divide por componentes con contratos simples:

- **Captura:** servicios Android entregan valores del contexto.
- **Procesamiento:** `ContextManager` integra los valores.
- **Decisión:** `AdaptationEngine` convierte el contexto en una decisión.
- **Adaptación:** `MainActivity` representa la decisión en la interfaz.

Esta separación permite que distintos integrantes trabajen sobre sensores, reglas, interfaz y documentación sin modificar toda la aplicación al mismo tiempo.

## 3. Aprender

Después de cada iteración se revisa:

- **Calidad desde el usuario:** ¿el cambio de modo es visible, entendible y útil?
- **Calidad técnica:** ¿la lógica está modularizada y sin duplicación innecesaria?
- **Funcionalidad:** ¿la adaptación ocurre automáticamente ante cambios reales?
- **Estado del proyecto:** ¿qué requisito falta y cuál es el siguiente riesgo?

Aprendizajes incorporados en el resultado final:

1. Un solo umbral de luminosidad puede generar parpadeo entre modos; se añadió histéresis.
2. El sensor de luz no está garantizado en todos los dispositivos; la batería funciona como segunda variable real y existe un fallback explícito.
3. La adaptación de ahorro no debe afectar el propósito principal; por eso se eliminan animaciones y contenido secundario, pero el temporizador continúa funcionando.
4. Mantener el motor de decisión independiente de Android permite probarlo sin ejecutar toda la aplicación.

## Correspondencia con el taller

`CONTEXTO -> PROCESAMIENTO -> DECISIÓN -> ADAPTACIÓN`

- Contexto: `LightSensorService` + `BatteryService`.
- Procesamiento: `ContextManager` + estabilización por histéresis.
- Decisión: `AdaptationEngine`.
- Adaptación: cambios de paleta, contraste, animaciones y contenido en `MainActivity`.

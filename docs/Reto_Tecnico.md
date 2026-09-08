# Preparación para el reto técnico en vivo

Los siguientes cambios están diseñados para poder implementarse rápidamente durante la sustentación.

## Reto 1 - Cambiar el umbral de modo noche

Archivo: `adaptation/AdaptationEngine.kt`

Cambiar:

```kotlin
const val DARK_ENTER_LUX = 40f
const val DARK_EXIT_LUX = 70f
```

Ejemplo solicitado por docente: entrar a modo noche debajo de 100 lux y salir por encima de 150 lux.

## Reto 2 - Cambiar el límite de batería baja

Cambiar:

```kotlin
const val LOW_BATTERY_PERCENT = 20
```

por el porcentaje indicado.

## Reto 3 - Mantener consejos visibles en ECO

En la decisión `ECO`, cambiar:

```kotlin
showTips = false
```

a `true`.

## Reto 4 - Agregar condición “cargando”

Ejemplo: si la batería está <= 20% pero `isCharging == true`, no se activa ECO. Esta regla ya existe y puede explicarse o modificarse fácilmente en:

```kotlin
val eco = context.batteryLevel <= LOW_BATTERY_PERCENT && !context.isCharging
```

## Reto 5 - Agregar un nuevo modo por luz media

1. Agregar un valor al enum `AdaptationMode`.
2. Agregar una regla en `AdaptationEngine`.
3. Asociar una paleta o cambio en `MainActivity`.

## Reto 6 - Incidencia: interfaz cambia demasiado seguido

Respuesta: revisar los umbrales de histéresis. El modo noche entra con `<40 lux` pero no sale hasta `>70 lux`; el exterior entra con `>1000 lux` y no sale hasta `<700 lux`.

## Reto 7 - Modificación sin romper el resto

La arquitectura permite modificar reglas del motor sin tocar los servicios de sensores. De igual manera, se puede cambiar la visualización sin modificar la captura del contexto.

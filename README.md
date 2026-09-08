# FocusSense - Taller 1: Desarrollo de una Aplicación Adaptativa

Aplicación Android nativa desarrollada en **Kotlin** para demostrar adaptación automática al contexto en tiempo real.

## 1. Problema y solución

Durante una sesión de estudio, las condiciones físicas y del dispositivo pueden cambiar sin que el usuario quiera detenerse a reconfigurar la aplicación. **FocusSense** es un temporizador de enfoque de 25 minutos que detecta automáticamente:

- **Luminosidad ambiental** mediante el sensor de luz del dispositivo.
- **Nivel de batería y estado de carga** mediante el sistema Android.

Con esa información adapta la interfaz sin intervención manual:

- **Modo noche:** se activa con poca luz.
- **Modo exterior:** aumenta el contraste cuando hay luz intensa.
- **Modo ECO:** con batería <= 20% y sin cargar, elimina animaciones y oculta contenido secundario.
- Los modos se pueden combinar: **noche + ECO** y **exterior + ECO**.

Esto implementa el pipeline solicitado:

`CONTEXTO -> PROCESAMIENTO -> DECISIÓN -> ADAPTACIÓN`

## 2. Arquitectura

```text
LightSensorService ----\
                       -> ContextManager -> AdaptationEngine -> MainActivity
BatteryService --------/          |                |                |
                            ContextSnapshot   AdaptationDecision   UI adaptada
```

La captura del contexto, el procesamiento/orquestación, la decisión y la interfaz se mantienen separados.

## 3. Requisitos

- Android Studio (versión reciente).
- JDK 17 (Android Studio ya incluye un JDK compatible en la mayoría de instalaciones).
- Android SDK 35.
- Dispositivo Android 8.0+ (API 26+) o emulador.
- Conexión a Internet solo para la primera sincronización de Gradle/Android Gradle Plugin si no están en caché.

**No se usan librerías externas en tiempo de ejecución.** La aplicación usa únicamente APIs de Android y Kotlin.

## 4. Cómo ejecutar

### Opción A - Android Studio

1. Descomprimir el proyecto.
2. Abrir la carpeta `FocusSense_Taller1` en Android Studio.
3. Esperar la sincronización de Gradle.
4. Seleccionar un dispositivo físico o emulador con Android 8.0 o superior.
5. Pulsar **Run** sobre el módulo `app`.

### Opción B - Línea de comandos

Con Android SDK configurado:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

En Windows:

```bat
gradlew.bat assembleDebug
gradlew.bat installDebug
```

El lanzador incluido descarga Gradle 8.9 en el primer uso si no existe localmente.

## 5. Demostración del comportamiento adaptativo

### Dispositivo físico

1. Iniciar la aplicación.
2. Cubrir el sensor de luz: debe aparecer **Modo noche**.
3. Iluminar el sensor con una linterna: debe aparecer **Modo exterior**.
4. Con batería <= 20% y sin estar cargando, debe activarse **Modo ECO** y desaparecer la tarjeta de consejo; la animación del temporizador se desactiva.

### Emulador Android

Usar **Extended controls / Virtual sensors** para variar la iluminación y **Battery** para cambiar el porcentaje de batería.

> Si el dispositivo no posee sensor de luz, la app lo informa y conserva la adaptación por batería, que sigue siendo una variable real del contexto.

## 6. Reglas de decisión

Las reglas están centralizadas en:

`app/src/main/java/pe/edu/uni/focussense/adaptation/AdaptationEngine.kt`

Umbrales principales:

- Entrada a noche: `< 40 lux`.
- Salida de noche: `> 70 lux`.
- Entrada a exterior: `> 1000 lux`.
- Salida de exterior: `< 700 lux`.
- Batería baja: `<= 20%` y no cargando.

Los distintos umbrales de entrada/salida implementan **histéresis**, evitando cambios repetitivos de interfaz cuando el sensor oscila cerca de un límite.

## 7. Ubicación del código relevante

| Elemento | Archivo / clase |
|---|---|
| Captura de luminosidad | `context/LightSensorService.kt` |
| Captura de batería | `context/BatteryService.kt` |
| Procesamiento/orquestación | `context/ContextManager.kt` |
| Decisión adaptativa | `adaptation/AdaptationEngine.kt` |
| Modelo de contexto | `model/ContextSnapshot.kt` |
| Resultado de adaptación | `model/AdaptationDecision.kt` |
| Adaptación observable / UI | `ui/MainActivity.kt` |

## 8. Metodología aplicada

El desarrollo se organizó con **Adaptive Software Development (ASD)**, tratado en el material de teoría como un enfoque iterativo y tolerante al cambio cuyo ciclo es **especular - colaborar - aprender**.

Ver `METODOLOGIA_ASD.md` para el desarrollo de las iteraciones y decisiones del equipo.

## 9. Validación del motor adaptativo

El motor de decisión no depende de Android y puede validarse con Kotlin/JVM:

```bash
kotlinc \
  app/src/main/java/pe/edu/uni/focussense/model/ContextSnapshot.kt \
  app/src/main/java/pe/edu/uni/focussense/model/AdaptationDecision.kt \
  app/src/main/java/pe/edu/uni/focussense/adaptation/AdaptationEngine.kt \
  tools/EngineSmokeTest.kt \
  -include-runtime -d engine-test.jar

java -jar engine-test.jar
```

Resultado esperado:

`OK: 6 escenarios del motor adaptativo validados.`

## 10. Documentos incluidos

- `docs/Documento_Tecnico.pdf`: entregable técnico, máximo 2 páginas.
- `docs/Documento_Tecnico.docx`: versión editable del mismo documento.
- `docs/Guion_Sustentacion.md`: guion para la exposición y demostración.
- `docs/Reto_Tecnico.md`: cambios rápidos sugeridos para prepararse ante el reto en vivo.
- `METODOLOGIA_ASD.md`: aplicación de la teoría de metodología ASD al proyecto.

## 11. Historial de commits

El ZIP incluye la carpeta `.git` con un historial de commits organizado por etapas. Para verlo:

```bash
git log --oneline --decorate
```

Al crear el repositorio final de GitHub se recomienda conservar este historial y que cada integrante agregue sus propios commits durante las mejoras o personalizaciones finales.

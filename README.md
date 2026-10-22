# MPAZ PRO 2.º – Aplicación móvil de apoyo didáctico (Segundo Básico)

Proyecto de la asignatura **DSY1105 – Desarrollo de Aplicaciones Móviles** (Evaluación Parcial 2).
Contexto: Escuela Escritora Marcela Paz (San Bernardo). MVP Android que reemplaza guías impresas
con actividades interactivas para las 4 asignaturas del nivel.

## Integrantes
- Rayén Aguayo Morales
- Armando Rosselot 

## Funcionalidades implementadas
- **Registro e inicio de sesión** con 3 perfiles: Jefatura UTP, Docente y Estudiante.
  La clave se guarda cifrada (PBKDF2 + sal) en la base de datos local (Room). La sesión se recuerda con DataStore.
- **Perfil UTP:** crea actividades (formulario con texto, número, fecha, checkbox y switch) y las publica o deja en borrador.
- **Perfil Estudiante:** ve las actividades publicadas, escucha la pregunta en voz alta y responde con intentos limitados.
- **Perfil Docente:** consulta el rendimiento por actividad (informe reservado al docente).
- **Validaciones visuales** en cada campo (borde de error, ícono y mensaje), con la lógica separada de la interfaz (`util/Validadores.kt`).
- **Animaciones:** botón con efecto al presionar, mensajes que aparecen/desaparecen, color animado en alternativas y barra de progreso.
- **Recursos nativos:** lectura en voz alta (TextToSpeech) y notificaciones locales (permiso `POST_NOTIFICATIONS`).
- Solo se usan datos ficticios.

## Arquitectura (MVVM)
```
data/        Room (entity, dao, AppDatabase), repository, session (DataStore)
util/        validaciones, cifrado de clave, fechas, voz, notificaciones
viewmodel/   AuthViewModel, ActividadViewModel, DocenteViewModel, EstudianteViewModel
ui/          components, navigation (Navigation Compose), screens por perfil, theme
```

## Pasos para ejecutar
1. Clonar el repositorio y abrirlo con Android Studio (versión reciente).
2. Esperar la sincronización de Gradle (**Sync Now**).
3. Ejecutar con ▶ en un emulador o dispositivo Android (minSdk 24).
4. Registrar un usuario de cada perfil (UTP, Docente, Estudiante) y probar el flujo:
   UTP crea y publica una actividad → Estudiante la responde → Docente revisa el rendimiento.

## Herramientas
Kotlin · Jetpack Compose · Material 3 · Room · DataStore · Navigation Compose · GitHub · Trello

## Flujo de trabajo en Git
```
git add .
git commit -m "feat: descripción técnica de lo que cambiaste"
git push
```

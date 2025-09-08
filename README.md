# Expediente Electrónico - Sistema de Gestión Médica

## Descripción
Sistema de gestión para consultorio médico desarrollado para la Universidad Politécnica de Sinaloa (UPSIN). Esta aplicación permite el manejo de expedientes médicos electrónicos, facilitando el seguimiento de pacientes y la gestión de consultas médicas.

## Características Principales
- ✅ Gestión de Pacientes
  - Registro de datos personales
  - Asignación de matrícula
  - Información académica (programa/carrera)
- 📋 Gestión de Consultas
  - Registro de consultas médicas
  - Historial clínico
  - Seguimiento de pacientes
- 📊 Generación de Reportes
  - Exportación de datos
  - Informes estadísticos
  - Resúmenes de consultas

## Tecnologías Utilizadas
- Java 21
- Spring Boot 3.5.0
- Swing (Interfaz Gráfica)
- MySQL (Base de Datos)
- Maven (Gestión de Dependencias)

### Dependencias Principales
- Spring Boot Starter Data JPA
- MySQL Connector
- FlatLaf (Look and Feel moderno)
- JCalendar
- Apache POI (Manejo de reportes Excel)
- Lombok

## Estructura del Proyecto
```
src/
├── main/
│   ├── java/rmp/expediente_electronico/
│   │   ├── gui/           # Interfaces gráficas
│   │   ├── modelo/        # Entidades y modelos
│   │   ├── repositorio/   # Capa de acceso a datos
│   │   └── servicio/      # Lógica de negocio
│   └── resources/         # Archivos de configuración
```

## Configuración del Entorno
1. Requisitos previos:
   - JDK 21 o superior
   - MySQL 8.0 o superior
   - Maven 3.6 o superior

2. Configuración de la base de datos:
   ```properties
   DATABASE_URL=jdbc:mysql://localhost:3306/expediente_db
   DATABASE_USER=tu_usuario
   DATABASE_PASSWORD=tu_contraseña
   ```

## Instalación y Ejecución
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/alfonso-ramos/ExpedienteElectronico.git
   ```

2. Navegar al directorio del proyecto:
   ```bash
   cd ExpedienteElectronico
   ```

3. Compilar el proyecto:
   ```bash
   mvn clean install
   ```

4. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

## Interfaz de Usuario
La aplicación cuenta con varias vistas principales:
- **Vista Principal**: Menú principal con acceso a todas las funcionalidades
- **Gestión de Pacientes**: Registro y edición de información de pacientes
- **Consultas Médicas**: Registro y seguimiento de consultas
- **Reportes**: Generación de informes y estadísticas

## Autores y Desarrolladores
- Alfonso Ramos
- Miguel Perez

---

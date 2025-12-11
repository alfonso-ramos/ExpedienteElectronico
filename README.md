# Expediente Electrónico - Sistema de Gestión Médica Universitaria 🏥

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://mysql.com)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Descripción

Sistema integral de gestión para consultorio médico desarrollado específicamente para la **Universidad Politécnica de Sinaloa (UPSIN)**. Esta aplicación permite el manejo completo de expedientes médicos electrónicos, facilitando el seguimiento de pacientes y la gestión eficiente de consultas médicas dentro del entorno universitario.

## ✨ Características Principales

### 👥 Gestión de Pacientes
- ✅ Registro completo de datos personales
- ✅ Asignación automática de matrícula estudiantil
- ✅ Información académica (programa/carrera)
- ✅ Datos médicos básicos (edad, peso, altura, IMC)
- ✅ Historial médico completo

### 🩺 Gestión de Consultas
- ✅ Registro detallado de consultas médicas
- ✅ Seguimiento de signos vitales
- ✅ Diagnósticos y tratamientos
- ✅ Prescripción de medicamentos
- ✅ Observaciones médicas
- ✅ Historial clínico cronológico

### 📊 Sistema de Reportes Avanzado
- ✅ **Reportes por fecha**: Análisis de consultas en rangos de tiempo específicos
- ✅ **Reportes mensuales**: Estadísticas detalladas por mes con gráficos semanales
- ✅ **Reportes anuales**: Resúmenes anuales con análisis por meses
- ✅ **Estadísticas médicas**: Conteos por diagnóstico, programa académico y género
- ✅ **Exportación Excel**: Reportes profesionales en formato .xlsx
- ✅ **Estilos personalizados**: Formatos visuales profesionales

### 🎨 Interfaz de Usuario
- ✅ Diseño moderno con FlatLaf
- ✅ Navegación intuitiva
- ✅ Validación de datos en tiempo real
- ✅ Manejo robusto de errores
- ✅ Experiencia de usuario optimizada

### 🤝 Contacto y soporte integrado
- ✅ Ventana dedicada (**VistaContacto**) con datos de los desarrolladores del proyecto
- ✅ Acceso directo desde la vista principal para dudas o solicitudes de soporte
- ✅ Cierre independiente (no termina la aplicación)

## 🛠 Tecnologías Utilizadas

### 🚀 Framework Principal
- **Java 21** - Lenguaje de programación moderno y estable
- **Spring Boot 3.5.0** - Framework para aplicaciones empresariales
- **MySQL 8.0** - Sistema de gestión de bases de datos
- **Maven** - Gestión de dependencias y construcción

### 📦 Dependencias Principales
- **Spring Boot Starter Data JPA** - Persistencia de datos
- **MySQL Connector/J** - Conexión a base de datos
- **FlatLaf 3.0** - Look and Feel moderno para Swing
- **JCalendar 1.4** - Componentes de calendario avanzados
- **Apache POI 5.4.1** - Generación de archivos Excel
- **Lombok** - Reducción de código boilerplate
- **Dotenv Java 3.0.0** - Gestión de variables de entorno
- **AbsoluteLayout** - Layout manager avanzado para GUI

## 🧱 Arquitectura del Sistema

El sistema sigue una arquitectura en capas apoyada por Spring Boot:

1. **Presentación (Swing + FlatLaf)**: Ventanas en `gui/` (VistaMain, VistaPaciente, VistaConsulta, VistaReporte y VistaContacto) inyectadas como beans para poder compartir estado y navegación.
2. **Servicios**: Clases en `servicio/` encapsulan la lógica de negocio y coordinan llamadas al repositorio.
3. **Repositorios (Spring Data JPA)**: Interfaces en `repositorio/` que exponen consultas hacia MySQL sin necesidad de SQL explícito.
4. **Persistencia**: Entidades en `modelo/` definen el mapeo ORM.
5. **Infraestructura**: `ExpedienteElectronicoApplication` levanta el contexto Spring en modo `headless=false` para crear las ventanas via `SwingUtilities`, y `dotenv-java` carga credenciales de la base de datos desde `.env`.

Esta estructura permite pruebas unitarias en los servicios, reutilización de ventanas mediante inyección y separación clara entre UI y datos.

## 🏗 Estructura del Proyecto

```
src/main/java/rmp/expediente_electronico/
├── 📱 gui/                    # Interfaces gráficas de usuario
│   ├── VistaMain.java         # Ventana principal del sistema
│   ├── VistaPaciente.java     # Gestión de pacientes
│   ├── VistaConsulta.java     # Registro de consultas médicas
│   ├── VistaReporte.java      # Generación de reportes
│   └── VistaContacto.java     # Información de soporte y contacto
├── 🏛 modelo/                 # Entidades y modelos de datos
│   ├── Paciente.java          # Entidad paciente
│   ├── Consulta.java          # Entidad consulta médica
│   └── Diagnostico.java       # Catálogo de diagnósticos
├── 💾 repositorio/           # Capa de acceso a datos (DAO)
│   ├── PacienteRepositorio.java
│   ├── ConsultaRepositorio.java
│   └── DiagnosticoRepositorio.java
└── 🔧 servicio/              # Lógica de negocio (Service Layer)
    ├── PacienteServicio.java
    ├── ConsultaServicio.java
    ├── DiagnosticoServicio.java
    └── ReporteServicio.java  # Servicio de generación de reportes
```

## ⚙️ Configuración del Entorno

### 📋 Requisitos Previos
- **JDK 21** o superior
- **MySQL 8.0** o superior
- **Maven 3.6** o superior

### 🗄️ Configuración de Base de Datos

1. Crear la base de datos:
```sql
CREATE DATABASE expediente_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Configurar las variables de entorno en `.env`:
```properties
DATABASE_URL=jdbc:mysql://localhost:3306/expediente_db
DATABASE_USER=tu_usuario_mysql
DATABASE_PASSWORD=tu_contraseña_mysql
```

## 🚀 Instalación y Ejecución

### 📥 Instalación

1. **Clonar el repositorio**:
```bash
git clone https://github.com/alfonso-ramos/ExpedienteElectronico.git
```

2. **Navegar al directorio**:
```bash
cd ExpedienteElectronico
```

3. **Instalar dependencias**:
```bash
mvn clean install
```

### ▶️ Ejecución

**Modo desarrollo**:
```bash
mvn spring-boot:run
```

**Generar JAR ejecutable**:
```bash
mvn clean package
java -jar target/expediente_electronico-0.0.1-SNAPSHOT.jar
```

La aplicación estará disponible en: **http://localhost:8080**

## 🎯 Funcionalidades Detalladas

### 👨‍⚕️ Módulo de Pacientes
- Registro de información personal completa
- Cálculo automático de IMC basado en peso y altura
- Validación de datos médicos
- Búsqueda y filtrado avanzado

### 📋 Módulo de Consultas
- Registro de consultas con fecha y hora
- Asociación automática con paciente
- Registro de signos vitales
- Diagnósticos con códigos CIE-10
- Seguimiento de tratamientos

### 📈 Módulo de Reportes
- **Reportes por Fecha**: Análisis de consultas en períodos específicos
- **Reportes Mensuales**: Estadísticas con desglose semanal
- **Reportes Anuales**: Resúmenes anuales con gráficos mensuales
- **Estadísticas Demográficas**: Análisis por género y programa académico
- **Estadísticas Clínicas**: Distribución por tipo de diagnóstico

## 🔒 Características de Seguridad

- ✅ Validación de datos de entrada
- ✅ Manejo seguro de excepciones
- ✅ Protección contra inyección SQL mediante JPA
- ✅ Gestión segura de sesiones
- ✅ Validación de tipos de archivo en reportes

## 🎨 Interfaz de Usuario

La aplicación cuenta con múltiples vistas especializadas:

| Vista | Descripción |
|-------|-------------|
| **🏠 Principal** | Menú principal con acceso a todas las funcionalidades |
| **👥 Pacientes** | Gestión completa del registro de pacientes |
| **🩺 Consultas** | Registro y seguimiento de consultas médicas |
| **📊 Reportes** | Generación de informes estadísticos y análisis |
| **📞 Contacto** | Información directa de los desarrolladores para soporte |

## 👥 Equipo de Desarrollo

**Desarrolladores Principales:**
- **Alfonso Ramos** - Arquitecto de software y desarrollador principal
- **Miguel Perez** - Desarrollador full-stack y especialista en UI/UX

**Contribuidores:**
- Comunidad de desarrolladores UPSIN

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 🆘 Soporte y Contacto

Para soporte técnico, reportes de bugs o sugerencias de mejora:
- 📧 Correo: soporte@expediente-electronico.com
- 💬 Issues: [GitHub Issues](https://github.com/alfonso-ramos/ExpedienteElectronico/issues)
- 📚 Documentación adicional: [Wiki del proyecto](https://github.com/alfonso-ramos/ExpedienteElectronico/wiki)

## 🔄 Historial de Versiones

### v1.0.0 (Actual)
- ✅ Sistema completo de gestión de pacientes
- ✅ Módulo de consultas médicas
- ✅ Generación de reportes avanzada
- ✅ Interfaz moderna y responsiva
- ✅ Compatibilidad con MySQL 8.0

---

**⭐ Si encuentras útil este proyecto, ¡dale una estrella en GitHub!**

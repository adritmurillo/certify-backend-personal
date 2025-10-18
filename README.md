# 🧾 CERTIFY Backend  
**Sistema web para emisión, gestión y verificación de certificados digitales**

📅 **Versión:** 1.0.0  
👤 **Cliente:** Grupo HackthonyPeru S.A.C.  
👨‍💻 **Equipo:** Proyecto CERTIFY  
🗓️ **Última actualización:** Octubre 2025  

---

## 🚀 Descripción General

**CERTIFY** es una plataforma web que permite a múltiples instituciones **emitir certificados digitales** y **validar su autenticidad mediante código QR**.  
Desarrollada bajo una arquitectura modular **Spring Boot + PostgreSQL**, garantiza **seguridad, trazabilidad y escalabilidad**.

El backend expone APIs RESTful que se comunican con el frontend (**React.js + Tailwind**) para gestionar empresas, usuarios, participantes y certificados.

---

## ⚙️ Tecnologías Utilizadas

| Tipo | Tecnología |
|------|-------------|
| Lenguaje | **Java 17** |
| Framework | **Spring Boot 3.5.6** |
| Base de datos | **PostgreSQL** |
| ORM | **Spring Data JPA** |
| Seguridad | **Spring Security + JWT** |
| Utilidades | **Lombok**, **DevTools**, **OpenPDF**, **ZXing** |
| Build Tool | **Maven** |
| Testing | **JUnit 5**, **Mockito** |

---

## 🧩 Características Principales

✅ Registro y gestión de **empresas emisoras** y usuarios con roles (RBAC).  
✅ Creación de **plantillas personalizadas** para certificados.  
✅ Emisión de **PDF con QR único y validación pública**.  
✅ Módulo de **reportes, auditoría y trazabilidad**.  
✅ **Respaldo y restauración** de base de datos.  
✅ Seguridad con **JWT** y encriptación de contraseñas **BCrypt**.  

---

## 🏗️ Arquitectura del Sistema
    ┌──────────────────────────┐
    │ FRONTEND (React + Vite)  │
    │ - UI/UX + Validación QR  │
    └───────────┬──────────────┘
                │ REST (JSON)
    ┌───────────▼──────────────┐
    │ BACKEND (Spring Boot)    │
    │ - Controladores REST     │
    │ - Servicios (Service)    │
    │ - Seguridad JWT          │
    │ - Generación PDF + QR    │
    └───────────┬──────────────┘
                │ JDBC
    ┌───────────▼──────────────┐
    │ PostgreSQL Database      │
    │ - Empresas, Usuarios     │
    │ - Certificados, Eventos  │
    │ - Auditorías, Roles      │
    └──────────────────────────┘
    ---

## 📁 Estructura del Proyecto

    certify-backend/
    ├── src/main/java/com/certify/backend
    │ ├── controller/ → Controladores REST
    │ ├── service/ → Lógica de negocio
    │ ├── repository/ → Repositorios JPA
    │ ├── model/ → Entidades (JPA)
    │ ├── config/ → Seguridad y JWT
    │ └── CertifyBackendApplication.java
    └── src/main/resources/
    ├── application.properties
    └── static/ y templates/ (opcional)

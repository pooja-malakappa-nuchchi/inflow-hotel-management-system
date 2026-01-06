# Project Report

## Hotel Booking and Management System Prototype

# Hotel Management System

Full-stack hotel management app with Spring Boot + React for rooms, bookings, payments, and analytics.

## Quick Start

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## Default Login
- Admin: `admin@hms.com` / `1234`

## Tech Stack
- Backend: Spring Boot, JWT, PostgreSQL
- Frontend: React, Tailwind CSS, Recharts

## Features
- Room & booking management
- Guest & staff management
- Inventory tracking
- Analytics dashboard (ADR, RevPAR, Occupancy)

## Prerequisites
- Java 17+, Node 16+, PostgreSQL 12+, Maven 3.6+


---

## Project Overview

This project is a **full-stack Hotel Booking and Management System (HMS)** designed to handle comprehensive hotel operations including room management, guest management, bookings, and payments.

The system has been developed using below web technologies:
- **Backend:** Spring Boot (Java) with PostgreSQL database
- **Frontend:** React (JavaScript) with sty;ing
- **Authentication:** JWT-based authentication with role-based access control (RBAC)
- **Architecture:** RESTful API following MVC pattern

## Intended Purpose 

The system is designed for **small to mid-sized hotels** that need a comprehensive digital solution for managing their operations.

### Target Users:

#### **Hotel Staff (STAFF Role)**
- View and manage all bookings
- Add and update room information
- Register and manage guest information
- Process payments
- View dashboard analytics

#### **Customers (Future Enhancement)**
- Browse available rooms
- Make online bookings
- View booking history
- Make payments
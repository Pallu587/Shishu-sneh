# Project Architecture: Shishu-Sneh

## Overview
Shishu-Sneh follows **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern. This ensures the codebase is modular, testable, and easy to maintain.

## Layers

### 1. Presentation Layer (Jetpack Compose)
- **UI**: Built entirely with Jetpack Compose for a modern, reactive user interface.
- **ViewModels**: Manage UI state and communicate with Use Cases. They use `StateFlow` to provide data to the UI.
- **Navigation**: Uses Jetpack Navigation Compose with a centralized `NavGraph`.

### 2. Domain Layer (Pure Kotlin)
- **Models**: Business logic entities (e.g., `Baby`, `Vaccine`).
- **Repositories (Interfaces)**: Define contracts for data operations.
- **Use Cases**: Encapsulate specific business rules (e.g., `GetVaccinesUseCase`, `GenerateVaccineSchedule`).
- **Utilities**: Specialized logic like the `VaccineScheduler`.

### 3. Data Layer
- **Room Database**: Local persistence for baby records, encrypted via **SQLCipher**.
- **Data Access Objects (DAOs)**: Interface for database operations.
- **Repositories (Implementation)**: Realize the domain contracts, managing data flow between local DB and preferences.
- **Preferences**: `DataStore` for managing application state like onboarding completion.

## Dependency Injection
- **Hilt**: Manages the lifecycle of dependencies across all layers, including WorkManager integration.

## Background Processing
- **WorkManager**: Handles periodic tasks like checking for overdue vaccinations and triggering system notifications.

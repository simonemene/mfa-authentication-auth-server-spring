# Spring Authorization Server MFA Demo

This project demonstrates a basic authentication flow using Spring Authorization Server with Multi-Factor Authentication (MFA).

The goal of the project is to show how an authentication server can be extended to support an additional verification step after the standard username and password login.

## Project Overview

The application implements a custom authentication flow based on:

- Spring Boot
- Spring Security
- Spring Authorization Server
- User authentication with username and password
- Multi-Factor Authentication using a second verification factor
- Custom user loading through `UserDetailsService`
- Role-based authentication using Spring Security authorities

## Main Features

The project includes:

- User authentication through Spring Security
- Custom `UserDetailsService` implementation
- Password-based login
- MFA verification step
- Basic role assignment with `ROLE_USER`
- Centralized exception handling using `ProblemDetail`
- Unit tests for authentication-related components

## Technologies Used

- Java
- Spring Boot
- Spring Security
- Spring Authorization Server
- Spring Data JPA
- Maven
- JUnit 5
- Mockito
- AssertJ

## Authentication Flow

The authentication flow is based on the following steps:

1. The user submits username and password.
2. Spring Security loads the user through the custom `UserDetailsService`.
3. If the user exists and the password is valid, the authentication process continues.
4. The MFA step is required before completing the authentication.
5. After successful MFA verification, the user is considered authenticated.
6. The authorization server can issue tokens for the authenticated user.

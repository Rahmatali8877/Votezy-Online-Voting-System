# Votezy – Online Voting System

A full-stack voting application built with Spring Boot, Spring Security, Spring Data JPA, MySQL, and a Bootstrap/JavaScript frontend.

## Features
- Voter registration and login with **session-based authentication** (Spring Security)
- Passwords stored as **BCrypt hashes**
- **Role-based access control**: `VOTER` and `ADMIN`
- One-user-one-vote enforced on the server using the logged-in user (the voter id is never sent by the client)
- Admin: manage candidates and voters, view votes, declare election results
- REST APIs with input validation and global exception handling

## Roles
| Action | Voter | Admin |
|---|---|---|
| Register / login | yes | yes |
| View candidates and results | yes | yes |
| Cast a vote | yes | no |
| Add / edit / delete candidates | no | yes |
| View / delete voters, view all votes | no | yes |
| Declare election result | no | yes |

## Run locally
1. Install Java 17 and MySQL, and create a database: `CREATE DATABASE votezy_db;`
2. Set environment variables (or edit `application.properties` locally, but never commit real passwords):
   - `DB_USERNAME` (default `root`)
   - `DB_PASSWORD`
   - `ADMIN_EMAIL` and `ADMIN_PASSWORD` (defaults: `admin@votezy.com` / `Admin@123`, change them)
3. Start the app: `./mvnw spring-boot:run`
4. Open http://localhost:8081
   - Voters register at `/register.html` and log in at `/login.html`
   - The admin account is created automatically on first start

## API overview
| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/voters/register` | public |
| POST | `/api/auth/login` (form: `email`, `password`) | public |
| POST | `/api/auth/logout` | logged in |
| GET | `/api/auth/me` | logged in |
| GET | `/api/candidate`, `/api/candidate/{id}` | logged in |
| POST / PUT / DELETE | `/api/candidate/add`, `/update/{id}`, `/delete/{id}` | admin |
| POST | `/api/votes/cast` (body: `candidateId`) | voter |
| GET | `/api/votes` | admin |
| GET | `/api/voters`, `/api/voters/{id}` and update/delete | admin |
| GET | `/api/election-result` | logged in |
| POST | `/api/election-result/declare` | admin |

## Screenshots
_Add screenshots of the login, voting, and admin pages here._

# SocialMediaDashboard

![SocialMediaDashboard demo](assets/social-media-dashboard-demo.gif)

## Description

`SocialMediaDashboard` is an interactive web dashboard that helps explore how **social media usage** relates to different **mental health indicators** using survey-based data.

The application is built with a decoupled architecture:

- **Backend:** Java + Spring Boot (REST API) with **JWT authentication**
- **Frontend:** Vue (SPA) powered by **Vite**, using **CoreUI** for the UI
- **Database:** MySQL (relational schema for respondents, demographics, usage metrics, mental health metrics, platforms, organizations, and users)

The dataset used to design and test the application comes from the public Kaggle dataset **“Social Media and Mental Health”**.

---

## Key features

- **Authentication & authorization (JWT)**
  - Secure login and protected routes
- **Interactive analytics dashboard**
  - KPI cards (e.g., respondents count, usage %, average distraction)
  - Charts and summaries for demographics and platform usage
- **Advanced filtering / exploration**
  - Analyze trends by age group, gender, occupation, platforms, etc.
- **Data management (authenticated users)**
  - Create new survey records (“Ingresar registro”)
  - Edit/delete records from a management table (“Modificar registro”)
  - Records are user-scoped (each user sees/manages their own entries)

---

## Tech stack

### Backend
- Java + Spring Boot (REST)
- Spring Data JPA (Hibernate)
- MySQL connector
- JWT (io.jsonwebtoken)

### Frontend
- Vue (SPA)
- Vite (dev server + build)
- Vue Router
- CoreUI (UI components/layout)

---

## Requirements

- **Java JDK** (use the version defined by the backend `pom.xml`)
- **Maven**
- **Node.js + npm**
- **MySQL Server**

---

## Installation

### 1) Clone the repository

```bash
git clone https://github.com/JABejaranoVela/SocialMediaDashboard.git
cd SocialMediaDashboard
```

### 2) Database setup (MySQL)

1. Create a database (example name: `social_media_dashboard`).
2. Run the SQL scripts located in the `db/` folder to create tables and (optionally) seed data.

> Note: If you prefer, you can also import the dataset first into a staging table and then normalize it into relational tables (as described in the project documentation).

### 3) Backend setup (Spring Boot)

Go to the backend folder and configure the database connection and JWT secret in `application.properties` (or via environment variables):

```bash
cd dashboard-backend/app
```

Run the backend:

```bash
mvn spring-boot:run
```

The backend is expected to run on:

- `http://localhost:9090`

### 4) Frontend setup (Vue + Vite)

In a new terminal:

```bash
cd dashboard-frontend
npm install
npm run dev
```

The frontend will start the Vite dev server (usually on `http://localhost:5173`).

During development, the frontend is configured to proxy API calls:

- Requests to `/api` → `http://localhost:9090`

---

## Usage

1. Start **MySQL**
2. Start the **backend** (Spring Boot)
3. Start the **frontend** (Vite)

Then open the frontend in your browser and use the sidebar navigation:

- **Inicio**: public dashboard overview
- **Iniciar sesión**: login form
- After login:
  - **Ingresar registro**
  - **Modificar registro**
  - **Cerrar sesión**

### Main routes (frontend)
- `/dashboard` → main analytics dashboard
- `/login` → authentication
- `/register` → create a new record
- `/edit` and `/edit-form` → record management / edit flow

---

## Configuration

### Backend (recommended)
- Do not hardcode secrets in `application.properties` for production.
- Use environment variables for:
  - DB URL / username / password
  - JWT secret

### Frontend
- The dev proxy is configured in `vite.config.js` so `/api` calls hit the backend locally.

---

## Notes & limitations

- The dataset is survey-based and intended for educational/analytical purposes.
- This project is designed for local development and demonstration. If deploying publicly, review:
  - secret management (JWT, DB credentials)
  - CORS configuration
  - HTTPS and production build strategy

---

## Resources

- Kaggle dataset (name): `Social Media and Mental Health`
- REST API + JWT authentication
- Vue + Vite SPA architecture

---

## License

No license file is currently included in this repository.
If you plan to open-source it, consider adding a license (e.g., MIT) and including a `LICENSE` file at the repository root.

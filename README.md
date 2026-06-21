<p align="center">
  <img src="src/main/resources/static/luxury_retreat_banner.png" alt="Luxury Retreat Royal Banner" width="100%">
</p>

<p align="center">
  <img src="src/main/resources/static/luxury_retreat_logo.png" alt="Luxury Retreat Gold Monogram" width="120px">
</p>

<h1 align="center">👑 LUXURY RETREAT 👑</h1>
<p align="center">
  <strong>The Most Ultra-Luxury Experiential Resorts & Reservation Platform in India</strong>
</p>

<p align="center">
  <a href="https://luxury-retreat.onrender.com" target="_blank">
    <img src="https://img.shields.io/badge/Live%20Demo-Visit%20Website-bda371?style=for-the-badge&logo=render&logoColor=fff" alt="Live Demo">
  </a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Theme-Royal%20Navy%20Blue-06122c?style=for-the-badge&logo=css3&logoColor=fff" alt="Royal Navy Blue Theme">
  <img src="https://img.shields.io/badge/Accent-Chamber%20Gold-bda371?style=for-the-badge&logo=adguard&logoColor=fff" alt="Chamber Gold Accent">
  <img src="https://img.shields.io/badge/Framework-Spring%20Boot%203-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3">
  <img src="https://img.shields.io/badge/Frontend-React%2018%20%2B%20Vite-61DAFB?style=for-the-badge&logo=react&logoColor=black" alt="React 18 + Vite">
</p>

---

## 🔱 Welcome to the Realm of Royalty

**Luxury Retreat** is an enterprise-grade hotel reservation and AI Concierge system. Built with a robust **Java Spring Boot 3.x** API backend and a cinematic, responsive **React 18 & Tailwind CSS** frontend compiled via a **Vite** build pipeline, the platform manages high-end hospitality features, adventure booking, theme gastronomy, and wellness spa operations.

Every visual element has been tailored to evoke a sense of nobility, transitioning from traditional themes into a deep, premium **Royal Navy Blue** and **Chamber Gold** aesthetic.

---

## 🎨 Visual Identity & Royal Design System

```
  ⚜️  Base Canvas        :  Deep Royal Navy Blue (#06122c)
  ⚜️  Grid & Card Panels :  Dark Navy Blue (#0c1b3d)
  ⚜️  Modals & Overlays  :  Midnight Blue (#050d21) with soft opacities
  ⚜️  Primary Accent     :  Gleaming Gold (#bda371) / Antique Gold (#a68a5c)
```

*   **Aspect-Ratio Video Backdrop**: At scroll position 0, a full-bleed widescreen YouTube background video plays, utilizing custom Javascript resizing math to auto-crop black letterboxes.
*   **Dynamic Sticky Header**: The navigation header overlays the video banner transparently, transition-blending to a semi-transparent royal navy backdrop (`rgba(6, 18, 44, 0.95)`) upon scroll.
*   **Cursive Monogram**: Features a gold "LX" monogram logo badge integrated into the header, footer, and active AI chat concierge panels.
*   **Zoom-on-Hover Staggered Grids**: Visual highlights of stays, dining, and extreme activities.

---

## 🚀 Technical Core & Capabilities

### 🛏️ Stay Chamber Reservation Grid
*   **6 Stays Properties**: Supports interactive bookings for *Garden Villa*, *Luxury Resort*, *Camp Luxury Retreat Tents*, *Adventure Resort*, *Enclave Villas*, and the *DATA Military Escape*.
*   **Live Catalog & ORM**: Real-time room retrieval and booking persistence powered by Hibernate ORM.

### 🍽️ Gastronomy Selection
*   **8 Theme Restaurants & Bars**: Details for Cafè 24, Villa Bistro, Parsi Dhaba, PNF, Crème Luxury Retreat, Sports Bar, Salaam Manekshaw, and Sky Garden.
*   **Interactive Table Booking**: Custom booking modals to schedule date, timing, and guest count.

### 💳 Checkout & Stepper Wizard
*   **Razorpay SDK Integration**: Frontend bookings connect to backend APIs using the official **Razorpay Java SDK** for order creation and secure signature verification.
*   **Flipping Stripe Card UI**: The credentials form uses **3D CSS perspective transforms (`rotateY`)** to flip the card dynamically to show CVV verification on input focus.
*   **POS Printable Receipt**: Generates printer-optimized invoices hiding browser frames.

### 🍷 Database-Driven RAG AI Concierge
*   **AI Chatbot Concierge**: A floating chat widget answering resort queries, backed by a Java-based **Retrieval-Augmented Generation (RAG)** pipeline.
*   **SQL Cosine-Similarity Matching**: User queries are tokenized, stop-words filtered, and mapped to knowledge documents using a native **SQL Cosine Similarity query** running on the H2/PostgreSQL engine.

### 🖥️ Developer Debug Console
*   **Simulated Tracing Telemetry**: Visualizes a conceptual monorepo structure, Prisma schema definition, pgvector RAG index mappings, and a simulated **LangGraph agent DAG intent-routing** log.
*   **NL-to-SQL Compiler**: Translates natural language questions into database queries.

---

## 🛠️ Technology Stack & Build Tools

*   **Java Backend**: Java 17, Spring Boot 3.x, Spring Data JPA, Hibernate, PostgreSQL runtime driver, H2 Local File Database.
*   **Node.js Build Pipeline**: Node.js, npm, package.json dependencies, Vite compiler, React 18, Tailwind CSS, PostCSS, Autoprefixer.
*   **Build Automation**: Apache Maven Wrapper (`mvnw`), **`frontend-maven-plugin`** (downloads local Node runtimes and compiles the React build during maven packages).
*   **APIs & SDKs**: Razorpay Java SDK (`com.razorpay`), Google Gemini API (optional fallback).

---

## 💻 How to Install & Run Locally

### Prerequisites
Make sure you have **Java 17** (or higher) installed on your system. Node/npm is handled automatically by the Maven build wrapper.

### Step 1: Clone the Repository
```bash
git clone https://github.com/Arpi-tect/Luxury.git
cd Luxury
```

### Step 2: Build & Package
Run the Maven wrapper to download dependencies, trigger the Vite frontend build, and package the static assets:
```bash
# On Windows
.\mvnw.cmd clean package -DskipTests

# On Linux / macOS
chmod +x mvnw
./mvnw clean package -DskipTests
```

### Step 3: Run the Spring Boot Server
```bash
# On Windows
.\mvnw.cmd spring-boot:run

# On Linux / macOS
./mvnw spring-boot:run
```

The server will spin up on port **`8082`**. Open your browser to:
👉 **[http://localhost:8082](http://localhost:8082)**

---

<p align="center">
  ⚜️ <b>Luxury Retreat</b> — Where hospitality meets imperial design. Powered by Spring Boot, PostgreSQL, and React. ⚜️
</p>

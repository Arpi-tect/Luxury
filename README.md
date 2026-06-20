# 🏨 LUXURY — Premium Hotel Reservation System

> **Where Every Stay is a Reign.**

Luxury is a commercial-grade, high-end hotel reservation platform designed to feel like booking a 7-star palace experience. Built with a robust **Java Spring Boot** REST API backend and a cinematic, responsive **React.js & Tailwind CSS** web application frontend.

---

## 🎨 Visual Identity & Theme

*   **Ivory-White Dominance (`#FDFBF6`)**: The primary canvas, evoking an airy palace suite at golden hour.
*   **Royal Blue Accents (`#2C4875` / `#5B7FB5`)**: Used as soft slate-blue washes, borders, and high-emphasis heading typography.
*   **Gold Primary Highlights (`#D4AF37` / `#E8C97A`)**: Used as elegant buttons, interactive highlights, and golden hover glows.
*   **Signature Backdrop — Ambient Flowing Silk Curtains**: A slow, wind-blown drape of light golden silk rendered as overlapping animated SVG paths that sway gently in the background, creating a living hotel lobby experience.

---

## 🚀 Key Features

*   **Palace Room Catalog Status Grid**: Browse and filter premium hotel room tiers (Standard, Deluxe, and Suite) with dynamic availability checks.
*   **Step-by-Step Checkout & Booking**: Guest registration with Passport/ID verification and direct selection of stay enhancements (Breakfast Buffet, Airport Shuttle pick-up, VIP Spa Lounge access).
*   **Contactless QR Check-in Receipt**: Generates a POS-style guest invoice with an isolated print layout that hides browser frames.
*   **Manager Analytics Dashboard**: Interactive operations metrics tracking hotel occupancy rate (%), gross revenues, RevPAR, and booking popularity graphs.
*   **Interactive Royal Concierge**: A gold-accented, RAG-inspired concierge assistant widget to answer booking queries and guide guests.

---

## 🛠️ Technology Stack

*   **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA, Hibernate, H2 Database.
*   **Frontend**: React (ES6+), Tailwind CSS, Babel, HTML5, Custom SVG animations, Print CSS.
*   **Build Tool**: Maven Wrapper (included).

---

## 💻 How to Run Locally

You only need **Java 17 or higher** installed.

### Step 1: Clone the repository
```bash
git clone https://github.com/Arpi-tect/Luxury.git
cd Luxury
```

### Step 2: Start the Application
Run the Maven wrapper command in your terminal:
*   **On Windows**:
    ```cmd
    mvnw.cmd spring-boot:run
    ```
*   **On Linux / macOS**:
    ```bash
    chmod +x mvnw
    ./mvnw spring-boot:run
    ```

The Tomcat server will start on port `8082`.

### Step 3: Open in Browser
👉 **[http://localhost:8082](http://localhost:8082)**

---

## 📁 Repository Structure
```text
Luxury/
├── pom.xml                     # Maven configurations
├── mvnw & mvnw.cmd             # Maven Wrapper scripts
├── data/                       # H2 Local database file storage
└── src/
    └── main/
        ├── java/com/apex/hotelreservation/
        │   ├── HotelReservationSystemApplication.java  # Main Boot class
        │   ├── model/                  # JPA Database Entities
        │   │   ├── Room.java           # Room details
        │   │   └── Booking.java        # Guest bookings
        │   ├── repository/             # Database access layers
        │   └── controller/             # REST controllers
        │       └── HotelController.java  # Core business API endpoints
        └── resources/
            ├── application.properties               # Config file (Port 8082)
            └── static/
                └── index.html                       # React Web Client
```

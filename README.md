# Luxura - Premium Hotel Reservation Dashboard

An enterprise-grade, full-stack hospitality booking management application built with a **Java Spring Boot** REST API backend and a responsive, interactive **React.js & Tailwind CSS** web client dashboard.

---

## 🚀 Features

*   **Dynamic Room Status Grid**: Browse and filter available hotel rooms categorized into **Standard** ($100/night), **Deluxe** ($180/night), and **Suite** ($350/night) with color-coded availability badges.
*   **Stay Enhancements (Add-ons)**: Support for check-in toggles including:
    - Breakfast Buffet (+$15/day)
    - Airport Shuttle Pickup (+$30 flat fee)
    - VIP Lounge & Spa Access (+$50 flat fee)
*   **Verification ID Management**: Store guest identification records (Passport, National ID, Driver's License) with document numbers.
*   **Printable Invoice POS Receipts**: Generates clean guest bill itemization. Uses CSS `@media print` rules to isolate the POS receipt layout for printing, automatically hiding website buttons and layout headers.
*   **Manager Analytics Dashboard**: High-level manager cockpit reporting hotel occupancy rate (%), gross revenue, RevPAR, and a vertical SVG popularity bar chart mapping standard vs deluxe vs suite booking distributions.
*   **Google OAuth & Guest Login**: Premium access portal supporting Google Identity Services sign-in or one-click Guest Session bypass.

---

## 🛠️ Technology Stack

*   **Backend**: Java 17, Spring Boot 3.x, Spring Data JPA, Hibernate, H2 Database.
*   **Frontend**: React (ES6+), Tailwind CSS, Babel, HTML5, Print CSS, Custom Responsive SVG Bar Charts.
*   **Build Tool**: Maven Wrapper (included).

---

## 💻 How to Run Locally

You only need **Java 17 or higher** installed.

### Step 1: Clone the repository
```bash
git clone https://github.com/Arpi-tect/Luxura.git
cd Luxura
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
HotelReservationSystem/
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

---

## 📡 REST API Documentation

*   **Get Room Catalog**: `GET /api/hotel/rooms`
*   **Get All Guest Reservations**: `GET /api/hotel/bookings`
*   **Process Booking**: `POST /api/hotel/book`
    *   Parameters: `guestName`, `contact`, `roomNumber`, `nights`, `idType`, `idNumber`, `breakfast`, `shuttle`, `spa`
*   **Cancel Reservation**: `DELETE /api/hotel/cancel/{bookingId}`
*   **Operations Analytics**: `GET /api/hotel/analytics` (Returns occupancy metrics, RevPAR, gross revenues, and category allocations)

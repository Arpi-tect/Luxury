# CodeAlpha_HotelReservationSystem

An advanced, full-stack enterprise hospitality booking management application built with a **Java Spring Boot** REST API backend and a responsive **React.js & Tailwind CSS** web client dashboard.

## 🚀 Features
*   **Dynamic Room Catalog Grid**: Browse and filter available hotel rooms categorized into **Standard** ($100/night), **Deluxe** ($180/night), and **Suite** ($350/night) with color-coded availability badges.
*   **Checkout & Booking Form**: Form to book rooms containing inputs for guest name, contact details, stay duration, and immediate calculation of the total cost.
*   **Simulated Payment Gateway**: Presents a mock payment prompt, details the stay invoice, authorizes payment, and issues a confirmation receipt.
*   **Reservation Directory Table**: Tracks all active bookings containing check-in reference numbers, guest details, room details, and overall invoicing details.
*   **Cancellation & Refund processing**: Allow reservations to be deleted, freeing up the room immediately and initiating a simulated refund transaction.
*   **Database Persistence**: Automatically saves check-in entries using local file-based H2 database persistence.

---

## 🛠️ Technology Stack
*   **Backend**: Java 17+, Spring Boot 3.x, Spring Data JPA, H2 Database.
*   **Frontend**: React (ES6+), Tailwind CSS, Babel, HTML5.
*   **Build Tool**: Maven Wrapper (included).

---

## 💻 How to Run Locally

You only need **Java 17 or higher** installed.

### Step 1: Clone the repository
```bash
git clone https://github.com/<your-username>/CodeAlpha_HotelReservationSystem.git
cd CodeAlpha_HotelReservationSystem
```

### Step 2: Boot up the Spring Boot Application
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

The server will build and initialize the database, starting the Tomcat server on port `8082`.

### Step 3: Open in Browser
Open your browser and navigate to:
👉 **[http://localhost:8082](http://localhost:8082)**

---

## 📁 Repository Structure
```text
HotelReservationSystem/
├── pom.xml                     # Maven configurations
├── mvnw & mvnw.cmd             # Maven Wrapper scripts
├── data/                       # Local database file storage
└── src/
    └── main/
        ├── java/com/codealpha/hotelreservation/
        │   ├── HotelReservationSystemApplication.java  # Main Boot class
        │   ├── model/                  # JPA Database Entities
        │   │   ├── Room.java           # Room details
        │   │   └── Booking.java        # Guest bookings
        │   ├── repository/             # Database access layers
        │   └── controller/             # REST controllers
        │       └── HotelController.java  # Core business API endpoints
        └── resources/
            ├── application.properties               # Database properties (Port 8082)
            └── static/
                └── index.html                       # React & Tailwind Web Client
```

---

## 📡 REST API Documentation

*   **Get All Rooms catalog**: `GET /api/hotel/rooms`
*   **Get All Guest Reservations**: `GET /api/hotel/bookings`
*   **Process Booking**: `POST /api/hotel/book?guestName=John&contact=+91...&roomNumber=201&nights=3`
*   **Cancel Reservation**: `DELETE /api/hotel/cancel/{bookingId}`

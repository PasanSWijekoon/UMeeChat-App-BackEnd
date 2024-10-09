# UMeeChat - Backend (Java EE + Hibernate)

## Overview

UMeeChat is a real-time chat application with a Java EE backend that handles user authentication, profile management, and real-time messaging. The backend uses Hibernate ORM to interact with a MySQL database and provides RESTful APIs for the React Native frontend.

## Features

- **User Registration**: Allows new users to sign up with their mobile number, name, password, and profile picture.
- **User Authentication**: Provides secure login functionality with mobile number and password validation.
- **Real-Time Messaging**: Enables users to send and receive messages, with online/offline and message status tracking (sent/seen).
- **Profile Management**: Handles profile image uploads and updates.
- **Status Tracking**: Tracks user statuses (online/offline) and message statuses (sent, seen).

## Tech Stack

- **Java EE 7**: For building the backend APIs.
- **Hibernate ORM**: For database interaction.
- **MySQL**: As the relational database for storing user and chat data.
- **Gson**: For handling JSON serialization and deserialization.
- **Servlets**: For handling HTTP requests and responses.

## Installation

### Prerequisites

- **JDK 8 or later**
- **Apache TomEE** or **GlassFish** for deployment
- **MySQL** for database management
- **Maven** for managing dependencies and building the project

### Setup and Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/umeechat-backend.git
   ```

2. **Navigate to the project directory:**
   ```bash
   cd umeechat-backend
   ```

3. **Set up the MySQL database:**
   - Create a new database for the application:
     ```sql
     CREATE DATABASE umeechat_db;
     ```
   - Update the database configuration in the `hibernate.cfg.xml` file located in the `src/main/resources` folder:
     ```xml
     <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/umeechat_db</property>
     <property name="hibernate.connection.username">your_db_username</property>
     <property name="hibernate.connection.password">your_db_password</property>
     ```

4. **Build the project using Maven:**
   ```bash
   mvn clean install
   ```

5. **Deploy the generated WAR file to your Java EE server (TomEE/GlassFish):**
   - Copy the WAR file from the `target` directory to your server’s deployment folder.

6. **Run the MySQL database migration scripts** (if required).

### Project Structure

- **/src/main/java/controller**: Contains the Java Servlet classes that handle the backend API requests (SignUp, SignIn, LoadChat, SendChat).
- **/src/main/java/entity**: Entity classes that represent the database tables (User, Chat, Chat_Status, User_Status).
- **/src/main/resources**: Contains Hibernate configuration files.
- **/webapp**: Contains static resources and deployment configurations (WEB-INF).

### API Endpoints

1. **Sign Up** (`POST /SignUp`):
   - Registers a new user by creating a new record in the `User` table and optionally saving a profile picture.
   - Request body:
     ```json
     {
       "mobile": "0771234567",
       "firstName": "John",
       "lastName": "Doe",
       "password": "StrongPassword123",
       "profileImage": "base64Image"
     }
     ```

2. **Sign In** (`POST /SignIn`):
   - Authenticates the user with a mobile number and password.
   - Request body:
     ```json
     {
       "mobile": "0771234567",
       "password": "StrongPassword123"
     }
     ```

3. **Load Home Data** (`GET /LoadHomeData?id={user_id}`):
   - Fetches a list of recent conversations for the logged-in user along with the status (online/offline) of other users.
   - Example request: `/LoadHomeData?id=1`

4. **Load Chat** (`GET /LoadChat?logged_user_id={user_id}&other_user_id={other_user_id}`):
   - Retrieves the chat history between two users.
   - Example request: `/LoadChat?logged_user_id=1&other_user_id=2`

5. **Send Chat** (`GET /SendChat?logged_user_id={user_id}&other_user_id={other_user_id}&message={message}`):
   - Sends a new chat message between two users.
   - Example request: `/SendChat?logged_user_id=1&other_user_id=2&message=Hello!`

### Database Schema

- **User Table**:
  - Stores user information such as first name, last name, mobile number, password (hashed), and profile image path.
  - Key columns: `id`, `first_name`, `last_name`, `mobile`, `password`, `profile_image`, `registered_date`.

- **Chat Table**:
  - Stores chat messages between users, including the sender, receiver, message, and timestamp.
  - Key columns: `id`, `from_user`, `to_user`, `message`, `date_time`, `chat_status_id`.

- **User_Status Table**:
  - Tracks the online/offline status of each user.
  - Key columns: `id`, `status`.

- **Chat_Status Table**:
  - Tracks the status of each message (e.g., sent, seen).
  - Key columns: `id`, `status`.

### Hibernate Configuration

- Hibernate is configured via the `hibernate.cfg.xml` file located in `src/main/resources`.
- Make sure the database connection URL, username, and password are correctly set to connect to your MySQL database.

```xml
<hibernate-configuration>
    <session-factory>
        <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/umeechat_db</property>
        <property name="hibernate.connection.username">your_db_username</property>
        <property name="hibernate.connection.password">your_db_password</property>
        <property name="hibernate.hbm2ddl.auto">update</property>
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>
    </session-factory>
</hibernate-configuration>
```

### Security

- Ensure that passwords are securely hashed before being stored in the database.
- Use HTTPS for secure communication between the frontend and backend.
- Implement additional security measures like rate limiting and input validation to prevent attacks like SQL injection.

### Deployment

1. **Build the Project**: Use Maven to build the project and generate the WAR file.
   ```bash
   mvn clean install
   ```

2. **Deploy the WAR File**: Deploy the WAR file to your Java EE server (TomEE, GlassFish, etc.).
   
3. **Access the Backend**: Ensure that the backend APIs are accessible via the configured domain (e.g., `http://localhost:8080/UMee_Chat_App`).

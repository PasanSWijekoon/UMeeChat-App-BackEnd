package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Session;

@WebServlet(name = "SendChat", urlPatterns = {"/SendChat"})
public class SendChat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Add CORS headers to allow cross-origin requests
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Update with your frontend origin
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Initialize Gson object for JSON conversion
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("success", false); // Default response status as failure
        
        // Open Hibernate session for database interaction
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        // Get parameters from the request (IDs and message)
        String logged_user_id = request.getParameter("logged_user_id"); // Sender user ID
        String other_user_id = request.getParameter("other_user_id");   // Receiver user ID
        String message = request.getParameter("message");               // Chat message content
        
        // Fetch user objects based on the provided user IDs
        User logged_user = (User) session.get(User.class, Integer.parseInt(logged_user_id));
        User other_user = (User) session.get(User.class, Integer.parseInt(other_user_id));
        
        // Create a new Chat object and populate it with details
        Chat chat = new Chat();
        
        // Set chat status (e.g., status ID 2 might represent 'sent' status)
        Chat_Status chat_Status = (Chat_Status) session.get(Chat_Status.class, 2);
        chat.setChat_status_id(chat_Status); // Set chat status as 'sent'
        
        // Set the current date and time as the chat timestamp
        chat.setDate_time(new Date());
        
        // Set the sender and receiver of the message
        chat.setFrom_user(logged_user);
        chat.setTo_user(other_user);
        
        // Set the chat message
        chat.setMessage(message);
        
        // Save the chat object in the database
        session.save(chat);
        
        try {
            // Commit the transaction to ensure the chat is saved
            session.beginTransaction().commit();
            responseJson.addProperty("success", true); // Mark success in response
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception and roll back if necessary
            session.getTransaction().rollback();
        } finally {
            // Close the session
            session.close();
        }
        
        // Set response content type to JSON and return the response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson)); // Send response JSON object
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle CORS preflight requests
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Update with your frontend origin
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setStatus(HttpServletResponse.SC_OK); // Return 200 OK for preflight request
    }
}

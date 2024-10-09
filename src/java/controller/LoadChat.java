package controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import entity.Chat;
import entity.Chat_Status;
import entity.User;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadChat", urlPatterns = {"/LoadChat"})
public class LoadChat extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Add CORS headers to the response
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Change to your frontend's origin
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // Allow credentials (if needed)

        // Create Gson object for JSON conversion
        Gson gson = new Gson();
        
        // Open Hibernate session for database interaction
        Session session = HibernateUtil.getSessionFactory().openSession();

        // Get the logged-in user ID and the other user's ID from request parameters
        String logged_user_id = request.getParameter("logged_user_id");
        String other_user_id = request.getParameter("other_user_id");

        // Fetch user objects from the database based on provided user IDs
        User logged_user = (User) session.get(User.class, Integer.parseInt(logged_user_id));
        User other_user = (User) session.get(User.class, Integer.parseInt(other_user_id));

        // Create a criteria query to fetch chat messages between the logged-in user and the other user
        Criteria criteria1 = session.createCriteria(Chat.class);
        criteria1.add(Restrictions.or(
                Restrictions.and(
                        Restrictions.eq("from_user", logged_user),
                        Restrictions.eq("to_user", other_user)
                ),
                Restrictions.and(
                        Restrictions.eq("from_user", other_user),
                        Restrictions.eq("to_user", logged_user)
                )
        ));
        
        // Order the chats by date and time in ascending order
        criteria1.addOrder(Order.asc("date_time"));

        // Retrieve the list of chat messages
        List<Chat> chat_list = criteria1.list();

        // Fetch the chat status object (assumed to be "seen" status with ID 1)
        Chat_Status chat_Status = (Chat_Status) session.get(Chat_Status.class, 1);

        // Date format for the chat timestamps
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

        // Create a JSON array to hold the chat messages
        JsonArray chatAarry = new JsonArray();

        // Loop through the list of chats and prepare the JSON object for each chat
        for (Chat chat : chat_list) {

            JsonObject chatObject = new JsonObject();
            chatObject.addProperty("message", chat.getMessage());
            chatObject.addProperty("datetime", dateFormat.format(chat.getDate_time()));

            // Determine whether the message is from the other user (left) or the logged-in user (right)
            if (chat.getFrom_user().getId() == other_user.getId()) {
                // If the message is from the other user, mark it as "left" (received)
                chatObject.addProperty("side", "left");

                // Update the message status to "seen" (chat status ID 2 means 'unseen', and we are updating it to 1 for 'seen')
                if (chat.getChat_status_id().getId() == 2) {
                    chat.setChat_status_id(chat_Status); // Set the chat status to 'seen'
                    session.update(chat); // Update the status in the database
                }
            } else {
                // If the message is from the logged-in user, mark it as "right" (sent)
                chatObject.addProperty("side", "right");
                chatObject.addProperty("status", chat.getChat_status_id().getId()); // Include the current message status
            }

            // Add the chat object to the JSON array
            chatAarry.add(chatObject);
        }

        // Commit the transaction to save any updates (if message status was updated)
        session.beginTransaction().commit();

        // Set the response content type to JSON
        response.setContentType("application/json");

        // Write the JSON array of chats to the response
        response.getWriter().write(gson.toJson(chatAarry));

        // Close the Hibernate session
        session.close();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle preflight requests (CORS preflight checks)
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Allow origin
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS"); // Allowed methods
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization"); // Allowed headers
        resp.setStatus(HttpServletResponse.SC_OK); // Return 200 OK for preflight request
    }
}

package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject; 
import com.google.gson.JsonArray;
import entity.Chat;
import entity.User;
import entity.User_Status;
import java.io.IOException; 
import java.io.File;
import java.util.List;
import java.text.SimpleDateFormat;
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

@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Add CORS headers to allow requests from your React Native app or any other origin
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Change to the actual origin of the frontend
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true"); // If credentials (e.g., cookies) are involved

        // Initialize Gson object for JSON conversion
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();

        // Default error message
        responseJson.addProperty("Success", false);
        responseJson.addProperty("message", "Unable to process your request");

        try {
            // Start Hibernate session for database operations
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Get the logged-in user's ID from the request parameters
            String user_id = request.getParameter("id");
            
            // Fetch the user object from the database based on user ID
            User user = (User) session.get(User.class, Integer.parseInt(user_id));
            
            // Update the user's status to 'online' (assuming user_status 1 is 'online')
            User_Status user_Status = (User_Status) session.get(User_Status.class, 1);
            user.setUser_status(user_Status);
            session.update(user);
            
            // Create a criteria query to get all other users except the logged-in user
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.ne("id", user.getId()));
            List<User> otherUserList = criteria1.list(); // List of other users
            
            // Prepare a JSON array to hold chat data for each other user
            JsonArray jsonChatArray = new JsonArray();
            
            // Loop through all other users and fetch the most recent chat with the logged-in user
            for(User otherUser : otherUserList) {

                // Create a criteria query to get the latest chat between the logged-in user and other users
                Criteria criteria2 = session.createCriteria(Chat.class);
                criteria2.add(Restrictions.or(
                    Restrictions.and(
                        Restrictions.eq("from_user", user),
                        Restrictions.eq("to_user", otherUser)
                    ),
                    Restrictions.and(
                        Restrictions.eq("from_user", otherUser),
                        Restrictions.eq("to_user", user)
                    )
                ));

                // Order the chat messages by ID in descending order (latest first)
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1); // Get only the latest chat message
                
                // Prepare JSON object for each user
                JsonObject jsonChatItem = new JsonObject();
                jsonChatItem.addProperty("other_user_id", otherUser.getId());
                jsonChatItem.addProperty("other_user_mobile", otherUser.getMobile());
                jsonChatItem.addProperty("other_user_name", otherUser.getFirst_name() + " " + otherUser.getLast_name());
                jsonChatItem.addProperty("other_user_status", otherUser.getUser_status().getId());
                
                // Check if avatar image exists for the other user
                String serverPath = request.getServletContext().getRealPath("");
                String otherAvatarImagePath = serverPath + File.separator + "AvatarImages" + File.separator + otherUser.getMobile() + ".jpg";
                File otherAvatarImagePathFile = new File(otherAvatarImagePath);
                
                if(otherAvatarImagePathFile.exists()) {
                    jsonChatItem.addProperty("avatar_image_found", true); // Avatar exists
                } else {
                    jsonChatItem.addProperty("avatar_image_found", false); // No avatar, use initials
                    jsonChatItem.addProperty("other_user_avatar_letters", otherUser.getFirst_name().charAt(0) + "" + otherUser.getLast_name().charAt(0));
                }
                
                // Fetch the chat list between the logged-in user and the other user
                List<Chat> dbChatList = criteria2.list();
                SimpleDateFormat dateformate = new SimpleDateFormat("hh:mm a");

                // If no chat exists, display default message
                if(dbChatList.isEmpty()) {
                    jsonChatItem.addProperty("message", "Let's Start New Conversation");
                    jsonChatItem.addProperty("time", dateformate.format(user.getRegistered_date()));
                    jsonChatItem.addProperty("chat_status_id", 1); // Default chat status
                } else {
                    // If chat exists, add the latest message and timestamp
                    jsonChatItem.addProperty("message", dbChatList.get(0).getMessage());
                    jsonChatItem.addProperty("time", dateformate.format(dbChatList.get(0).getDate_time()));
                    jsonChatItem.addProperty("chat_status_id", dbChatList.get(0).getChat_status_id().getId());
                }

                // Add the chat item to the chat array
                jsonChatArray.add(jsonChatItem);
            }
            
            // Set success response
            responseJson.addProperty("Success", true);
            responseJson.addProperty("message", "Success");
            responseJson.add("user", gson.toJsonTree(user)); // Add user details to response
            responseJson.add("jsonChatArray", gson.toJsonTree(jsonChatArray)); // Add chat array to response
            
            // Commit the transaction and close the session
            session.beginTransaction().commit();
            session.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Set response content type to JSON and write the response JSON object
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle preflight requests for CORS
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Change to the actual origin of the frontend
        resp.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setStatus(HttpServletResponse.SC_OK); // Return 200 OK for OPTIONS request
    }
}

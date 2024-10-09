package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.User;
import entity.User_Status;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import model.Validations;

@MultipartConfig // Required for file upload handling
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Add CORS headers to allow cross-origin requests
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Replace with the frontend origin
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        
        // Initialize Gson for JSON response
        Gson gson = new Gson();
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("Success", false); // Default response is failure
        
        // Retrieve form parameters from the request
        String mobile = request.getParameter("mobile");
        String firstname = request.getParameter("firstName");
        String lastname = request.getParameter("lastName");
        String password = request.getParameter("password");
        Part profileImage = request.getPart("profileImage"); // Handle profile image upload
        
        // Validate form inputs
        if (mobile.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your Mobile Number");
        } else if (!Validations.isMobileNumberValid(mobile)) {
            responseJson.addProperty("message", "Invalid Mobile Number");
        } else if (firstname.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your First Name");
        } else if (lastname.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your Last Name");
        } else if (password.isEmpty()) {
            responseJson.addProperty("message", "Please Enter Your Password");
        } else if (!Validations.isPasswordValid(password)) {
            responseJson.addProperty("message", "Password must include at least one uppercase letter, number, special character, and be at least eight characters long");
        } else {
            
            // Start a new Hibernate session
            Session session = HibernateUtil.getSessionFactory().openSession();
            
            // Check if the mobile number is already registered
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("mobile", mobile));
            
            if (!criteria1.list().isEmpty()) { 
                // If mobile number is already used, return an error message
                responseJson.addProperty("message", "Mobile Number Already Used");
            } else {
                // Create a new User object and populate it with form data
                User user = new User();
                user.setFirst_name(firstname);
                user.setLast_name(lastname);
                user.setMobile(mobile);
                user.setPassword(password);
                user.setRegistered_date(new Date()); // Set the registration date
                
                // Set the default user status (e.g., 2 could represent 'Active' status)
                User_Status user_Status = (User_Status) session.get(User_Status.class, 2);
                user.setUser_status(user_Status);
                
                // Save the new user to the database
                session.save(user);
                session.beginTransaction().commit();
                
                // Handle profile image upload if provided
                if (profileImage != null) {
                    String applicationPath = request.getServletContext().getRealPath(""); // Get the server's file path
                    String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");
                    
                    // Construct the file path to save the image
                    String avatarImagePath = newApplicationPath + File.separator + "AvatarImages" + File.separator + mobile + ".jpg";
                    System.out.println(avatarImagePath);

                    // Save the profile image to the server
                    File file = new File(avatarImagePath);
                    Files.copy(profileImage.getInputStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                
                // Registration successful
                responseJson.addProperty("Success", true);
                responseJson.addProperty("message", "Registration Complete");
            }
            
            // Close the session
            session.close();
        }
        
        // Set response content type to JSON and return the response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJson)); // Send the response JSON
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Handle CORS preflight request for POST method
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081"); // Replace with the frontend origin
        resp.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        resp.setStatus(HttpServletResponse.SC_OK); // Respond with OK for OPTIONS request
    }
}

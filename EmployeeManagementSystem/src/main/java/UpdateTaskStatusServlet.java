import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/UpdateTaskStatusServlet")
public class UpdateTaskStatusServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;

        // Read the incoming JSON data
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        // Parse the JSON data
        String jsonData = sb.toString();
        int taskId = -1;
        String status = "";

        try {
            // Extract taskId and status from JSON
            String[] keyValuePairs = jsonData.replaceAll("[{}\"]", "").split(",");
            for (String pair : keyValuePairs) {
                String[] entry = pair.split(":");
                if (entry[0].trim().equals("taskId")) {
                    taskId = Integer.parseInt(entry[1].trim());
                } else if (entry[0].trim().equals("status")) {
                    status = entry[1].trim();
                }
            }

            if (taskId == -1 || status.isEmpty()) {
                response.getWriter().println("Invalid taskId or status.");
                return;
            }

            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");
            
            // Update the task status in the database
            String query = "UPDATE assigned_work SET update_status = ? WHERE work_id = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, status);
            pst.setInt(2, taskId);
            int rowCount = pst.executeUpdate();

            if (rowCount > 0) {
                response.getWriter().println("Status updated successfully!");
            } else {
                response.getWriter().println("Failed to update status.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}

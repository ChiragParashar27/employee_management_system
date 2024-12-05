import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/UnapprovedEmployeesServlet")
public class UnapprovedEmployeesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        StringBuilder json = new StringBuilder();
        json.append("["); // Start of JSON array
        
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@")) {
            // Use correct condition based on your table schema (e.g., 0 for false, 1 for true)
            String query = "SELECT employee_id, first_name, last_name, status FROM employees WHERE status = 'pending'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            boolean first = true; // To manage commas between JSON objects
            
            while (rs.next()) {
                if (!first) {
                    json.append(","); // Add a comma before the next object if it's not the first
                }
                first = false;

                // Building JSON response
                json.append("{");
                json.append("\"employee_id\":").append(rs.getInt("employee_id")).append(",");
                json.append("\"first_name\":\"").append(rs.getString("first_name")).append("\",");
                json.append("\"last_name\":\"").append(rs.getString("last_name")).append("\",");
                json.append("\"status\":\"").append(rs.getString("status")).append("\"");
                json.append("}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Set error status and write an error message for easier debugging
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
            return; // Stop further processing
        }

        json.append("]"); // End of JSON array
        out.print(json.toString()); // Output the JSON response
        out.close();
    }
}

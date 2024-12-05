import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/HRDashboardServlet")
public class HRDashboardServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html"); // Ensure the content type is HTML

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        
        PrintWriter out = response.getWriter();
        
        try {
            // Database connection
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@"); // Replace with actual password

            // SQL query to fetch approved employees
            String query = "SELECT employee_id, first_name, last_name FROM employees WHERE status = 'approved'";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();

            // Generate HTML output
            out.println("<html><head><title>HR Dashboard</title></head><body>");
            out.println("<h2>Approved Employees</h2>");

            // Check if any employees exist
            if (!rs.isBeforeFirst()) { 
                out.println("<p>No approved employees available.</p>");
            } else {
                // Display each employee with a task assignment form
                while (rs.next()) {
                    out.println("<div>");
                    out.println("<p>Employee ID: " + rs.getInt("employee_id") + " | Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + "</p>");
                    
                    // Task Assignment Form
                    out.println("<form action='AssignTaskServlet' method='post'>");
                    out.println("<input type='hidden' name='employee_id' value='" + rs.getInt("employee_id") + "'>");
                    out.println("<label for='task_description'>Assign Task:</label>");
                    out.println("<input type='text' name='task_description' required><br><br>");
                    out.println("<label for='due_date'>Due Date:</label>");
                    out.println("<input type='date' name='due_date' required><br><br>");
                    out.println("<input type='submit' value='Assign Task'>");
                    out.println("</form>");
                    out.println("</div><hr>");
                }
            }

            out.println("</body></html>");

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("<p>Database error occurred: " + e.getMessage() + "</p>");
        } finally {
            // Close the ResultSet, PreparedStatement, and Connection to release resources
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

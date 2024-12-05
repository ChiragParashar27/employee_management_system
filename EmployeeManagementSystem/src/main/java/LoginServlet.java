import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");
            
            // Query to check user credentials and retrieve employee_id
            String query = "SELECT employee_id, role FROM login WHERE username = ? AND password = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                int employeeId = rs.getInt("employee_id");

                // Store employee_id in session
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("employee_id", employeeId); // Save employee_id in session

                if ("hr".equals(role)) {
                    response.sendRedirect("hr_dashboard.html");
                } else if ("employee".equals(role)) {
                    // Update login status in the employees table
                    String updateStatusQuery = "UPDATE employees SET login_status = 'logged_in' WHERE employee_id = ?";
                    PreparedStatement updatePst = con.prepareStatement(updateStatusQuery);
                    updatePst.setInt(1, employeeId);
                    updatePst.executeUpdate();

                    response.sendRedirect("employee_dashboard.html");
                }
            } else {
                response.getWriter().println("Invalid credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

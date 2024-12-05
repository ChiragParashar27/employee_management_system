import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Fetch session if it exists

        if (session != null) {
            // Retrieve the employee_id from the session
            Integer employeeId = (Integer) session.getAttribute("employee_id");

            if (employeeId != null) {
                // Update employee status in the database
                try {
                    // Connect to the database
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");

                    // SQL query to update the employee's status to 'logged_out'
                    String updateStatusQuery = "UPDATE employees set login_status = 'logged_out' WHERE employee_id = ?";
                    PreparedStatement pst = con.prepareStatement(updateStatusQuery);
                    pst.setInt(1, employeeId);
                    pst.executeUpdate();

                    // Close the database connection
                    pst.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Invalidate the session to log out the employee
            session.invalidate();
        }

        // Redirect to the login page after logout
        response.sendRedirect("login.html");
    }
}

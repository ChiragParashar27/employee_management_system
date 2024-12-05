import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;
@WebServlet("/EmployeeDashboardServlet")
public class EmployeeDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String username = (String) session.getAttribute("username");

        if (username != null) {
            try {
                // Connect to the database
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");
                
                // Query to get assigned work and salary details
                String query = "SELECT task_description, due_date FROM assigned_work aw JOIN login l ON aw.employee_id = l.employee_id WHERE l.username = ?";
                PreparedStatement pst = con.prepareStatement(query);
                pst.setString(1, username);
                
                ResultSet rs = pst.executeQuery();
                
                PrintWriter out = response.getWriter();
                out.println("<h2>Your Assigned Work</h2>");
                while (rs.next()) {
                    out.println("<p>Task: " + rs.getString("task_description") + " | Due Date: " + rs.getString("due_date") + "</p>");
                }
                
                query = "SELECT total_salary, payment_date FROM salary s JOIN login l ON s.employee_id = l.employee_id WHERE l.username = ?";
                pst = con.prepareStatement(query);
                pst.setString(1, username);
                
                rs = pst.executeQuery();
                
                out.println("<h2>Your Salary Details</h2>");
                while (rs.next()) {
                    out.println("<p>Total Salary: " + rs.getString("total_salary") + " | Payment Date: " + rs.getString("payment_date") + "</p>");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            response.sendRedirect("login.html");
        }
    }
}

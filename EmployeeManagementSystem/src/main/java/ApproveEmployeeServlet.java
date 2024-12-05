import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/ApproveEmployeeServlet")
public class ApproveEmployeeServlet extends HttpServlet {
    
    // Handles fetching unapproved employees
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");

            // Query to fetch employees waiting for approval
            String fetchPendingEmployeesQuery = "SELECT employee_id, first_name, last_name, status FROM employees WHERE status != 'approved'";
            PreparedStatement pst = con.prepareStatement(fetchPendingEmployeesQuery);
            ResultSet rs = pst.executeQuery();

            // Build the table rows to be sent back as a response
            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String status = rs.getString("status");

                // Create a row for each unapproved employee
                out.println("<tr>");
                out.println("<td>" + employeeId + "</td>");
                out.println("<td>" + firstName + "</td>");
                out.println("<td>" + lastName + "</td>");
                out.println("<td>" + status + "</td>");
                out.println("<td><input type='radio' name='employee_id' value='" + employeeId + "' required></td>");
                out.println("</tr>");
            }

            rs.close();
            pst.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            out.println("Error: " + e.getMessage());
        } finally {
            out.close();
        }
    }

    // Handles approving the selected employee
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get data from the form
        int employeeId = Integer.parseInt(request.getParameter("employee_id"));
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");

            // Update employee status to 'approved'
            String updateStatusQuery = "UPDATE employees SET status = 'approved' WHERE employee_id = ?";
            PreparedStatement pst = con.prepareStatement(updateStatusQuery);
            pst.setInt(1, employeeId);
            pst.executeUpdate();

            // Insert login credentials into the login table
            String insertLoginQuery = "INSERT INTO login (username, password, role, employee_id) VALUES (?, ?, 'employee', ?)";
            pst = con.prepareStatement(insertLoginQuery);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setInt(3, employeeId);
            pst.executeUpdate();

            // Send a response back to the HR
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("Employee with ID " + employeeId + " approved and login credentials created.");
            out.close();

            pst.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}

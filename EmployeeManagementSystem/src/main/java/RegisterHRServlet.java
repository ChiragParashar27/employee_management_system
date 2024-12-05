import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/RegisterHRServlet")
public class RegisterHRServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String department = request.getParameter("department");
        String password = request.getParameter("password"); // Plaintext password

        Connection con = null;
        PreparedStatement pst = null;

        try {
            // Connect to the database
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");

            // Insert HR data into the hr table
            String query = "INSERT INTO hr (first_name, last_name, email, phone, department, password) VALUES (?, ?, ?, ?, ?, ?)";
            pst = con.prepareStatement(query);
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, email);
            pst.setString(4, phone);
            pst.setString(5, department);
            pst.setString(6, password); // Store plaintext password

            // Execute the update
            int rowCount = pst.executeUpdate();

            PrintWriter out = response.getWriter();
            if (rowCount > 0) {
                out.println("HR registered successfully!");
            } else {
                out.println("HR registration failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Database error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        } finally {
            // Close resources
            try {
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

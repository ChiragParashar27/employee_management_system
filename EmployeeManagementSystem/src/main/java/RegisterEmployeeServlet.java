import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/RegisterEmployeeServlet")
public class RegisterEmployeeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("register".equals(action)) {
            registerEmployee(request, response);
        } else if ("checkStatus".equals(action)) {
            checkRegistrationStatus(request, response);
        }
    }

    private void registerEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String department = request.getParameter("department");
        String position = request.getParameter("position");

        //String username = email; // Using email as username
        //String password = "default_password"; // Default password

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@")) {
            String query = "INSERT INTO employees (first_name, last_name, email, phone, department, position, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')";
            PreparedStatement pst = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            // Insert into the employees table
            pst.setString(1, firstName);
            pst.setString(2, lastName);
            pst.setString(3, email);
            pst.setString(4, phone);
            pst.setString(5, department);
            pst.setString(6, position);

            int rowCount = pst.executeUpdate();

            if (rowCount > 0) {
                // Retrieve the generated employee ID
                ResultSet generatedKeys = pst.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int employeeId = generatedKeys.getInt(1);

                    // Insert into the login table
                    //String loginQuery = "INSERT INTO login (username, password, role, employee_id) VALUES (?, ?, 'employee', ?)";
                    //PreparedStatement pstLogin = con.prepareStatement(loginQuery);
                    //pstLogin.setString(1, username);
                    //pstLogin.setString(2, password);
                    //pstLogin.setInt(3, employeeId);
                    //pstLogin.executeUpdate();

                    response.getWriter().write("Registration successful! Await HR approval.");
                }
            } else {
                response.getWriter().write("Registration failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Error: " + e.getMessage());
        }
    }

    private void checkRegistrationStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@")) {
            // Check the status of the employee
            String query = "SELECT status FROM employees WHERE email = ?";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, email);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                if ("approved".equals(status)) {
                    // If approved, fetch the username and password
                    String loginQuery = "SELECT username, password FROM login WHERE employee_id = (SELECT employee_id FROM employees WHERE email = ?)";
                    PreparedStatement pstLogin = con.prepareStatement(loginQuery);
                    pstLogin.setString(1, email);
                    ResultSet rsLogin = pstLogin.executeQuery();

                    if (rsLogin.next()) {
                        String username = rsLogin.getString("username");
                        String password = rsLogin.getString("password");

                        // Respond with the username and password
                        response.getWriter().write("Status: " + status + ". Username: " + username + ", Password: " + password);
                    } else {
                        response.getWriter().write("Status: " + status + ". But login credentials are not available.");
                    }
                } else {
                    response.getWriter().write("Status: " + status + ". Await HR approval.");
                }
            } else {
                response.getWriter().write("Email not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Error: " + e.getMessage());
        }
    }
}

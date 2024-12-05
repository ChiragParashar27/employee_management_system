import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/LoggedInEmployeesServlet")
public class LoggedInEmployeesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");

            String query = "SELECT employee_id, first_name, last_name, login_status FROM employees WHERE login_status = 'logged_in'";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            ArrayList<String> employees = new ArrayList<>();
            out.println("["); // Start of JSON array

            while (rs.next()) {
                String employee = String.format(
                        "{\"employee_id\": %d, \"first_name\": \"%s\", \"last_name\": \"%s\", \"login_status\": \"%s\"}",
                        rs.getInt("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("login_status")
                );
                employees.add(employee);
            }

            out.println(String.join(",", employees)); // Join the JSON objects with commas
            out.println("]"); // End of JSON array

            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

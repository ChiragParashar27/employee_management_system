import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/EmployeeListServlet")
public class EmployeeListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        StringBuilder json = new StringBuilder();
        json.append("["); // Start of JSON array
        
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");
            String query = "SELECT employee_id, first_name, last_name, email, department FROM employees";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    json.append(","); // Add comma before next object if it's not the first
                }
                first = false;
                
                // Build JSON object string manually
                json.append("{");
                json.append("\"employee_id\":").append(rs.getInt("employee_id")).append(",");
                json.append("\"first_name\":\"").append(rs.getString("first_name")).append("\",");
                json.append("\"last_name\":\"").append(rs.getString("last_name")).append("\",");
                json.append("\"email\":\"").append(rs.getString("email")).append("\",");
                json.append("\"department\":\"").append(rs.getString("department")).append("\"");
                json.append("}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        json.append("]"); // End of JSON array
        out.print(json.toString());
        out.close();
    }
}

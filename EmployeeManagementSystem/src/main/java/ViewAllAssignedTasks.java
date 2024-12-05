import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet("/ViewAllAssignedTasks")
public class ViewAllAssignedTasks extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        List<Map<String, Object>> tasks = new ArrayList<>();

        try {
            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");

            // SQL query to fetch all assigned tasks
            String query = "SELECT e.employee_id, e.first_name, e.last_name, aw.work_id, aw.task_description, aw.update_status " +
                           "FROM assigned_work aw JOIN employees e ON aw.employee_id = e.employee_id";
            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Loop through the result set and build a list of task details
            while (rs.next()) {
                Map<String, Object> task = new HashMap<>();
                task.put("employeeId", rs.getInt("employee_id"));
                task.put("employeeName", rs.getString("first_name") + " " + rs.getString("last_name"));
                task.put("taskId", rs.getInt("work_id"));
                task.put("taskDescription", rs.getString("task_description"));
                task.put("status", rs.getString("update_status"));
                tasks.add(task);
            }

            // Create a JSON response
            StringBuilder jsonResponse = new StringBuilder("[");
            for (int i = 0; i < tasks.size(); i++) {
                Map<String, Object> task = tasks.get(i);
                jsonResponse.append("{");
                jsonResponse.append("\"employeeId\":").append(task.get("employeeId")).append(",");
                jsonResponse.append("\"employeeName\":\"").append(task.get("employeeName")).append("\",");
                jsonResponse.append("\"taskId\":").append(task.get("taskId")).append(",");
                jsonResponse.append("\"taskDescription\":\"").append(task.get("taskDescription")).append("\",");
                jsonResponse.append("\"status\":\"").append(task.get("status")).append("\"");
                jsonResponse.append("}");
                if (i < tasks.size() - 1) {
                    jsonResponse.append(",");
                }
            }
            jsonResponse.append("]");
            out.print(jsonResponse.toString()); // Send the JSON response
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error: " + e.getMessage());
        } finally {
            out.close();
        }
    }
}

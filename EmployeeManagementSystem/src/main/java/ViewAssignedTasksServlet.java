import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/ViewTasksEmp")
public class ViewAssignedTasksServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username"); // Assuming username is stored in session
        
        try {
            // Connect to the database
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@");
            
            // Query to get assigned tasks for the logged-in employee
            String query = "SELECT work_id, task_description, due_date, status FROM assigned_work WHERE employee_id = (SELECT employee_id FROM login WHERE username = ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username); // Assuming username is linked to employee_id
            
            ResultSet rs = pst.executeQuery();
            
            PrintWriter out = response.getWriter();
            while (rs.next()) {
                int taskId = rs.getInt("work_id");
                String taskDescription = rs.getString("task_description");
                Date dueDate = rs.getDate("due_date");
                String status = rs.getString("status");

                out.println("<tr>");
                out.println("<td>" + taskId + "</td>");
                out.println("<td>" + taskDescription + "</td>");
                out.println("<td>" + dueDate + "</td>");
                out.println("<td>" + status + "</td>");
                out.println("<td>");
                out.println("<select id='status_" + taskId + "'>");
                out.println("<option value='pending'>Pending</option>");
                out.println("<option value='in_progress'>In Progress</option>");
                out.println("<option value='completed'>Completed</option>");
                out.println("</select>");
                out.println("<button onclick='updateStatus(" + taskId + ")'>Update</button>");
                out.println("</td>");
                out.println("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.*;
import java.sql.*;

@WebServlet("/AssignTaskServlet")
public class AssignTaskServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String employeeIdStr = request.getParameter("employee_id");
        String taskDescription = request.getParameter("task_description");
        String dueDate = request.getParameter("due_date");

        if (employeeIdStr == null || employeeIdStr.isEmpty()) {
            response.getWriter().println("Employee ID is missing.");
            return;
        }

        try {
            int employeeId = Integer.parseInt(employeeIdStr);

            // Connect to the database
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/employee_management", "root", "chir@g2710@")) {
                String insertQuery = "INSERT INTO assigned_work (employee_id, task_description, due_date) VALUES (?, ?,?)";
                try (PreparedStatement pst = con.prepareStatement(insertQuery)) {
                    pst.setString(1, employeeIdStr);
                    pst.setString(2, taskDescription);
                   pst.setDate(3, Date.valueOf(dueDate));

                    int rowCount = pst.executeUpdate();
                    if (rowCount > 0) {
                        response.getWriter().println("Task assigned successfully.");
                    } else {
                        response.getWriter().println("Failed to assign task.");
                    }
                }
            }
        } catch (NumberFormatException e) {
            response.getWriter().println("Invalid employee ID.");
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Database error occurred. Please try again later.");
        }
    }
}

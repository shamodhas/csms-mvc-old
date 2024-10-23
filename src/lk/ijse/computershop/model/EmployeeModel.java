package lk.ijse.computershop.model;

import lk.ijse.computershop.to.Employee;
import lk.ijse.computershop.util.CrudUtil;
import lk.ijse.computershop.util.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EmployeeModel {
    public static ArrayList<Employee> getAllEmployee() throws SQLException, ClassNotFoundException {
        ArrayList<Employee>allEmployeeList=new ArrayList<>();
        ResultSet rst=CrudUtil.execute("SELECT *FROM employee");
        while (rst.next()){
            allEmployeeList.add(new Employee(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getString(6)
            ));
        }
        return allEmployeeList;
    }

    public static boolean deleteEmployee(String employeeId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM employee WHERE employeeId=?",employeeId);
    }

    public static String getNextEmployeeId() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT employeeId FROM employee ORDER BY employeeId DESC LIMIT 1");
        if(rst.next()){
            return Project.generateNextId("E",rst.getString(1));
        }
        return Project.generateNextId("E",null);
    }

    public static String existsEmployee(String nic) throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT employeeId FROM employee WHERE nic=?",nic);
        if (rst.next()){
            return rst.getString(1);
        }
        return null;
    }

    public static boolean addEmployee(Employee employee) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO employee VALUES(?,?,?,?,?,?)",
                employee.getEmployeeId(),
                employee.getName(),
                employee.getAddress(),
                employee.getNic(),
                employee.getTelephoneNumber(),
                employee.getRank()
        );
    }

    public static boolean updateEmployee(Employee employee) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE employee set name=?, address=?, nic=?, telephoneNumber=?, `rank`=? where employeeId=?",
                employee.getName(),
                employee.getAddress(),
                employee.getNic(),
                employee.getTelephoneNumber(),
                employee.getRank(),
                employee.getEmployeeId()
        );
    }

    public static boolean clearAll() throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("delete from employee");
    }
}

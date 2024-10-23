package lk.ijse.computershop.model;

import lk.ijse.computershop.to.Repair;
import lk.ijse.computershop.to.RepairTransactionDetails;
import lk.ijse.computershop.util.CrudUtil;
import lk.ijse.computershop.util.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RepairModel {
    public static String getNextRepairId() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT repairId FROM repair ORDER BY repairId DESC LIMIT 1");
        if(rst.next()){
            return Project.generateNextId("R",rst.getString(1));
        }
        return Project.generateNextId("R",null);
    }
    public static int getNonReturnRepairCount() throws SQLException, ClassNotFoundException {
        ArrayList<Repair>nonReturnRepairList=getAllNonReturnRepair();
        return nonReturnRepairList.size();
    }

    public static ArrayList<Repair> getAllNonReturnRepair() throws SQLException, ClassNotFoundException {
        ArrayList<Repair>nonReturnRepairList=new ArrayList<>();
        ResultSet rst= CrudUtil.execute("SELECT *FROM repair WHERE state=? || state=?" ,"repaired","repairing");
        while (rst.next()){
            nonReturnRepairList.add(new Repair(
                    rst.getString(1),
                    LocalDate.parse(rst.getString(2)),
                    LocalDate.parse(rst.getString(3)),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getString(6)
            ));
        }
        return nonReturnRepairList;
    }

    public static ArrayList<Repair> getAllReturnedRepair() throws SQLException, ClassNotFoundException {
        ArrayList<Repair>returnedReturnRepairList=new ArrayList<>();
        ResultSet rst= CrudUtil.execute("SELECT *FROM repair WHERE state=?","returned");
        while (rst.next()){
            returnedReturnRepairList.add(new Repair(
                    rst.getString(1),
                    LocalDate.parse(rst.getString(2)),
                    LocalDate.parse(rst.getString(3)),
                    rst.getString(4),
                    rst.getString(5),
                    rst.getString(6)
            ));
        }
        return returnedReturnRepairList;
    }

    public static boolean deleteRepair(String repairId) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM repair WHERE repairId=?",repairId);
    }

    public static boolean addRepair(Repair repair) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO repair VALUES(?,?,?,?,?,?)",
                repair.getRepairId(),
                repair.getReceiveDate(),
                repair.getReturnDate(),
                repair.getState(),
                repair.getDescription(),
                repair.getCustomerId()
        );
    }

    public static boolean updateRepair(Repair repair) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE repair set receiveDate=?, returnDate=?, state=?, description=?, customerId=? where repairId=?",
                repair.getReceiveDate(),
                repair.getReturnDate(),
                repair.getState(),
                repair.getDescription(),
                repair.getCustomerId(),
                repair.getRepairId()
        );
    }

    public static String searchRepairId(String repairId) throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT description FROM repair WHERE repairId=?",repairId);
        if (rst.next()){
            return rst.getString(1);
        }
        return null;
    }

    public static boolean updateRepairForTransaction(ArrayList<RepairTransactionDetails> repairTransactionDetailsArrayList) throws SQLException, ClassNotFoundException {
        for (RepairTransactionDetails details:repairTransactionDetailsArrayList) {
            boolean isAdded=CrudUtil.execute("UPDATE repair set state=? where repairId=?",
                    "returned",
                    details.getRepairId());
            if (!isAdded)
                return false;
        }
        return true;
    }
}

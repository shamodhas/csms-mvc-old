package lk.ijse.computershop.model;

import lk.ijse.computershop.db.DBConnection;
import lk.ijse.computershop.to.Transaction;
import lk.ijse.computershop.util.CrudUtil;
import lk.ijse.computershop.util.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class TransactionModel {
    public static String getNextTransactionId() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT transactionId FROM transaction ORDER BY transactionId DESC LIMIT 1");
        if(rst.next()){
            return Project.generateNextId("T",rst.getString(1));
        }
        return Project.generateNextId("T",null);
    }
    public static int getAllTransactionCount() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT *FROM transaction");
        int i=0;
        while (rst.next()){
            i++;
        }
        return i;
    }

    public static double getTodayAllIncome() throws SQLException, ClassNotFoundException {
        ArrayList<Transaction>toDayAllTransaction= searchDate(LocalDate.now());
        double total=0;
        for (Transaction transaction:toDayAllTransaction) {
            total+=transaction.getTotal();
        }
        return total;
    }

    private static ArrayList<Transaction> searchDate(LocalDate date) throws SQLException, ClassNotFoundException {
        ArrayList<Transaction>toDayAllTransaction=new ArrayList<>();
        ResultSet rst=CrudUtil.execute("SELECT *FROM transaction WHERE dateTime LIKE ?",date+"%");
        while (rst.next()){
            toDayAllTransaction.add(new Transaction(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getDouble(5)
            ));
        }
        return toDayAllTransaction;
    }

    public static int searchDateTransactionCount(LocalDate date) throws SQLException, ClassNotFoundException {
        ArrayList<Transaction>toDayAllTransaction= searchDate(date);
        return toDayAllTransaction.size();
    }

    public static Transaction searchCustomer(String customerId) throws SQLException, ClassNotFoundException {
        ResultSet rst=CrudUtil.execute("SELECT *FROM transaction WHERE customerId=?",customerId);
        if (rst.next()){
            return new Transaction(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getDouble(5)
            );
        }
        return null;
    }

    public static LocalDate getDate(String transactionId) throws SQLException, ClassNotFoundException {
        ResultSet rst=CrudUtil.execute("SELECT dateTime FROM transaction WHERE transactionId=?",transactionId);
        if (rst.next()){
            return LocalDate.parse(rst.getString(1).substring(0,10));
        }
        return null;
    }

    public static boolean addTransaction(Transaction transaction) throws SQLException, ClassNotFoundException {
        try{
            DBConnection.getInstance().getConnection().setAutoCommit(false);
            if (CrudUtil.execute("INSERT INTO transaction VALUES(?,?,?,?,?)",transaction.getTransactionId(),transaction.getDateTime(),transaction.getCustomerId(),transaction.getType(),transaction.getTotal())){
                if(RepairModel.updateRepairForTransaction(transaction.getRepairTransactionDetailsArrayList())) {
                    if (RepairTransactionDetailsModel.addDetails(transaction.getRepairTransactionDetailsArrayList())) {
                        if (ItemTransactionDetailsModel.addDetails(transaction.getItemTransactionDetailsArrayList())){
                            if (ItemModel.updateSellingItem((transaction.getItemTransactionDetailsArrayList()))) {
                                DBConnection.getInstance().getConnection().commit();
                                return true;
                            }
                        }
                    }
                }
            }
            DBConnection.getInstance().getConnection().rollback();
            return false;
        }finally {
            DBConnection.getInstance().getConnection().setAutoCommit(true);
            System.out.println("Auto Commit "+DBConnection.getInstance().getConnection().getAutoCommit());
        }
    }

    public static ArrayList<Transaction> getAllTransaction() throws SQLException, ClassNotFoundException {
        ResultSet rst=CrudUtil.execute("SELECT *FROM transaction");
        ArrayList<Transaction>transactionArrayList=new ArrayList<>();
        while (rst.next()){
            transactionArrayList.add(new Transaction(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getString(4),
                    rst.getDouble(5)
            ));
        }
        return transactionArrayList;
    }
}

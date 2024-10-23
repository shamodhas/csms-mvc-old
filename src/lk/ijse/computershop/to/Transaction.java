package lk.ijse.computershop.to;
import java.util.ArrayList;

public class Transaction {
    private String transactionId;

    private String dateTime;

    private String customerId;
    private String type;
    private double total;
    private ArrayList<ItemTransactionDetails>itemTransactionDetailsArrayList;
    private ArrayList<RepairTransactionDetails>repairTransactionDetailsArrayList;
    public Transaction() {
    }

    public Transaction(String transactionId, String dateTime, String customerId, String type, double total) {
        this.transactionId = transactionId;
        this.dateTime = dateTime;
        this.customerId = customerId;
        this.type = type;
        this.total = total;
    }

    public Transaction(String transactionId, String dateTime, String customerId, String type, double total, ArrayList<ItemTransactionDetails> itemTransactionDetailsArrayList, ArrayList<RepairTransactionDetails> repairTransactionDetailsArrayList) {
        this.transactionId = transactionId;
        this.dateTime = dateTime;
        this.customerId = customerId;
        this.type = type;
        this.total = total;
        this.itemTransactionDetailsArrayList = itemTransactionDetailsArrayList;
        this.repairTransactionDetailsArrayList = repairTransactionDetailsArrayList;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<ItemTransactionDetails> getItemTransactionDetailsArrayList() {
        return itemTransactionDetailsArrayList;
    }

    public void setItemTransactionDetailsArrayList(ArrayList<ItemTransactionDetails> itemTransactionDetailsArrayList) {
        this.itemTransactionDetailsArrayList = itemTransactionDetailsArrayList;
    }

    public ArrayList<RepairTransactionDetails> getRepairTransactionDetailsArrayList() {
        return repairTransactionDetailsArrayList;
    }

    public void setRepairTransactionDetailsArrayList(ArrayList<RepairTransactionDetails> repairTransactionDetailsArrayList) {
        this.repairTransactionDetailsArrayList = repairTransactionDetailsArrayList;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}

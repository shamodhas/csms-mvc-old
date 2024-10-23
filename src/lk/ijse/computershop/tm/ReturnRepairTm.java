package lk.ijse.computershop.tm;

import java.time.LocalDate;

public class ReturnRepairTm {
    private String repairId;
    private String transactionId;
    private String description;
    private String customerId;
    private double price;
    private LocalDate receiveDate;
    private LocalDate trueReturnDate;

    public ReturnRepairTm() {
    }

    public ReturnRepairTm(String repairId, String transactionId, String description, String customerId, double price, LocalDate receiveDate, LocalDate trueReturnDate) {
        this.repairId = repairId;
        this.transactionId = transactionId;
        this.description = description;
        this.customerId = customerId;
        this.price = price;
        this.receiveDate = receiveDate;
        this.trueReturnDate = trueReturnDate;
    }

    public String getRepairId() {
        return repairId;
    }

    public void setRepairId(String repairId) {
        this.repairId = repairId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(LocalDate receiveDate) {
        this.receiveDate = receiveDate;
    }

    public LocalDate getTrueReturnDate() {
        return trueReturnDate;
    }

    public void setTrueReturnDate(LocalDate trueReturnDate) {
        this.trueReturnDate = trueReturnDate;
    }
}

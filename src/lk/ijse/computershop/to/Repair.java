package lk.ijse.computershop.to;

import java.time.LocalDate;

public class Repair {
    private String repairId;
    private LocalDate receiveDate;
    private LocalDate returnDate;
    private String state;
    private String description;
    private String customerId;

    public Repair() {
    }

    public Repair(String repairId, LocalDate receiveDate, LocalDate returnDate, String state, String description, String customerId) {
        this.repairId = repairId;
        this.receiveDate = receiveDate;
        this.returnDate = returnDate;
        this.state = state;
        this.description = description;
        this.customerId = customerId;
    }

    public String getRepairId() {
        return repairId;
    }

    public void setRepairId(String repairId) {
        this.repairId = repairId;
    }

    public LocalDate getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(LocalDate receiveDate) {
        this.receiveDate = receiveDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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
}

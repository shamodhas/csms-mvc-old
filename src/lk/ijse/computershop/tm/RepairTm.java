package lk.ijse.computershop.tm;

public class RepairTm {
    private String repairId;
    private String description;
    private double repairPrice;

    public RepairTm() {
    }

    public RepairTm(String repairId, String description, double repairPrice) {
        this.repairId = repairId;
        this.description = description;
        this.repairPrice = repairPrice;
    }

    public String getRepairId() {
        return repairId;
    }

    public void setRepairId(String repairId) {
        this.repairId = repairId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRepairPrice() {
        return repairPrice;
    }

    public void setRepairPrice(double repairPrice) {
        this.repairPrice = repairPrice;
    }
}

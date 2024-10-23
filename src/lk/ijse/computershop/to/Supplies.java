package lk.ijse.computershop.to;


import java.util.ArrayList;

public class Supplies {
    private String suppliesId;
    private String dateTime;
    private String supplierId;
    private ArrayList<SuppliesDetails> suppliesDetailsArrayList;

    public Supplies() {
    }

    public Supplies(String suppliesId, String dateTime, String supplierId, ArrayList<SuppliesDetails> suppliesDetailsArrayList) {
        this.suppliesId = suppliesId;
        this.dateTime = dateTime;
        this.supplierId = supplierId;
        this.suppliesDetailsArrayList = suppliesDetailsArrayList;
    }

    public Supplies(String suppliesId, String dateTime, String supplierId) {
        this.suppliesId = suppliesId;
        this.dateTime = dateTime;
        this.supplierId = supplierId;
    }

    public String getSuppliesId() {
        return suppliesId;
    }

    public void setSuppliesId(String suppliesId) {
        this.suppliesId = suppliesId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public ArrayList<SuppliesDetails> getSuppliesDetailsArrayList() {
        return suppliesDetailsArrayList;
    }

    public void setSuppliesDetailsArrayList(ArrayList<SuppliesDetails> suppliesDetailsArrayList) {
        this.suppliesDetailsArrayList = suppliesDetailsArrayList;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateTime() {
        return dateTime;
    }
}

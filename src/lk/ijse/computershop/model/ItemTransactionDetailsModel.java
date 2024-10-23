package lk.ijse.computershop.model;

import lk.ijse.computershop.to.ItemTransactionDetails;
import lk.ijse.computershop.util.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemTransactionDetailsModel {
    public static boolean addDetails(ArrayList<ItemTransactionDetails> itemTransactionDetailsArrayList) throws SQLException, ClassNotFoundException {
        for (ItemTransactionDetails details:itemTransactionDetailsArrayList) {
            boolean isAdded= CrudUtil.execute("INSERT INTO itemtransactiondetails VALUES(?,?,?,?)",
                    details.getTransactionId(),
                    details.getItemCode(),
                    details.getQuantity(),
                    details.getUnitPrice()
            );
            if(!isAdded)
                return false;
        }
        return true;
    }

    public static ArrayList<ItemTransactionDetails> search(String transactionId) throws SQLException, ClassNotFoundException {
        ArrayList<ItemTransactionDetails>details=new ArrayList<>();
        ResultSet rst=CrudUtil.execute("SELECT * FROM itemtransactiondetails WHERE transactionId=?",transactionId);
        while (rst.next()){
            details.add(new ItemTransactionDetails(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getInt(3),
                    rst.getDouble(4)
            ));
        }
        return details;
    }
}

package lk.ijse.computershop.model;

import lk.ijse.computershop.to.Item;
import lk.ijse.computershop.to.ItemTransactionDetails;
import lk.ijse.computershop.to.SuppliesDetails;
import lk.ijse.computershop.util.CrudUtil;
import lk.ijse.computershop.util.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ItemModel {
    public static ArrayList<Item> getAllItem() throws SQLException, ClassNotFoundException {
        ArrayList<Item>itemArrayList=new ArrayList<>();
        ResultSet rst= CrudUtil.execute("SELECT *FROM item");
        while (rst.next()){
            itemArrayList.add(new Item(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5)
            ));
        }
        return itemArrayList;
    }

    public static boolean deleteItem(String itemCode) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("DELETE FROM item WHERE itemCode=?",itemCode);
    }

    public static String getNextItemCode() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT itemCode FROM item ORDER BY itemCode DESC LIMIT 1");
        if(rst.next()){
            return Project.generateNextId("I",rst.getString(1));
        }
        return Project.generateNextId("I",null);
    }

    public static String searchDescriptionItem(String description) throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT itemCode FROM item WHERE description=?",description);
        if (rst.next()){
            return rst.getString(1);
        }
        return null;
    }

    public static boolean addItem(Item item) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO item VALUES(?,?,?,?,?)",
                item.getItemCode(),
                item.getItemType(),
                item.getDescription(),
                item.getUnitPrice(),
                item.getQtyOnStock()
        );
    }

    public static boolean updateItem(Item item) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE item set itemType=?, description=?, unitPrice=?, qtyOnStock=? WHERE itemCode=?",
                item.getItemType(),
                item.getDescription(),
                item.getUnitPrice(),
                item.getQtyOnStock(),
                item.getItemCode()
        );
    }

    public static String searchItemCode(String itemCode) throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT description FROM item WHERE itemCode=?",itemCode);
        if (rst.next()){
            return rst.getString(1);
        }
        return null;
    }

    public static boolean updateQty(String itemCode, int qty) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("UPDATE Item SET qtyOnStock = qtyOnStock - ? WHERE itemCode = ?",qty,itemCode);
    }

    public static boolean addQty(String itemCode, int addingQty) throws SQLException, ClassNotFoundException {
        return updateQty(itemCode,-(addingQty));
    }
    public static boolean reducesQty(String itemCode, int addingQty) throws SQLException, ClassNotFoundException {
        return updateQty(itemCode,addingQty);
    }

    public static int getQty(String itemCode) throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT qtyOnStock FROM item WHERE itemCode=?",itemCode);
        if (rst.next()){
            return rst.getInt(1);
        }
        return -1;
    }

    public static Item search(String search) throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT *FROM item WHERE itemCode=?",search);
        if (rst.next()){
            return new Item(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5)
            );
        }
        rst= CrudUtil.execute("SELECT *FROM item WHERE description=?",search);
        if (rst.next()){
            return new Item(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getDouble(4),
                    rst.getInt(5)
            );
        }

        return new Item();
    }

    public static boolean updateSuppliedItem(ArrayList<SuppliesDetails> suppliesDetailsArrayList) throws SQLException, ClassNotFoundException {
        for (SuppliesDetails details:suppliesDetailsArrayList) {
            boolean isAdded=addQty(details.getItemCode(),details.getQuantity());
            if (!isAdded)
                return false;
        }
        return true;
    }
    public static boolean updateSellingItem(ArrayList<ItemTransactionDetails> itemTransactionDetailsArrayList) throws SQLException, ClassNotFoundException {
        for (ItemTransactionDetails details:itemTransactionDetailsArrayList) {
            boolean isAdded=reducesQty(details.getItemCode(),details.getQuantity());
            if (!isAdded)
                return false;
        }
        return true;
    }

    public static boolean clearAll() throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("delete from item");
    }
}

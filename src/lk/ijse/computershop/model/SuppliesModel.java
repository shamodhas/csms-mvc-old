package lk.ijse.computershop.model;

import lk.ijse.computershop.db.DBConnection;
import lk.ijse.computershop.to.Supplies;
import lk.ijse.computershop.util.CrudUtil;
import lk.ijse.computershop.util.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SuppliesModel {
    public static String getNextSuppliesId() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT suppliesId FROM supplies ORDER BY suppliesId DESC LIMIT 1");
        if(rst.next()){
            return Project.generateNextId("G",rst.getString(1));
        }
        return Project.generateNextId("G",null);
    }

    public static boolean addSupplies(Supplies supplies) throws SQLException, ClassNotFoundException {
        try{
            DBConnection.getInstance().getConnection().setAutoCommit(false);
            if (CrudUtil.execute("INSERT INTO supplies VALUES(?,?,?)",supplies.getSuppliesId(),supplies.getDateTime(),supplies.getSupplierId())){
                if (SuppliesDetailsModel.addDetails(supplies.getSuppliesDetailsArrayList())){
                    if (ItemModel.updateSuppliedItem(supplies.getSuppliesDetailsArrayList())){
                        DBConnection.getInstance().getConnection().commit();
                        return true;
                    }
                }
            }
            DBConnection.getInstance().getConnection().rollback();
            return false;
        }finally {
            DBConnection.getInstance().getConnection().setAutoCommit(true);
        }
    }

    public static ArrayList<Supplies> getAllSupplies() throws SQLException, ClassNotFoundException {
        ArrayList<Supplies>suppliesArrayList=new ArrayList<>();
        ResultSet rst = CrudUtil.execute("SELECT *FROM supplies");
        while (rst.next()){
            suppliesArrayList.add(new Supplies(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3)
            ));
        }
        return suppliesArrayList;
    }
}

package lk.ijse.computershop.model;

import lk.ijse.computershop.to.User;
import lk.ijse.computershop.util.CrudUtil;

import java.sql.SQLException;

public class AdminModel {
    public static boolean resetAll() throws SQLException, ClassNotFoundException {
        try {
            CrudUtil.Transaction(true);
            if (CustomerModel.clearAll()) {
                if (EmployeeModel.clearAll()) {
                    if (ItemModel.clearAll()) {
                        if (SupplierModel.clearAll()) {
                            if (UserModel.clearAll()) {
                                User user = new User("U001", "Admin", "Admin@11", null, null, null, "Admin");
                                if (UserModel.addUser(user)) {
                                    CrudUtil.save();
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
            CrudUtil.clear();
            return false;
        }finally {
            CrudUtil.Transaction(false);
        }
    }
}

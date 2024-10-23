package lk.ijse.computershop.model;

import lk.ijse.computershop.to.LoginRecord;
import lk.ijse.computershop.util.CrudUtil;
import lk.ijse.computershop.util.Project;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LoginRecordModel {
    public static String getNextLoginId() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT loginId FROM loginrecord ORDER BY loginId DESC LIMIT 1");
        if(rst.next()){
            return Project.generateNextId("L",rst.getString(1));
        }
        return Project.generateNextId("L",null);
    }


    public static boolean saveLogin(LoginRecord loginRecord) throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("INSERT INTO loginrecord VALUES(?, ?, ?)",
                loginRecord.getLoginId(),
                loginRecord.getDateTime(),
                loginRecord.getUserId()
        );
    }
    public static ArrayList<LoginRecord> getAllLogin() throws SQLException, ClassNotFoundException {
        ResultSet rst= CrudUtil.execute("SELECT * FROM loginrecord");
        ArrayList<LoginRecord>loginRecordArrayList=new ArrayList<>();
        while (rst.next()){
            loginRecordArrayList.add(new LoginRecord(
                    rst.getString(1),
                    rst.getTimestamp(2).toLocalDateTime(),
                    rst.getString(3)
            ));
        }
        return loginRecordArrayList;
    }

    public static boolean clearAllRecord() throws SQLException, ClassNotFoundException {
        return CrudUtil.execute("delete from loginrecord");
    }
}
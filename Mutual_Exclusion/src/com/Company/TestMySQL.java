package com.Company;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class TestMySQL {
    public static void main(String args[]) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/lab", "root", "Eshant@c18");
            Statement st = connect.createStatement();
            ResultSet result = st.executeQuery("select client_no from cm");
            while(result.next()){
                System.out.println(result.getString(1));
            }
            connect.close();

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}



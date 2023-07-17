package com.Company;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class TestMySQL {
    public static void main(String args[]) throws SQLException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection connect_01 = DriverManager.getConnection("jdbc:mysql://localhost:3306/lab", "root", "Eshant@c18");
            Connection connect_02 = DriverManager.getConnection("jdbc:mysql://localhost:3306/ars", "root", "Eshant@c18");
            Statement st_01 = connect_01.createStatement();
            Statement st_02 = connect_02.createStatement();
            ResultSet result_01 = st_01.executeQuery("select client_no from cm");
            ResultSet result_02 = st_02.executeQuery("select passenger_username from passenger");
            while(result_01.next()){
                System.out.println(result_01.getString(1));
            }

            while(result_02.next()){
                System.out.println(result_02.getString(1));
            }

            connect_02.close();
            connect_01.close();

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}



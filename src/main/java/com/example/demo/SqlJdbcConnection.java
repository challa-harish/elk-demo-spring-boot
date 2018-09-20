package com.example.demo;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
 
public class SqlJdbcConnection { 
	
	 public String getVehicleDetails(String vehicleId) throws SQLException {
		 Connection conn = null;
		 try {
	 
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");	
			 conn = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=testdb;user=root;password=password");
			System.out.println("test");
			String sql = "SELECT * FROM vehicle WHERE _vehicle_id_ = ?";
		//	Statement sta = conn.createStatement();
			//String Sql = "select * from employee";
			//ResultSet rs = sta.executeQuery(Sql);
			String vehicleName = null;
			PreparedStatement ps = conn.prepareStatement(sql);
			  ps.setString(1, vehicleId);

			    ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				vehicleName = rs.getString("_vehicle_name_");		
			}
			return vehicleName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			conn.close();
		}
		
	}
}
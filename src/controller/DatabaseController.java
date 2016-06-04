package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DatabaseController
{
	private Connection connection;

	public DatabaseController()
	{

		loadDatabaseDriver();
		connect();
	}

	private void loadDatabaseDriver()
	{
		try
		{
			// Load the JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e)
		{
			// Could not find the database driver
			System.out.println("ClassNotFoundException : " + e.getMessage());
		}
	}

	private void connect()
	{
		try
		{
			// TODO Update connection to exam server on exam day.
			connection = DriverManager.getConnection(
					"jdbc:mysql://databases.aii.avans.nl:3306/mjschink_db?user=mjschink&password=Ab12345");
		} catch (SQLException ex)
		{
			// handle any errors
			System.out.println("Houston, we've had a problem...");
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			JOptionPane.showMessageDialog(null, "U heeft geen internetverbinding.","Wordfeud",JOptionPane.ERROR_MESSAGE);
		}

	}

	public ResultSet query(String query)
	{

		try
		{
			if (connection.isClosed())
			{
				connect();
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		Statement stmt = null;
		try
		{

			stmt = connection.createStatement();
			ResultSet rSet = stmt.executeQuery(query);

			return rSet;
		} catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}

		return null;
	}

	public void queryUpdate(String query)
	{
		Statement stmt = null;
		try
		{
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void closeConnection(){
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean pingedBack(){
		try {
			return connection.isValid(1000);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}

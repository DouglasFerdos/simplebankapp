package com.bank;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DataBaseIntegration {

    public int createAccount(
        Connection conn,
        int accountUserSocialNumber,
        String accountUserFirstName,
        String accountUserLastName,
        LocalDate accountUserBirthdate,
        String accountUserMotherFullName,
        String accountUserAddress,
        BigDecimal accountBalance) {

        // Single statement call, making manually commiting not necessary.
        // Isolation level is TRANSACTION_READ_UNCOMMITTED, the default isolation for postgresql JDBC
        // that works the same as TRANSACTION_READ_COMMITTED because PG does NOT support uncommited reads (dirty reads).
        try (PreparedStatement st = conn.prepareStatement("SELECT create_account ( ?, ?, ?, CAST(? AS DATE), ?, ?, ? )")) {

            // Setting the Statement params
            st.setInt(1, accountUserSocialNumber);
            st.setString(2, accountUserFirstName);
            st.setString(3, accountUserLastName);
            st.setString(4, accountUserBirthdate.toString());
            st.setString(5, accountUserMotherFullName);
            st.setString(6, accountUserAddress);
            st.setBigDecimal(7, accountBalance);

            // Execution the statement and storing the returned value in a result set
            ResultSet rs = st.executeQuery();

            // Reading the returned data stored in the result set
            int accountNumber = 0;
            while(rs.next()){
                accountNumber = rs.getInt(1);
            }

            // Returning the accountNumber generated by postgresql
            return accountNumber;

        } catch (Exception e) {
            // If the autocommit was set to false and an error has occurred it is possible to rollback here.
            // for autocommit(true), the default one if not explicit set to false, the rollback occurs automatically.
            e.printStackTrace();

        } finally {
            // Closes the connection with the database if still exists
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return -1 if the try_catch fails
        return -1;
    }

    public String closeAccount(
            Connection conn,
            int accountNumber,
            int accountUserSocialNumber,
            LocalDate accountUserBirthdate,
            String accountUserMotherFullName
            ) {

        try (PreparedStatement st = conn.prepareStatement("SELECT close_account ( ?, ?, CAST(? AS DATE), ? )")) {

            // Setting the Statement params
            st.setInt(1, accountNumber);
            st.setInt(2, accountUserSocialNumber);
            st.setString(3, accountUserBirthdate.toString());
            st.setString(4, accountUserMotherFullName);

            // Execution the statement and storing the returned value in a result set
            ResultSet rs = st.executeQuery();

            // Reading the returned data stored in the result set
            String confirmation = "";
            while(rs.next()){
                confirmation = rs.getString(1);
            }

            // Returning the confirmation that the account is closed or the balance is not zero
            return confirmation;

        } catch (Exception e) {
            // If the autocommit was set to false and an error has occurred it is possible to rollback here.
            // for autocommit(true), the default one if not explicit set to false, the rollback occurs automatically.
            e.printStackTrace();

        } finally {
            // Closes the connection with the database if still exists
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return String if the try_catch fails
        return "COULD NOT CLOSE THE CLIENT ACCOUNT";
    }

    public Map<String,String> getAccountInfo(
            Connection conn,
            int accountUserSocialNumber,
            LocalDate accountUserBirthdate,
            String accountUserMotherFullName){

        try (PreparedStatement st = conn.prepareStatement("SELECT * FROM get_account_info (?, CAST(? AS DATE), ?)")) {

            // Setting the Statement params
            st.setInt(1, accountUserSocialNumber);
            st.setString(2, accountUserBirthdate.toString());
            st.setString(3, accountUserMotherFullName);

            // Execution the statement and storing the returned value in a result set
            ResultSet rs = st.executeQuery();

            // Getting the returned data
            // Using Map for storing the data
            Map<String, String> accountInfoMap = new HashMap<>();

            // getting the data from the ResultSet into the Map
            while(rs.next()){
                accountInfoMap.put("Account Number", String.valueOf(rs.getInt("account_number")));
                accountInfoMap.put("Social Number", String.valueOf(rs.getInt("account_user_social_number")));
                accountInfoMap.put("First Name", rs.getString("account_user_first_name"));
                accountInfoMap.put("Last Name", rs.getString("account_user_last_name"));
                accountInfoMap.put("Birthdate", rs.getString("account_user_birthdate"));
                accountInfoMap.put("Mother Full Name", rs.getString("account_user_mother_full_name"));
                accountInfoMap.put("Address", rs.getString("account_user_address"));
                accountInfoMap.put("Account Balance", rs.getString("account_balance"));
            }

            // Returns the map with the account info data
            return accountInfoMap;

        } catch (Exception e) {
            // If the autocommit was set to false and an error has occurred it is possible to rollback here.
            // for autocommit(true), the default one if not explicit set to false, the rollback occurs automatically.
            e.printStackTrace();

        } finally {
            // Closes the connection with the database if still exists
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Map for returning an error without breaking the program if the try_catch fails
        Map<String, String> error = new HashMap<>();
        error.put("error","COULD NOT LOCATE THE ACCOUNT INFO");
        return error;
    }

    public String updateAccountInfo(
        Connection conn,
        int accountNumber,
        int accountUserSocialNumber,
        LocalDate accountUserBirthdate,
        String accountUserMotherFullName,
        String accountUserFirstName,
        String accountUserLastName,
        String accountUserAddress) {
        
        try (PreparedStatement st = conn.prepareStatement("SELECT update_user_data (?, ?, CAST(? AS DATE), ?, ?, ?, ?)")){

            // Setting the Statement params
            st.setInt(1, accountNumber);
            st.setInt(2, accountUserSocialNumber);
            st.setString(3,accountUserBirthdate.toString());
            st.setString(4, accountUserMotherFullName);
            st.setString(5, accountUserFirstName);
            st.setString(6, accountUserLastName);
            st.setString(7, accountUserAddress);

            // Execution the statement and storing the returned value in a result set
            ResultSet rs = st.executeQuery();

            // Reading the returned data stored in the result set
            String confirmation = "";
            while(rs.next()){
                confirmation = rs.getString(1);
            }

            // Returning the confirmation that the user data was updated
            return confirmation;
            
        } catch (Exception e) {
            // If the autocommit was set to false and an error has occurred it is possible to rollback here.
            // for autocommit(true), the default one if not explicit set to false, the rollback occurs automatically.
            e.printStackTrace();

        } finally {
            // Closes the connection with the database if still exists
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return String if the try_catch fails
        return "ERROR: COULD NOT UPDATE THE USER DATA";
    }

    public String depositMoney(Connection conn, int accountNumber, BigDecimal depositAmount) {

        try(PreparedStatement st = conn.prepareStatement("SELECT deposit_money(?, ?)")) {

            // Setting the Statement params
            st.setInt(1, accountNumber);
            st.setBigDecimal(2, depositAmount);
            
            // Execution the statement and storing the returned value in a result set
            ResultSet rs = st.executeQuery();

            // Reading the returned data stored in the result set
            String updatedAccountBalance = "";
            while(rs.next()){
                updatedAccountBalance = rs.getString(1);
            }

            // Returning the deposited amount and the new account balance
            return (depositAmount + " DEPOSITED, YOUR NEW ACCOUNT BALANCE IS: " + updatedAccountBalance);

        } catch (Exception e) {
            // If the autocommit was set to false and an error has occurred it is possible to rollback here.
            // for autocommit(true), the default one if not explicit set to false, the rollback occurs automatically.
            e.printStackTrace();

        } finally {
            // Closes the connection with the database if still exists
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return String if the try_catch fails
        return "ERROR: COULD NOT COMPLETE THE DEPOSIT";
    }

    public String withdrawMoney (Connection conn, int accountNumber, BigDecimal withdrawAmount){

        try(PreparedStatement st = conn.prepareStatement(" SELECT withdraw_money(?, ?)")){

            // Setting the Statement params
            st.setInt(1,accountNumber);
            st.setBigDecimal(2, withdrawAmount);

            // Execution the statement and storing the returned value in a result set
            ResultSet rs = st.executeQuery();

            // Reading the returned data stored in the result set
            String confirmation = "";
            while(rs.next()){
                confirmation = rs.getString(1);
            }

            // Returning the confirmation with the amount that was withdrawn or if the founds were insufficient
            return confirmation;

        } catch (Exception e) {
            // If the autocommit was set to false and an error has occurred it is possible to rollback here.
            // for autocommit(true), the default one if not explicit set to false, the rollback occurs automatically.
            e.printStackTrace();

        } finally {
            // Closes the connection with the database if still exists
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return String if the try_catch fails
        return "ERROR: COULD NOT WITHDRAW THE MONEY";
    }

    // Single statement call, making manually commiting not necessary.
    // But using the setAutoCommit() to false for exemplification
    public String transferMoney(
            Connection conn,
            int senderAccountNumber,
            int destinyAccountNumber,
            BigDecimal transferAmount,
            LocalDate senderAccountUserBirthdate,
            int senderAccountUserSocialNumber,
            String senderAccountUserMotherFullName) {

        try(PreparedStatement st = conn.prepareStatement("SELECT transfer_money( ?, ?, ?, CAST(? AS DATE), ?, ? );")){

            // Disabling the auto commit
            conn.setAutoCommit(false);

            // Setting the Statement params
            st.setInt(1,senderAccountNumber);
            st.setInt(2, destinyAccountNumber);
            st.setBigDecimal(3, transferAmount);
            st.setString(4,senderAccountUserBirthdate.toString());
            st.setInt(5, senderAccountUserSocialNumber);
            st.setString(6, senderAccountUserMotherFullName);

            // Execution the statement and storing the returned value in a result set
            ResultSet rs = st.executeQuery();

            // Commiting the transaction
            // This will commit the previous statements executions commit IF THOSE WERE ALL SUCCESSFULLY.
            // Else it will throw an exception
            conn.commit();

            // Reading the returned data stored in the result set
            String confirmation = "";
            while(rs.next()){
                confirmation = rs.getString(1);
            }

            // Returning the confirmation with the amount that was transferred, if the founds were insufficient or if one of the account numbers could not be found
            return confirmation;

        } catch (Exception e) {
            // If the autocommit is set to false and an error has occurred here will be responsible for the rollback.
            // for autocommit(true), the default one if not explicit set to false, the rollback occurs automatically.
            // print the error that prevented the commit
            e.printStackTrace();

            // try_catch block for the rollback
            try {
                conn.rollback();
            } catch (SQLException exRollback) {
                // will print the error if the successful commit and rollback fails
                exRollback.printStackTrace();
            }

        } finally {
            // Closes the connection with the database if still exists
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // Return String if the try_catch fails
        return "ERROR: MONEY TRANSFERENCE HAS FAILED";
    }

}
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.*;
import java.util.Locale;
import java.util.Scanner;


public class Main {
    public static void main(String args[]) {
        Scanner scan = new Scanner(System.in);
        int choice = 1, option = 1, customerid = 0;
        SavingsAccount s;
        PayAccount p;
        Account acc = new Account();
        String accid;
        System.out.println("Welcome to IDBC Bank");
        while(choice==1){
        System.out.println("Enter appropriate choice\n'1' for Opening an Account\n'2' for Existing account");
        int n = scan.nextInt();
        if (n == 1) {
            System.out.println("Enter appropriate option\n's'- create savings account\n'P'- create pay account");
            String in = scan.next();
            scan.nextLine();
            System.out.print("enter name:");
            String name = scan.nextLine();
            System.out.print("enter age :");
            int age = scan.nextInt();
            System.out.print("Enter Phone Number :");
            long phnNo = scan.nextLong();
            scan.nextLine();
            System.out.print("Enter City : ");
            String city = scan.nextLine();

            if (in.equalsIgnoreCase("S") || in.equalsIgnoreCase("P")) {
                if (age < 18) {
                    System.out.println("your are not eligible for creating account");
                } else {
                    if (in.equalsIgnoreCase("S")) {
                        String acctype = "savings";
                        s = new Account(name, age, phnNo, city, acctype);
                        acc = new Account((Account) s);
                        choice=2;
                    } else {
                        String acctype = "pay";
                        p = new Account(name, age, phnNo, city, acctype);
                        acc = new Account((Account) p);
                        choice=2;
                    }
                    System.out.println("Bank account should have minimum balance.i.e,2000");
                    int depositAmount = 0;
                    while (true) {
                        System.out.println("Enter Amount more than minimum balance");
                        depositAmount = scan.nextInt();
                        if (depositAmount >= 2000)
                            break;
                    }
                    acc.deposit(depositAmount);
                }

            }
        } else if (n == 2) {
            System.out.println("enter your account id");
            accid = scan.next();

            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc", "root", "password@123");
                PreparedStatement st = con.prepareStatement("select cid from accounts where accid=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                st.setString(1, accid);
                ResultSet rs = st.executeQuery();
                if (rs.next() == false){
                    System.out.println("---------ACCOUNT NOT FOUND----------");
                    System.out.println("Enter 1 for going back to main screen");
                    choice=scan.nextInt();

                }
                else {
                    customerid = rs.getInt(1);
                    st = con.prepareStatement("select fname from customer where cid=?");
                    st.setInt(1, customerid);
                    ResultSet nm = st.executeQuery();
                    if (nm.next()) {
                        System.out.println("*************************************-- WELCOME " + nm.getString(1).toUpperCase(Locale.ROOT) + " --***************************************");
                        Account.accountNumber = accid;
                        choice=2;

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Invalid Input");
        }
        }
        while (option == 1) {
            System.out.println("Enter the option\n1-for Check Balance\n2-Deposit\n3-Fund Transfer\n4-Calculate Intrest\n5-Mini Statement");
            int value = scan.nextInt();
            switch (value) {
                case 1:
                    acc.checkBalance();
                    break;
                case 2:
                    int depositAmount = 0;
                    System.out.println("Enter Amount");
                    depositAmount = scan.nextInt();
                    acc.deposit(depositAmount);
                    break;
                case 3:
                    acc.fundTransfer();
                    break;
                case 4:
                    acc.calculateIntrest();
                    break;
                case 5:
                    acc.miniStatement();
                    break;
                default:
                    System.out.println("Invalid Option");
            }

            System.out.println("Enter '1' to perform operations on BankAccount\nEnter other than '1' to quit");
            option = scan.nextInt();
            if (option != 1)
                break;
        }
        System.out.println("Thank you for Visiting IDBC Bank");
        }
    }


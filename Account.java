import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Account implements PayAccount,SavingsAccount {

         String name,city,acctype,AccountCreatedDate,LastUpdatedIntrestDate,currentdate,AccCreDate;
         static String accountNumber,date;
         int age, pin;
         static  int totalBalance=0;
         long phnNo,count=0;
         Account account;
         Scanner scan=new Scanner(System.in);

        public Account(String name, int age, long phnNo, String city,String acctype){
        this.name = name;
        this.age = age;
        this.phnNo = phnNo;
        this.city = city;
        this.acctype=acctype;
        createAccount();
        }

        public Account(Account account) {
        this.account = account;
        }

         public Account() {
        }
        public static void setDate(String d){
            date=d;
        }

        public void generateAccountNumberAndPin () {
        int lowerBound = 10000000, higherBound = 80000000, low = 1000, high = 8999;
        Random generate = new Random();
        accountNumber=("IDBC" + Integer.toString(generate.nextInt(higherBound - lowerBound) + lowerBound));
        pin=(generate.nextInt(high - low) + low);
        System.out.println("Account Number - " + accountNumber);
        System.out.println("Pin - " + pin);
    }

        public void calculateIntrest () {
            try{
                Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
                PreparedStatement ps= con.prepareStatement("select acctype,balance,AccountCreatedDate,LastUpdatedIntrestDate,curdate() from accounts where accid=?");
                ps.setString(1,accountNumber);
                ResultSet r=ps.executeQuery();
                if(r.next()==false)
                    System.out.println("Account doesn't exist");
                else{
                    acctype=r.getString(1);
                    totalBalance=r.getInt(2);
                    AccountCreatedDate=r.getString(3);
                    String s[]=AccountCreatedDate.split(" ");
                    AccCreDate=s[0];
                    LastUpdatedIntrestDate=r.getString(4);
                    currentdate=r.getString(5);
                    if(acctype.equalsIgnoreCase("savings")){
                        if(LastUpdatedIntrestDate==null && AccCreDate.equalsIgnoreCase(currentdate))
                        {
                            ps=con.prepareStatement("update accounts set LastUpdatedIntrestDate=? where accid=?");
                            ps.setString(1,AccountCreatedDate);
                            ps.setString(2,accountNumber);
                            ps.executeUpdate();
                        }
                        if(LastUpdatedIntrestDate==null)
                            LastUpdatedIntrestDate=currentdate;
                        ps=con.prepareStatement("SELECT DATEDIFF(?,?) AS DateDiff");
                        ps.setString(1,LastUpdatedIntrestDate);
                        ps.setString(2,AccountCreatedDate);
                        ResultSet res=ps.executeQuery();
                        if(res.next()==false){
                           System.out.println("Error");
                        }
                        else{
                            int datediff=res.getInt(1);
                            if(datediff<=365)
                                System.out.println("Intrest is already added to your account on time\nyour balance after adding intrest is "+totalBalance);
                            else if(datediff>=365){
                                int rem=datediff%365;
                                int quo=datediff/365;
                                if(rem==0){
                                    totalBalance=(int)(totalBalance+(totalBalance * 0.025));
                                    ps= con.prepareStatement("update accounts set balance=?,LastUpdatedIntrestDate=current_date() where accid=?");
                                    ps.setInt(1,totalBalance);
                                    ps.setString(2,accountNumber);
                                    int t=ps.executeUpdate();
                                    System.out.println("Intrest Amount is added to your account\n your balance is "+totalBalance);
                                    intrestTranserLog(accountNumber,"Intrest Calculation",LastUpdatedIntrestDate,totalBalance,"Intrest is Credited");
                                  }
                                else{
                                    while(count<quo){
                                        totalBalance=(int)(totalBalance+(totalBalance * 0.025));
                                        count++;
                                    }
                                    ps= con.prepareStatement("SELECT date_sub(current_date(), INTERVAL ? DAY)");
                                    ps.setInt(1,rem);
                                    ResultSet rs=ps.executeQuery();
                                    rs.next();
                                    LastUpdatedIntrestDate=rs.getString(1);
                                    ps= con.prepareStatement("update accounts set balance=?,LastUpdatedIntrestDate=current_date() where accid=?");
                                    ps.setInt(1,totalBalance);
                                    ps.setString(2,accountNumber);
                                    int t=ps.executeUpdate();
                                    System.out.println("Intrest is already added to your account on time\nyour balance after adding intrest is "+totalBalance);
                                    intrestTranserLog(accountNumber,"Intrest Calculation",LastUpdatedIntrestDate,totalBalance,"Intrest is Credited");

                                }
                            }
                        }
                    }
                    else{
                        System.out.println("Your account is payments account\nyou cannot have intrest");
                        System.out.println("********************************************************************************************************************************************************");}
                }
            }catch(SQLException e ){e.printStackTrace();}
       }


        public void deposit (int depositAmount) {

            try{
                Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
                PreparedStatement ps=con.prepareStatement("select balance from accounts where accid=?");
                ps.setString(1,accountNumber);
                ResultSet r=ps.executeQuery();
                if(r.next()==false)
                    System.out.println("Acc is not present");
                else{
                    totalBalance=r.getInt(1);
                    totalBalance=totalBalance+depositAmount;
                    ps= con.prepareStatement("update accounts set balance=? where accid=?");
                    ps.setInt(1,totalBalance);
                    ps.setString(2,accountNumber);
                    int t=ps.executeUpdate();
                    if(t>0)
                        System.out.println("\nBalance " +totalBalance);
                    transactionLog(accountNumber,"Deposit",totalBalance,"Amount is Added");
                    System.out.println("*******************************************************************************************************************");}
            }catch(SQLException e ){e.printStackTrace();}
       }

        public void checkBalance () {
            try{
                Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
                PreparedStatement ps= con.prepareStatement("select balance from accounts where accid=?");
                ps.setString(1,accountNumber);
                ps.executeQuery();
                ResultSet r=ps.executeQuery();
                System.out.println(accountNumber);
                if(r.next()==false)
                    System.out.println("\nAccount Not Exists");
                else{
                    System.out.println("\nBalance = "+r.getInt(1));
                }
                transactionLog(accountNumber,"Balance Check",totalBalance,"Checking Balance");
                System.out.println("*******************************************************************************************************************");
              }catch(SQLException e ){e.printStackTrace();}
        }

        public void fundTransfer () {
        int transfer_amount = 0;
        String reciveraccountid;
        System.out.println("Enter the amount you like to transfer ");
        transfer_amount = scan.nextInt();
        System.out.println("Enter receiver account number");
        reciveraccountid=scan.next();
        try{
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
            PreparedStatement ps=con.prepareStatement("select balance from accounts where accid=?");
            ps.setString(1,accountNumber);
            ResultSet r=ps.executeQuery();
            if(r.next()==false)
                System.out.println("Acc is not present");
            else{
                totalBalance=r.getInt(1);
               }
            if (transfer_amount >totalBalance || totalBalance <=2000)
                System.out.println("Insufficient Balance\nAvailable Balance is " + totalBalance);
            else {
                totalBalance=totalBalance-transfer_amount;
                ps=con.prepareStatement("select cid,balance from accounts where accid=?");
                ps.setString(1,reciveraccountid);
                ResultSet rq=ps.executeQuery();
                if(rq.next()==false){
                    ps= con.prepareStatement("update accounts set balance=? where accid=?");
                    ps.setInt(1,totalBalance);
                    ps.setString(2,accountNumber);
                    int re=ps.executeUpdate();
                    if(!(re>0))
                        System.out.println("Error occured during Fund Transfer");
                    else{
                        System.out.println(transfer_amount + " is successfully transfered.\nAvailable Balance is " +totalBalance);
                    }
                }
                else{
                    String cid=rq.getString(1);
                    int receiverbal=rq.getInt(2);
                    receiverbal=receiverbal+transfer_amount;
                    ps=con.prepareStatement("select Fname from customer where cid=?");
                    ps.setString(1,cid);
                    ResultSet rname=ps.executeQuery();
                    rname.next();
                    ps= con.prepareStatement("update accounts set balance=? where accid=?");
                    ps.setInt(1,receiverbal);
                    ps.setString(2,reciveraccountid);
                    ps.executeUpdate();
                    int re=ps.executeUpdate();
                    ps= con.prepareStatement("update accounts set balance=? where accid=?");
                    ps.setInt(1,totalBalance);
                    ps.setString(2,accountNumber);
                    ps.executeUpdate();
                    int ra=ps.executeUpdate();
                    if(!(re>0 && ra>0))
                        System.out.println("Error occured during Fund Transfer");
                    else{
                    System.out.println(transfer_amount + " is successfully transfered to "+rname.getString(1));
                    System.out.println("\nAvailable Balance is " +totalBalance);
                    fundTransferLog(reciveraccountid,"Credit",accountNumber,totalBalance,"Amount is credited");
                    }
                }
            }
                fundTransferLog(accountNumber,"Debit",reciveraccountid,totalBalance,"Amount is deducted");
                System.out.println("*******************************************************************************************************************");
        }catch(SQLException e ){e.printStackTrace();}
       }


    public void createAccount(){
            try{
               generateAccountNumberAndPin();
               Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
               PreparedStatement ps=con.prepareStatement("insert into customer(fname,age,phnno,city) values(?,?,?,?)");
               ps.setString(1,name);
               ps.setLong(3,phnNo);
               ps.setInt(2,age);
               ps.setString(4,city);
               int k=ps.executeUpdate();
               ps= con.prepareStatement("select cid from customer where fname=? and phnNo=?");
               ps.setString(1,name);
               ps.setLong(2,phnNo);
               ResultSet r=ps.executeQuery();
               if (r.next()==false){
                   System.out.println("Error!! Try creating Account again");
               }
               else{
                   System.out.println("Customer ID is "+r.getInt(1));
                   int customerid=r.getInt(1);
                   ps=con.prepareStatement("insert into accounts(accid,cid,accpin,acctype,balance) values (?,?,?,?,?)");
                   ps.setString(1,accountNumber);
                   ps.setInt(2,customerid);
                   ps.setInt(3,pin);
                   ps.setString(4,acctype);
                   ps.setInt(5,totalBalance);
                   int ac=ps.executeUpdate();
                   if(ac>0)
                       System.out.println("Account created successfully");
                       transactionLog(accountNumber,"Account Creation",totalBalance,"Account is created");

                   System.out.println("*******************************************************************************************************************");
               }
        }catch(SQLException e ){e.printStackTrace();}
    }
    public void miniStatement(){
            try{
                Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
                PreparedStatement ps=con.prepareStatement("select * from transactions where accid=?");
                ps.setString(1,accountNumber);
                ResultSet r=ps.executeQuery();
                if(r.next()==false)
                    System.out.println("Account is not present\\No transactions are made");
                else{
                    System.out.format("%30s %30s %30s %30s %30s %30s %30s","| TransId |","| AccId |","| TransType |","| SenderOrReciverAccNo |","| TransDate |","| Balance |","| Breifing |");
                    do{//System.out.format("%10s %30s", "Srno", "Item Category\n")
                        System.out.println();
                        System.out.format("%30s %30s %30s %30s %30s %30d %30s ",r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getInt(6),r.getString(7));
                    }while(r.next());
                }
                System.out.println();
                System.out.println("*******************************************************************************************************************");
            }catch(SQLException e ){e.printStackTrace();}


    }
    public void transactionLog(String accid,String transtype,int balance,String breifing){
            try{
            Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
            PreparedStatement ps= con.prepareStatement("insert into transactions(accid,transtype,balance,breifing) values (?,?,?,?)");
            ps.setString(1,accid);
            ps.setString(2,transtype);
            ps.setInt(3,balance);
            ps.setString(4,breifing);
            ps.executeUpdate();
        }catch(SQLException e ){e.printStackTrace();}
    }
    public void fundTransferLog(String accid,String transtype,String SenderOrReciverAccNo,int balance,String breifing){
            try{
                Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc","root","password@123");
                PreparedStatement ps= con.prepareStatement("insert into transactions(accid,transtype,SenderOrReciverAccNo,balance,breifing) values (?,?,?,?,?)");
                ps.setString(1,accid);
                ps.setString(2,transtype);
                ps.setString(3,SenderOrReciverAccNo);
                ps.setInt(4,balance);
                ps.setString(5,breifing);
                ps.executeUpdate();
            }catch(SQLException e ){e.printStackTrace();}
    }

    public void intrestTranserLog(String accid,String transtype,String transdate,int balance,String breifing) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/idbc", "root", "password@123");
            PreparedStatement ps = con.prepareStatement("insert into transactions(accid,transtype,transdate,balance,breifing) values (?,?,?,?,?)");
            ps.setString(1, accid);
            ps.setString(2, transtype);
            ps.setString(3, transdate);
            ps.setInt(4, balance);
            ps.setString(5, breifing);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}


 
package Business;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 *
 * @author amadou
 */
public class Card {
    private int acctno;
    private double crlimit, baldue;
    private String errmsg, actionmsg;
 
public Card() {
    //create new account...
    
  this.acctno = 0;
  this.crlimit = 0;
  this.baldue =  0;
  this.errmsg = "";
  this.actionmsg = "";
  
  while (this.acctno == 0) {
      try {
          this.acctno = (int) (Math.random() * 1000000);
          BufferedReader in = new BufferedReader( 
                          new FileReader ("CC" + this.acctno + ".txt"));
          //bad result: account already exists
          in.close();
          this.acctno = 0;
          
      
      } catch (IOException e) {
          //good result : new account number
          this.crlimit = 1000;
          if (writestatus() == true) {
              this.actionmsg = "Acount # " + this.acctno + " Opened.";
              writelog(this.actionmsg);
              
          } else {
           //open failed: fatal error
           this.acctno = -1;
           this.crlimit = 0;
          }
          }
     
          }//end of while

          }//end of constructor
     public Card(int a) {
         //overload constructor to re-open existing account
         this.acctno = a;
         this.crlimit = 0;
         this.baldue = 0;
         this.errmsg = "";
         this.actionmsg = "";
         
        try {
            BufferedReader in = new BufferedReader(
                    new FileReader("CC" + this.acctno + " .txt"));
            this.crlimit = Double.parseDouble(in.readLine());
            this.baldue = Double.parseDouble(in.readLine());
            in.close();
            this.actionmsg = "Account " + this.acctno + " re-opened." ;
            
        } catch (IOException e) {
            //account not found
            this.acctno = 0;
            this.errmsg = "Account" + a + " not found.";
        
        } catch (NumberFormatException e) {
            //account found but CC file damaged
            this.acctno = 0;
            this.errmsg = "Acccount" + a + " has damaged status file.";
            
        }
            
    }

   
       private boolean writestatus() {
           boolean result = true;
          try {
             PrintWriter out = new PrintWriter(
                         new FileWriter ("CC" + this.acctno + " .txt") );
             out.println(this.crlimit);
             out.println(this.baldue);
             out.close();
             
           } catch (IOException e) {
             result = false;
             this.errmsg = "Write status failed on account: " + e.getMessage();
               
           }
          return result;
       }
       
       private void writelog(String msg) {
          try {
              Calendar cal = Calendar.getInstance();
              DateFormat df = DateFormat.getDateTimeInstance();
              String ts = df.format(cal.getTime());
              PrintWriter out = new PrintWriter(
                      new FileWriter("CCL" + this.acctno + ".txt",true) );
             out.println(ts + ": " + msg);
             out.close();
              
           
          } catch (IOException e) {
              this.errmsg = "Write log failure: " + e.getMessage();
          }
           
       }//end of writelog
       public int getAcctNo() {
           return this.acctno;
       }
       public double getCrLimit() {
           return this.crlimit;
       }
       public double getBalDue() {
           return this.baldue;
       }
       public double getCrRemain() {
           return this.crlimit - this.baldue;
       }
       public String getErrorMsg() {
          return this.errmsg;
       }
       public String getActionMsg() {
           return this.actionmsg;
       }
       
       public void SetCharge(double c, String d) {
           this.errmsg = "";
           this.actionmsg = "";
           
          
           if (this.acctno <= 0) {
           this.errmsg = "Charge attempt on a non-active account.";    
           
           return;
          }
           
           if (c <= 0) {
             this.actionmsg = "Charge declined: must be > 0";
             writelog(this.actionmsg);
           } else if(d.isEmpty()) {
            this.actionmsg = " Charge of " + c + " declined: no description.";
            writelog(this.actionmsg);
           } else if (c > getCrRemain()) {
            this.actionmsg = " Charge declined: over limit.";
            writelog(this.actionmsg);
           } else {
               this.baldue += c;
               if (writestatus()) {
               this.actionmsg = " Charge of " + c + " for " + d + " posted.";
               writelog(this.actionmsg);
           } else {
                   this.baldue -= c;
           }
           }
    
       }
       public ArrayList<String> getLog() { 
           this.actionmsg = "";
           this.errmsg = "";
           
           if (this.acctno <= 0) {
           this.errmsg = "Charge attempt on a non-active account.";
           return null;
           }
       ArrayList<String> lg = new ArrayList<>();
       try {
          BufferedReader in = new BufferedReader(
                  new FileReader("CCL" + this.acctno + ".txt"));
          String s = in.readLine();
          while (s != null) {
              lg.add(s);
              s = in.readLine();
          
          }
          in.close();
     this.actionmsg = lg.size() + " entries returned for account: " + this.acctno;
         
         } catch (IOException e) {
         this.errmsg = "Log file error: " + e.getMessage();
         lg = null;
       
           
       }
       return lg;
       }//end of getLog
       
    public void setCrInc(double inc) {
        this.errmsg = "";
        this.actionmsg = "";
        if (this.acctno <= 0) {
        this.errmsg = "Charge attempt on non-active account.";
        return;
        }
        if (inc <= 0) {
        this.errmsg = "Credit Increase request non positive.";
        writelog(this.errmsg);
        return;
        }
      if (inc % 100 != 0){
          //increase is not a multiple of 100
          this.actionmsg = "Increase declined: not a multiple of 100.";
          writelog(this.actionmsg);
      } else {
          int rnum = (int) (Math.random() * 10) + 1;
          if (rnum >= 4) {
              //grant increase
              this.crlimit += inc;
              if(writestatus()) {
              this.actionmsg = " Credit Increase of " + inc + "granted.";
              writelog(this.actionmsg);
              } else {
              this.crlimit -= inc;
              }
              
          } else {
    this.actionmsg = "Increase request of " + inc + " declined at this time.";
         writelog(this.actionmsg);
          }
      }
        
    }
}  



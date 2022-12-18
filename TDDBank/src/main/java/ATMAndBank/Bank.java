package ATMAndBank;

import CCardAndPerson.Person;
import Request.ATMrequest;

public class Bank {
    private ATMrequest atmRequest;

    public Person getPerson(String cardNumberId){
        return null;
    }

    public String attemptLogin(String creditCardIdNumber, String username, String PIN){
        return null;
    }
    public int attemptedLoginTries(String creditCardIdNumber){
        return 0;
    }
    public boolean creditCardStatus(String creditCardIdNumber){
        return false;
    }

    public void deposit(Integer addAmount){

    }
    public int getMoney(String creditCardNumberId){
        return 0;
    }
    public void withdrawMoney(Integer withdrawAmount){
    }

}

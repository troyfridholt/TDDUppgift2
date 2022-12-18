package ATMAndBank;

import Request.ATMrequest;
import CCardAndPerson.Person;

public class ATM {
    private Bank bank;

    public ATM(Bank bank) {
        this.bank = bank;
    }

    public String getPerson(ATMrequest atmRequest){
        Person person = bank.getPerson(atmRequest.getCreditCard().getIdNumber());
        return person.getFirstName() + " " + person.getLastName();
    }

    public String attemptLogin(ATMrequest atmRequest){
        if (atmRequest.getCreditCard().getPin().equals(atmRequest.getPinInput())){
            return "Logged in";
        }
        return loginTries(atmRequest);
    }

    public String loginTries(ATMrequest atmRequest) {
        int attempts = bank.attemptedLoginTries(atmRequest.getCreditCard().getIdNumber());

        if(attempts == 1){
            return "Try Again (1/3)";
        }
        else if(attempts == 2){
            return "Try Again (2/3)";
        }
        else if (attempts == 3){
            return "Try Again (3/3)";
        }
        else{
            return "Too many wrong attempts.";
        }
    }

    public String cardStatus(ATMrequest atmRequest){
        if(bank.creditCardStatus(atmRequest.getCreditCard().getIdNumber())){
            return "Logged in";
        }
        else{
            return "Error";
        }
    }

    public int getAmount(ATMrequest atmRequest){
        if(atmRequest.getCreditCard().isAccessedLoggedIn())
            return bank.getMoney(atmRequest.getCreditCard().getIdNumber());
        else{
            return 0;
        }
    }

    public void deposit(ATMrequest atmRequest, Integer amountToAdd){
        bank.deposit(atmRequest.setDeposit(amountToAdd));
    }

    public String withdraw(ATMrequest atmRequest){
        if (getAmount(atmRequest) >= atmRequest.getWithdraw()){
            bank.withdrawMoney(atmRequest.getWithdraw());
            return "Withdrawn: " + atmRequest.getWithdraw();
        }
        else{
            return "Not enough funds. Try again.";
        }
    }




}

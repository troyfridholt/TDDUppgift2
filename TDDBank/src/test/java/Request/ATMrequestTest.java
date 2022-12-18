package Request;

import ATMAndBank.ATM;
import ATMAndBank.Bank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import CCardAndPerson.CreditCard;
import CCardAndPerson.Person;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ATMrequestTest {

    private Bank bank;
    private ATM atm;
    private ArgumentCaptor<Integer>depositCaptor;
    private ArgumentCaptor<Integer>withdrawCaptor;

    @BeforeEach
    public void setUp(){
        bank = mock(Bank.class);
        atm = new ATM(bank);
        depositCaptor = ArgumentCaptor.forClass(Integer.class);
        withdrawCaptor = ArgumentCaptor.forClass(Integer.class);
    }

    private static List<Arguments> testData(){
        return Arrays.asList(
                Arguments.of("1234 1234 1234 1234", "Karl","Nilsson", "1234"),
                Arguments.of("1111 2222 3333 4444", "Erik","Eriksson", "1234")
        );
    }

    @ParameterizedTest
    @MethodSource("testData")
    public void getUsersFromBank(String cardId, String firstName, String lastName){
        ATMrequest atmRequest = new ATMrequest(new CreditCard(cardId,"",""));
        when(bank.getPerson(cardId)).thenReturn(new Person(firstName, lastName));

        String expected = firstName + " " + lastName;
        String actual = atm.getPerson(atmRequest);

        assertEquals(expected, actual);
    }


    @Test
    public void checkIfCheckingMoneyWorks(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "", "");
        creditCard.setAccessedLoggedIn(true);
        ATMrequest atmRequest = new ATMrequest(creditCard);
        int moneyInBank = 45000;
        when(bank.getMoney("1234 1234 1234 1234")).thenReturn(moneyInBank);
        int expected = moneyInBank;
        int actual = atm.getAmount(atmRequest);

        assertEquals(expected, actual);

    }

    @Test
    public void checkCreditCardDeposit(){
        CreditCard creditCard = new CreditCard("9999 8888 7777 6666", "", "");
        creditCard.setAccessedLoggedIn(true);
        int depositAmount = 5000;
        int moneyInBank = 50000;
        ATMrequest atmRequest = new ATMrequest(creditCard, depositAmount);
        when(bank.getMoney("9999 8888 7777 6666")).thenReturn(moneyInBank);
        atm.deposit(atmRequest, depositAmount);
        verify(bank,times(1)).deposit(depositCaptor.capture());
        assertEquals(depositAmount, depositCaptor.getValue());


    }

    @Test
    public void checkIfFundsIsEfficient(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "", "4444");
        creditCard.setAccessedLoggedIn(true);
        int withdrawAmount = 90;
        int moneyInBank = 120;
        ATMrequest atmRequest = new ATMrequest(creditCard, "4444", withdrawAmount,0);
        when(bank.getMoney("1234 1234 1234 1234")).thenReturn(moneyInBank);

        atm.withdraw(atmRequest);
        verify(bank,times(1)).withdrawMoney(withdrawCaptor.capture());
        assertEquals(withdrawAmount, withdrawCaptor.getValue());
    }

    @Test
    public void checkIfItWorksToWithdrawTooMuchMoney(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "", "");
        creditCard.setAccessedLoggedIn(true);
        int withdrawAmount = 90;
        int moneyInBank = 50;
        ATMrequest atmRequest = new ATMrequest(creditCard, withdrawAmount);
        when(bank.getMoney("1234 1234 1234 1234")).thenReturn(moneyInBank);
        atm.withdraw(atmRequest);
        verify(bank,times(1)).withdrawMoney(withdrawCaptor.capture());
        assertNotEquals(withdrawAmount, withdrawCaptor.getValue());
    }

    @Test
    public void checkIfAttempt1Works(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "blabla", "1234");
        String wrongPIN = "1111";
        ATMrequest atmRequest = new ATMrequest(creditCard, wrongPIN);
        when(bank.attemptedLoginTries("1234 1234 1234 1234")).thenReturn(1);
        String expected = "Try Again (1/3)";
        String actual = atm.attemptLogin(atmRequest);
        assertEquals(expected, actual);
    }
    @Test
    public void checkIf3AttemptsWorks(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "blabla", "1234");
        String wrongPIN = "1111";
        ATMrequest atmRequest = new ATMrequest(creditCard, wrongPIN);
        when(bank.attemptedLoginTries("1234 1234 1234 1234")).thenReturn(3);
        String expected = "Try Again (3/3)";
        String actual = atm.attemptLogin(atmRequest);
        assertEquals(expected, actual);
    }
    @Test
    public void checkIfBlockAccessWorks(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "blabla", "1234");
        String wrongPIN = "1111";
        ATMrequest atmRequest = new ATMrequest(creditCard, wrongPIN);
        when(bank.attemptedLoginTries("1234 1234 1234 1234")).thenReturn(4);
        String expected = "Too many wrong attempts.";
        String actual = atm.attemptLogin(atmRequest);
        assertEquals(expected, actual);
    }

    @Test
    public void checkAccessToCCard(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "blabla", "1234");
        ATMrequest atmRequest = new ATMrequest(creditCard);
        boolean status = bank.creditCardStatus("1234 1234 1234 1234");
        when(status).thenReturn(true);
        String expected = "Logged in";
        String actual = atm.cardStatus(atmRequest);
        assertEquals(expected, actual);
    }

    @Test
    public void checkIfBlocked(){
        CreditCard creditCard = new CreditCard("1234 1234 1234 1234", "blabla", "1234");
        ATMrequest atmRequest = new ATMrequest(creditCard);
        boolean status = bank.creditCardStatus("1234 1234 1234 1234");
        when(status).thenReturn(false);
        String expected = "Error";
        String actual = atm.cardStatus(atmRequest);
        assertEquals(expected, actual);
    }

}
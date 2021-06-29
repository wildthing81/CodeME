package com.me.utils;

import com.me.calculator.TransactionRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionUtilsTest {

    @Test
    void  testCreditTransaction() {
        TransactionRecord trxRecord = new TransactionRecord("TX12", "ME2", "ME1",
                "20/10/2018 12:00:00", "25.0", "PAYMENT" ,null);

        String accountId="ME1";
        assertTrue(TransactionUtils.isTrxCredit(accountId,trxRecord));
    }


    @Test
    void  testDebitTransaction() {
        TransactionRecord trxRecord = new TransactionRecord("TX12", "ME1", "ME2",
                "20/10/2018 12:00:00", "25.0", "PAYMENT" ,null);

        String accountId="ME1";
        assertTrue(TransactionUtils.isTrxDebit(accountId,trxRecord));

    }

    @Test
    void  testCreationTimeInInputTimeFrame() {
        TransactionRecord trxRecord = new TransactionRecord("TX12", "ME1", "ME2",
                "20/10/2018 12:47:55",
                "25.0", "PAYMENT" ,null);

        LocalDateTime startTime = LocalDateTime.parse("20/10/2018 12:00:00",TransactionUtils.dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse("20/10/2018 19:00:00",TransactionUtils.dateTimeFormatter);

        assertTrue(TransactionUtils.isTimeValidTrx(startTime,endTime,trxRecord));
    }

    @Test
    void  testCreationTimeEqualToInputStartTime() {
        TransactionRecord trxRecord = new TransactionRecord("TX12", "ME1", "ME2",
                "20/10/2018 12:00:00",
                "25.0", "PAYMENT" ,null);

        LocalDateTime startTime = LocalDateTime.parse("20/10/2018 12:00:00",TransactionUtils.dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse("20/10/2018 19:00:00",TransactionUtils.dateTimeFormatter);

        assertTrue(TransactionUtils.isTimeValidTrx(startTime,endTime,trxRecord));
    }

    @Test
    void  testCreationTimeEqualToInputEndTime() {
        TransactionRecord trxRecord = new TransactionRecord("TX12", "ME1", "ME2",
                "20/10/2018 19:00:00",
                "25.0", "PAYMENT" ,null);

        LocalDateTime startTime = LocalDateTime.parse("20/10/2018 12:00:00",TransactionUtils.dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse("20/10/2018 19:00:00",TransactionUtils.dateTimeFormatter);

        assertTrue(TransactionUtils.isTimeValidTrx(startTime,endTime,trxRecord));
    }


    @Test
    void  testCreationTimeOutsideInputTimeFrame() {
        TransactionRecord trxRecord = new TransactionRecord("TX12", "ME1", "ME2",
                "20/10/2018 22:00:00",
                "25.0", "PAYMENT" ,null);

        LocalDateTime startTime = LocalDateTime.parse("20/10/2018 12:00:00",TransactionUtils.dateTimeFormatter);
        LocalDateTime endTime = LocalDateTime.parse("20/10/2018 19:00:00",TransactionUtils.dateTimeFormatter);

        assertFalse(TransactionUtils.isTimeValidTrx(startTime,endTime,trxRecord));
    }


    @Test
    void testValidTransactionRecord(){
        String trxRecord = "TX10001, ACC334455, ACC778899, 20/10/2018 12:47:55, 25.00, PAYMENT";
        String[] fields = TransactionUtils.validTrxFormat(trxRecord);
        assertNotNull(fields);
        assertEquals(6,fields.length);
        trxRecord = "TX10004, ACC334455, ACC998877, 20/10/2018 19:45:00, 10.50, REVERSAL,TX10002";
        fields = TransactionUtils.validTrxFormat(trxRecord);
        assertNotNull(fields);
        assertEquals(7,fields.length);
    }

    @Test
    void testIncorrectNumberOfFields(){
        String trxRecord = "ACC998877,ACC778899 , 20/10/2018 18:00:00, 5.00, PAYMENT";
        assertNull(TransactionUtils.validTrxFormat(trxRecord));
    }

    @Test
    void testMissingTransactionIdTransactionRecord(){
        String trxRecord = ",, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT";
        assertNull(TransactionUtils.validTrxFormat(trxRecord));
    }

    @Test
    void testMissingFromAccountIdTransactionRecord(){
        String trxRecord = "TX10003,, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT";
        assertNull(TransactionUtils.validTrxFormat(trxRecord));
    }

    @Test
    void testMissingToAccountIdTransactionRecord(){
        String trxRecord = "TX10003, ACC998877, , 20/10/2018 18:00:00, 5.00, PAYMENT";
        assertNull(TransactionUtils.validTrxFormat(trxRecord));
    }

    @Test
    void testInvalidAmountTransactionRecord(){
        String trxRecord = "TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5A0, PAYMENT";
        assertNull(TransactionUtils.validTrxFormat(trxRecord));
    }

    @Test
    void testInvalidCreationDateTransactionRecord(){
        String trxRecord = "TX10001, ACC334455, ACC778899, 20/10/18 12:77:55, 25.00, PAYMENT";
        assertNull(TransactionUtils.validTrxFormat(trxRecord));
    }
}

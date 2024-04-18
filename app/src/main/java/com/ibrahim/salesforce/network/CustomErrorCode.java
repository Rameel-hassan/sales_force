package com.ibrahim.salesforce.network;


public enum CustomErrorCode {
    SUCCESS(200), ERROR_SYSTEM(1), ERROR_REQUIRED(2), ERROR_INVALID(3), ERROR_NO_DATA_EXISTS(5), ERROR_ALREADY_EXISTS(6), ERROR_NOT_AUTHORIZED(7);
    private int value;

    //    Code 	Response 	            Response Description
//    200 	Success Response 	    For successful completed transaction will return response code 200 in “Code”.
//      1 	System Error 	        For any System error, transaction will return response code 1 in “Code”.
//      2 	Required Error 	        For any mandatory parameter us missing, transaction will return response code 2 in “Code”.
//      3 	Invalid Error 	        For any transactionid that doesn’t exist in the system, the System will return response code 3 in “Code”.
//      5 	No Data Exists Error 	For any transaction that doesn’t have any data against certain criteria will return response code 5 in “Code”.
//      6 	Already Exists Error 	For any request to whom data already exists against certain criteria, transaction will return response code 6 in “Code”.
//      7 	Not Authorized Error 	For User Authentication/Authorization failure error, transaction will return response code 7 in “Code”.
    private CustomErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

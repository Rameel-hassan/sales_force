/*
 * Property : GSoft 
 * Created by : GSoft
 * Updated by : GSoft
 */

package com.app.salesforce.utilities;

import android.util.Log;

/**
 * The Class DQDebugHelper.
 */
public class DebugHelper {


	/** The mode debug. */
	private static boolean MODE_DEBUG = true;

	/** The mode debug. */
	private static boolean MODE_TRACK = true;

	/** The Constant TAG. */
	private static final String TAG = "DebugHelper";


	/**
	 * Prints the and track error.
	 * 
	 * @param error
	 *            the exception
	 */
	public static void trackError(Error error) {

		DebugHelper.printError(TAG, error);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param TAG
	 *            the tag
	 * @param error
	 *            the exception
	 */
	public static void trackError(String TAG, Error error) {

		DebugHelper.printError(TAG, error);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param TAG
	 *            the tag
	 * @param error
	 *            the exception
	 */
	public static void printError(String TAG, Error error) {

		if (DebugHelper.MODE_DEBUG) {
			DebugHelper.printData(TAG, "Error = " + error.toString());
			error.printStackTrace();
			if (MODE_TRACK) {
				//BugSenseHandler.sendEvent("Error = " + error.toString());
			}
		}
	}
	
	public static void printError(Error error) {

		if (DebugHelper.MODE_DEBUG) {
			DebugHelper.printData(TAG, "Error = " + error.toString());
			error.printStackTrace();
			if (MODE_TRACK) {
				//BugSenseHandler.sendEvent("Error = " + error.toString());
			}
		}
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param exception
	 *            the exception
	 */
	public static void trackException(Exception exception) {

		DebugHelper.print(TAG, exception, true);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void trackException(String TAG, Exception exception) {

		DebugHelper.print(TAG, exception, true);
	}


	/**
	 * Prints the and track exception.
	 * 
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void print(String TAG, Exception exception, boolean track) {

		if (DebugHelper.MODE_DEBUG) {
			DebugHelper.printData(TAG, "Exception = " + exception.toString());
			exception.printStackTrace();
//			if (track) {
				//BugSenseHandler.sendException(exception);
//			}
		}
	}


	/**
	 * Prints the exception.
	 * 
	 * @param exception
	 *            the exception
	 */
	public static void printException(Exception exception) {

		DebugHelper.print(TAG, exception, false);
	}


	/**
	 * Prints the exception.
	 * 
	 * @param TAG
	 *            the tag
	 * @param exception
	 *            the exception
	 */
	public static void printException(String TAG, Exception exception) {

		DebugHelper.print(TAG, exception, true);
	}


	/**
	 * Prints the data.
	 * 
	 * @param data
	 *            the data
	 */
	public static void printData(String data) {

		printData(TAG, data);
	}


	/**
	 * Prints the data.
	 * 
	 * @param TAG
	 *            the tag
	 * @param data
	 *            the data
	 */
	public static void printData(String TAG, String data) {

		if (DebugHelper.MODE_DEBUG) {
			Log.e("" + TAG, "Data = " + data);
		}
	}

}

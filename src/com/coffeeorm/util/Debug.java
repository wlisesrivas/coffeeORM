package com.coffeeorm.util;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * 
 * @author wlises.rivas
 */
public class Debug {

	/**
	 * Standard output log
	 * @param obj
	 * @param error
	 */
	public static void log(Object obj, boolean error) {	
		SimpleDateFormat dateFormat = new SimpleDateFormat("y-W-d k:m:s:S");
		
		if(error)
			System.err.println( dateFormat.format(new Date()) + " " + obj.toString() );
		else
			System.out.println( dateFormat.format(new Date()) + " " + obj.toString() );
	}
	
	/**
	 * Standard output log
	 * @param obj
	 */
	public static void log(Object obj) {
		Debug.log(obj, false);
	}
	
}

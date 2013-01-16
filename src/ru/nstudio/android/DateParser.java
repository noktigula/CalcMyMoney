package ru.nstudio.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.widget.Toast;

public class DateParser 
{
	public static final String 		CALCMONEY_FORMAT = new String("dd MMMM yyyy");
	public static final String 		SQLITE_FORMAT = new String ("yyyy-MM-dd");
	
	public DateParser()
	{
	} // GregorianCalendarParser
	
	public static GregorianCalendar parseStringToDate(Context context, String strDate)
	{
		GregorianCalendar gc;
		try
		{
			Date date = new SimpleDateFormat(SQLITE_FORMAT).parse(strDate);
			gc = new GregorianCalendar();
			gc.setTime(date);
		} // try
		catch (ParseException pExcept)
		{
			Toast.makeText(context, pExcept.getMessage(), 10000).show();
			gc = new GregorianCalendar();
		} // catch
		
		return gc;
	} // parseGregorianCalendar
	
	public static String format(Context context, String strDate, String formatType)
	{
		GregorianCalendar gc = parseStringToDate(context, strDate);		
		SimpleDateFormat sdf = new SimpleDateFormat(formatType);
		return sdf.format(gc.getTime()).toString();
	} // static format
	
	public static String format(Context context, GregorianCalendar date, String formatType)
	{
		GregorianCalendar gc = date;
		SimpleDateFormat sdf = new SimpleDateFormat(formatType);
		return sdf.format(gc.getTime()).toString();
	} // static format w/ GregorianCalendar
		
} // class GregorianCalendarParser

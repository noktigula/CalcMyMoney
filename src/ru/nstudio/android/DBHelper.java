package ru.nstudio.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper 
{

	public static final int CURRENT_DATABASE_VERSION = 1;

    public static final String FINANCE          = "Finance";
    public static final String FINANCE_ID       = "idFinance";
    public static final String FINANCE_REASON   = "reason";
    public static final String FINANCE_PRICE    = "price";
    public static final String FINANCE_QUANTITY = "quantity";
    public static final String FINANCE_DATE     = "financeDate";
    public static final String FINANCE_TYPE     = "type";
	
	public DBHelper(Context context, int version) 
	{
		super(context, "CalcMoneyDB", null, version);
		
	} // DBHelper

	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		String[] strArrMonth = new String[]{"Январь", "Февраль",
											"Март",   "Апрель", "Май",
											"Июнь",   "Июль",	"Август",
											"Сентябрь", "Октябрь", "Ноябрь",
											"Декабрь"};
		
		String queryCreateFinance = new String("CREATE TABLE " + FINANCE + "( " +
											  FINANCE_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
											  FINANCE_REASON   + " TEXT 	 NOT NULL, " +
											  FINANCE_PRICE    + " REAL 	 NOT NULL, " +
											  FINANCE_QUANTITY + " INTEGER NOT NULL, " +
											  FINANCE_TYPE     + " INTEGER NOT NULL, " +
											  FINANCE_DATE     + " TEXT NOT NULL)");
		 
		String queryCreateMonthTitle = new String("CREATE TABLE MonthTitle( " +
												  "idMonthTitle INTEGER PRIMARY KEY AUTOINCREMENT, " +
												  "title		STRING NOT NULL)");
		
		db.execSQL(queryCreateFinance);
		db.execSQL(queryCreateMonthTitle);
		
		ContentValues cv = new ContentValues();
		for (String month : strArrMonth)
		{
			cv.clear();
			cv.put("title", month);
			db.insert("MonthTitle", null, cv);
		} // for
		
		String[] reasons = new String[] {"test_1", "test_2", "test_4", "test_5", "test_6", "test_7",   "test_8"};
		double[] prices = new double[]  {100, 		500,	 30000,		 5000,	   300000,	 1000000, 10000000};
		int[] quants = new int[]		{2,			2,		 1,			 2,		   1,		 1,		  1};
		int[] types = new int[]			{1,			0,		 1,			 0,		   1,		 0,		  1};
		String[] dates = new String[]	{"2012-08-27", "2012-08-25", "2012-08-01", "2012-08-03", "2012-07-15", "2012-07-14", "2011-08-27"};
		
		for(int i = 0; i < reasons.length; ++i)
		{
			cv.clear();
			cv.put("reason", reasons[i]);
			cv.put("price", prices[i]);
			cv.put("quantity", quants[i]);
			cv.put("type", types[i]);
			cv.put("financeDate", dates[i]);
			db.insert("Finance", null, cv);
		} // for
	} // onCreate

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) 
	{
		// TODO Auto-generated method stub

	} // onUpgrade

} // DBHelper

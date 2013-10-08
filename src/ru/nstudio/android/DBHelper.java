package ru.nstudio.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper 
{

	public static final int CURRENT_DATABASE_VERSION = 1;

	public class Finance
	{
		public static final String TABLE_NAME    = "Finance";
		public static final String ID       = "idFinance";
		public static final String REASON   = "reason";
		public static final String PRICE    = "price";
		public static final String QUANTITY = "quantity";
		public static final String DATE     = "financeDate";
		public static final String TYPE     = "type";
		public static final String CATEGORY = "idCategory";
	}

	public class Category
	{
		public static final String TABLE_NAME = "Category";
		public static final String ID 		  = "idCategory";
		public static final String NAME 	  = "CategoryTitle";
	}
	
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

		String[] strCategories= new String[]{"Нет категории", "Транспорт", "Продукты", "Развлечения"};
		
		String queryCreateFinance = new String("CREATE TABLE " + Finance.TABLE_NAME + "( " +
											  Finance.ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
											  Finance.REASON   + " TEXT 	 NOT NULL, " +
											  Finance.PRICE    + " REAL 	 NOT NULL, " +
											  Finance.QUANTITY + " INTEGER NOT NULL, " +
											  Finance.TYPE     + " INTEGER NOT NULL, " +
											  Finance.DATE     + " TEXT NOT NULL," +
											  Finance.CATEGORY + " INTEGER NOT NULL)");
		 
		String queryCreateMonthTitle = new String("CREATE TABLE MonthTitle( " +
												  "idMonthTitle INTEGER PRIMARY KEY AUTOINCREMENT, " +
												  "title		STRING NOT NULL)");

		String queryCreateCategoryTable = new String("CREATE TABLE " + Category.TABLE_NAME + "(" +
													  Category.ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
													  Category.NAME  + " TEXT NOT NULL)");
		
		db.execSQL(queryCreateFinance);
		db.execSQL(queryCreateMonthTitle);
		db.execSQL(queryCreateCategoryTable);
		
		ContentValues cv = new ContentValues();
		for (String month : strArrMonth)
		{
			cv.clear();
			cv.put("title", month);
			db.insert("MonthTitle", null, cv);
		} // for

		for(String category : strCategories)
		{
			cv.clear();
			cv.put( Category.NAME, category );
			db.insert(Category.TABLE_NAME, null, cv);
		}

		
		String[] reasons = new String[] {"test_1", "test_2", "test_4", "test_5", "test_6", "test_7",   "test_8"};
		double[] prices = new double[]  {100, 		500,	 30000,		 5000,	   300000,	 1000000, 10000000};
		int[] quants = new int[]		{2,			2,		 1,			 2,		   1,		 1,		  1};
		int[] types = new int[]			{1,			0,		 1,			 0,		   1,		 0,		  1};
		int[] categories = new int[]    {1,			1,		 1,			 1,		   1,		 1, 	  1};
		String[] dates = new String[]	{"2012-08-27", "2012-08-25", "2012-08-01", "2012-08-03", "2012-07-15", "2012-07-14", "2011-08-27"};
		
		for(int i = 0; i < reasons.length; ++i)
		{
			cv.clear();
			cv.put(Finance.REASON, reasons[i]);
			cv.put(Finance.PRICE, prices[i]);
			cv.put(Finance.QUANTITY, quants[i]);
			cv.put(Finance.TYPE, types[i]);
			cv.put(Finance.DATE, dates[i]);
			cv.put(Finance.CATEGORY, categories[i]);
			db.insert(Finance.TABLE_NAME, null, cv);
		} // for
	} // onCreate

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{

	} // onUpgrade

} // DBHelper

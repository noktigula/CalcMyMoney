package ru.nstudio.android.Storage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by noktigula on 12.03.14.
 */
public class MoneyProvider extends ContentProvider
{
	private static Map<String, String> _projectionMapFinance;
	private static Map<String, String> _projectionMapCategory;
	static
	{
		_projectionMapFinance = new HashMap<String, String>(  );
		_projectionMapCategory = new HashMap<String, String>(  );

		_projectionMapFinance.put( MoneyContract.Finance._ID, MoneyContract.Finance._ID);
		_projectionMapFinance.put( MoneyContract.Finance.DATE, MoneyContract.Finance.DATE);
		_projectionMapFinance.put( MoneyContract.Finance.PRICE, MoneyContract.Finance.PRICE );
		_projectionMapFinance.put( MoneyContract.Finance.QUANTITY, MoneyContract.Finance.QUANTITY );
		_projectionMapFinance.put( MoneyContract.Finance.REASON, MoneyContract.Finance.REASON );
		_projectionMapFinance.put( MoneyContract.Finance.TYPE, MoneyContract.Finance.TYPE );
		_projectionMapFinance.put( MoneyContract.Finance.CATEGORY, MoneyContract.Finance.CATEGORY );

		_projectionMapCategory.put( MoneyContract.Category._ID, MoneyContract.Category._ID );
		_projectionMapCategory.put( MoneyContract.Category.NAME, MoneyContract.Category.NAME );
	}

	private static final UriMatcher _uriMatcher;
	private static final int INCOMING_OPERATION_COLLECTION_INDICATOR = 1;
	private static final int INCOMING_OPERATION_ITEM_INDICATOR = 2;
	private static final int INCOMING_CATEGORY_COLLECTION_INDICATOR = 3;
	private static final int INCOMING_CATEGORY_ITEM_INDICATOR = 4;
	private static final int INCOMING_MONTH_OVERVIEW_COLLECTION_INDICATOR = 5;
	private static final int INCOMING_MONTH_OVERVIEW_ITEM_INDICATOR = 6;
	static
	{
		_uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );

		_uriMatcher.addURI( MoneyContract.AUTHORITY, MoneyContract.Finance.TABLE_NAME,
				INCOMING_OPERATION_COLLECTION_INDICATOR );
		_uriMatcher.addURI( MoneyContract.AUTHORITY, MoneyContract.Finance.TABLE_NAME + "/#",
				INCOMING_OPERATION_ITEM_INDICATOR );

		_uriMatcher.addURI( MoneyContract.AUTHORITY, MoneyContract.Category.TABLE_NAME,
				INCOMING_CATEGORY_COLLECTION_INDICATOR );
		_uriMatcher.addURI( MoneyContract.AUTHORITY, MoneyContract.Category.TABLE_NAME + "/#",
				INCOMING_CATEGORY_ITEM_INDICATOR );
	}

	private static class DBHelper extends SQLiteOpenHelper
	{
		public DBHelper(Context context)
		{
			super(context, MoneyContract.DATABASE_NAME, null, MoneyContract.DATABASE_VERSION);
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

			String queryCreateFinance = new String("CREATE TABLE " + MoneyContract.Finance.TABLE_NAME + "( " +
					MoneyContract.Finance._ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					MoneyContract.Finance.REASON   + " TEXT 	 NOT NULL, " +
					MoneyContract.Finance.PRICE    + " REAL 	 NOT NULL, " +
					MoneyContract.Finance.QUANTITY + " INTEGER NOT NULL, " +
					MoneyContract.Finance.TYPE     + " INTEGER NOT NULL, " +
					MoneyContract.Finance.DATE     + " TEXT NOT NULL," +
					MoneyContract.Finance.CATEGORY + " INTEGER NOT NULL)");

			String queryCreateMonthTitle = new String("CREATE TABLE MonthTitle( " +
					"idMonthTitle INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"title		STRING NOT NULL)");

			String queryCreateCategoryTable = new String("CREATE TABLE " + MoneyContract.Category.TABLE_NAME + "(" +
					MoneyContract.Category._ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					MoneyContract.Category.NAME  + " TEXT NOT NULL)");

			String queryCreateYearOperationsView = new String( "CREATE VIEW IF NOT EXISTS " + MoneyContract.ViewYear.VIEW_NAME + " AS" +
					"SELECT " +
					MoneyContract.Finance._ID +
					"strftime('%Y', " + MoneyContract.Finance.DATE + ") AS " + MoneyContract.ViewYear.YEAR +", " +
					"strftime('%m', " + MoneyContract.Finance.DATE + ") AS " + MoneyContract.ViewYear.MONTH +", " +
					"mt.title, IFNULL(tmp1.plus, 0) AS " + MoneyContract.ViewYear.INCOME +", " +
					"IFNULL(tmp2.minus, 0) AS " + MoneyContract.ViewYear.EXPEND +", " +
					"(IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) AS " + MoneyContract.ViewYear.TOTAL + " " +
					"FROM " + MoneyContract.Finance.TABLE_NAME + " AS f " +
					"INNER JOIN MonthTitle AS mt ON mt.idMonthTitle = " + MoneyContract.ViewYear.MONTH + " " +
					"LEFT JOIN (SELECT strftime('%m', " + MoneyContract.Finance.DATE + ") AS month, strftime('%Y', " +
						MoneyContract.Finance.DATE + ") AS year, " +
					"SUM( " + MoneyContract.Finance.PRICE + "*" + MoneyContract.Finance.QUANTITY + ") AS plus " +
					"FROM " + MoneyContract.Finance.TABLE_NAME + " " +
					"WHERE " + MoneyContract.Finance.TYPE + " = 1 " +
					"GROUP BY year, month) AS tmp1 ON tmp1.month = fmonth AND tmp1.year = fyear " +
					"LEFT JOIN (SELECT strftime('%m', " + MoneyContract.Finance.DATE +") AS month, strftime('%Y',"+ MoneyContract.Finance.DATE +") AS year, " +
					"SUM("+MoneyContract.Finance.PRICE+"*"+MoneyContract.Finance.QUANTITY + ") AS minus " +
					"FROM "+MoneyContract.Finance.TABLE_NAME+" " +
					"WHERE " + MoneyContract.Finance.TYPE + " = 0 " +
					"GROUP BY year, month) AS tmp2 ON tmp2.month = fmonth AND tmp2.year = fyear " +
					"GROUP BY strftime('%Y', " + MoneyContract.Finance.DATE+"), strftime('%m', " + MoneyContract.Finance.DATE + "), mt.title, " +
					"tmp1.plus, tmp2.minus, (IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) " +
					"ORDER BY fyear, fmonth" );

			db.execSQL(queryCreateFinance);
			db.execSQL(queryCreateMonthTitle);
			db.execSQL(queryCreateCategoryTable);
			db.execSQL( queryCreateYearOperationsView );

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
				cv.put( MoneyContract.Category.NAME, category );
				db.insert( MoneyContract.Category.TABLE_NAME, null, cv);
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
				cv.put( MoneyContract.Finance.REASON, reasons[i]);
				cv.put( MoneyContract.Finance.PRICE, prices[i]);
				cv.put( MoneyContract.Finance.QUANTITY, quants[i]);
				cv.put( MoneyContract.Finance.TYPE, types[i]);
				cv.put( MoneyContract.Finance.DATE, dates[i]);
				cv.put( MoneyContract.Finance.CATEGORY, categories[i]);
				db.insert( MoneyContract.Finance.TABLE_NAME, null, cv);
			} // for
		} // onCreate

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
		{

		} // onUpgrade

		public boolean isValueUnique(  String tableName, String field, String value )
		{
			SQLiteDatabase db = getWritableDatabase();
			Cursor c = db.query( tableName, new String[]{field}, field + " = ?", new String[]{value}, null, null, null );
			return c.getCount() == 0;
		}

		public void insertDistinct( String tableName, String field, String value )
		{
			if( !isValueUnique( tableName, field, value ) )	return;

			ContentValues cv = new ContentValues(  );
			cv.put( field, value );

			SQLiteDatabase db = getWritableDatabase();
			db.insert( tableName, null, cv );
		}
	} // DBHelper

	private DBHelper _dbHelper;

	@Override
	public boolean onCreate()
	{
		_dbHelper = new DBHelper( getContext() );
		return true;
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,
						 String sortOrder )
	{
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String orderBy;

		switch( _uriMatcher.match( uri ) )
		{
			case INCOMING_OPERATION_COLLECTION_INDICATOR:
			{
				qb.setTables( MoneyContract.Finance.TABLE_NAME );
				qb.setProjectionMap( _projectionMapFinance );
				orderBy = TextUtils.isEmpty( sortOrder )
									? MoneyContract.Finance.DEFAULT_SORT_ORDER
									: sortOrder;
				break;
			}

			case INCOMING_OPERATION_ITEM_INDICATOR:
			{
				qb.setTables( MoneyContract.Finance.TABLE_NAME );
				qb.setProjectionMap( _projectionMapFinance );
				qb.appendWhere( MoneyContract.Finance._ID + " = " + uri.getPathSegments().get( 1 ) );
				orderBy = TextUtils.isEmpty( sortOrder )
						? MoneyContract.Category.DEFAULT_SORT_ORDER
						: sortOrder;
				break;
			}

			case INCOMING_CATEGORY_COLLECTION_INDICATOR:
			{
				qb.setTables( MoneyContract.Category.TABLE_NAME );
				qb.setProjectionMap( _projectionMapCategory );
				orderBy = TextUtils.isEmpty( sortOrder )
						? MoneyContract.Category.DEFAULT_SORT_ORDER
						: sortOrder;
				break;
			}

			case INCOMING_CATEGORY_ITEM_INDICATOR:
			{
				qb.setTables( MoneyContract.Category.TABLE_NAME );
				qb.setProjectionMap( _projectionMapCategory );
				qb.appendWhere( MoneyContract.Category._ID + " = " + uri.getPathSegments().get( 1 ) );
				orderBy = TextUtils.isEmpty( sortOrder )
						? MoneyContract.Category.DEFAULT_SORT_ORDER
						: sortOrder;
				break;
			}

			default: throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		Cursor c = qb.query( db, projection, selection, selectionArgs, null, null, orderBy );
		c.setNotificationUri( getContext().getContentResolver(), uri );
		return c;
	}

	@Override
	public String getType( Uri uri )
	{
		switch( _uriMatcher.match( uri ) )
		{
			case INCOMING_OPERATION_COLLECTION_INDICATOR:
				return MoneyContract.Finance.CONTENT_TYPE;
			case INCOMING_OPERATION_ITEM_INDICATOR:
				return MoneyContract.Finance.CONTENT_ITEM_TYPE;
			case INCOMING_CATEGORY_COLLECTION_INDICATOR:
				return MoneyContract.Category.CONTENT_TYPE;
			case INCOMING_CATEGORY_ITEM_INDICATOR:
				return MoneyContract.Category.CONTENT_ITEM_TYPE;
			default: throw new IllegalArgumentException( "Unknown URI: " + uri );
		}
	}

	private void checkInsertValues( ContentValues values, Set<String> checkValues )
	{
		final String initialErrorMessage = "Can't insert data - some fields are missed: ";
		StringBuilder errorBuilder = new StringBuilder( initialErrorMessage );
		for( String key : checkValues )
		{
			if( !values.containsKey( key ) )
			{
				if( key.equals( MoneyContract.Finance._ID ) ) continue;

				errorBuilder.append( key + ", " );
			}
		}
		if( !errorBuilder.toString().equals( initialErrorMessage ) )
		{
			throw new IllegalArgumentException( errorBuilder.toString() );
		}
	}

	@Override
	public Uri insert( Uri uri, ContentValues initialValues )
	{
		if( _uriMatcher.match( uri ) != INCOMING_OPERATION_COLLECTION_INDICATOR &&
			_uriMatcher.match( uri ) != INCOMING_CATEGORY_COLLECTION_INDICATOR )
		{
			throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

		if( initialValues == null )
		{
			throw new IllegalArgumentException( "No values to insert" );
		}

		String tableName;
		Uri contentUri;
		if( _uriMatcher.match( uri ) == INCOMING_OPERATION_COLLECTION_INDICATOR )
		{
			tableName = MoneyContract.Finance.TABLE_NAME;
			contentUri = MoneyContract.Finance.CONTENT_URI;
			checkInsertValues( initialValues, _projectionMapFinance.keySet() );
		}
		else
		{
			tableName = MoneyContract.Category.TABLE_NAME;
			contentUri = MoneyContract.Category.CONTENT_URI;
			checkInsertValues( initialValues, _projectionMapCategory.keySet() );
		}

		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		long rowId = db.insert( tableName, null, initialValues );
		if( rowId > 0 )
		{
			Uri insertedValueUri = ContentUris.withAppendedId( contentUri, rowId );
			getContext().getContentResolver().notifyChange( insertedValueUri, null );

			return insertedValueUri;
		}

		throw new RuntimeException("OOPS! Can't insert data");
	}

	@Override
	public int delete( Uri uri, String where, String[] whereArgs )
	{
		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		int count;
		switch( _uriMatcher.match( uri ) )
		{
			case INCOMING_OPERATION_ITEM_INDICATOR:
			{
				String rowId = uri.getPathSegments().get(1);
				count = db.delete( MoneyContract.Finance.TABLE_NAME,
						MoneyContract.Finance._ID + "=" + rowId +
						(!TextUtils.isEmpty( where ) ? " AND (" + where + ")" : "" ), whereArgs );
				break;
			}

			case INCOMING_OPERATION_COLLECTION_INDICATOR:
			{
				count = db.delete( MoneyContract.Finance.TABLE_NAME, where, whereArgs );
				break;
			}

			case INCOMING_CATEGORY_ITEM_INDICATOR:
			{
				String rowId = uri.getPathSegments().get(1);
				count = db.delete( MoneyContract.Category.TABLE_NAME,
						MoneyContract.Category._ID + " = " + rowId +
						(!TextUtils.isEmpty( where ) ? " AND ( " + where + ")" : ""), whereArgs);
				break;
			}

			case INCOMING_CATEGORY_COLLECTION_INDICATOR:
			{
				count = db.delete( MoneyContract.Category.TABLE_NAME, where, whereArgs );
				break;
			}
			default: throw new IllegalArgumentException("Unknonw URI: " + uri);
		}

		getContext().getContentResolver().notifyChange( uri, null );
		return count;
	}

	@Override
	public int update( Uri uri, ContentValues initialValues, String where, String[] whereArgs )
	{
		if( initialValues == null ) throw new IllegalArgumentException( "No values to update" );

		SQLiteDatabase db = _dbHelper.getWritableDatabase();
		int count;
		switch( _uriMatcher.match( uri ) )
		{
			case INCOMING_OPERATION_ITEM_INDICATOR:
			{
				String rowId = uri.getPathSegments().get( 1 );
				count = db.update( MoneyContract.Finance.TABLE_NAME, initialValues,
						MoneyContract.Finance._ID + "=" + rowId +
						(!TextUtils.isEmpty( where ) ? " AND (" + where + ")" : ""), whereArgs);
				break;
			}

			case INCOMING_OPERATION_COLLECTION_INDICATOR:
			{
				count = db.update( MoneyContract.Finance.TABLE_NAME, initialValues, where, whereArgs );
				break;
			}

			case INCOMING_CATEGORY_ITEM_INDICATOR:
			{
				String rowId = uri.getPathSegments().get(1);
				count = db.update( MoneyContract.Category.TABLE_NAME, initialValues,
						MoneyContract.Category._ID + "=" + rowId +
						(!TextUtils.isEmpty( where ) ? " AND (" + where + ")" : ""), whereArgs);
				break;
			}

			case INCOMING_CATEGORY_COLLECTION_INDICATOR:
			{
				count = db.update( MoneyContract.Category.TABLE_NAME, initialValues, where, whereArgs );
				break;
			}

			default: throw new IllegalArgumentException( "Unknown URI: " + uri );
		}

		getContext().getContentResolver().notifyChange( uri, null );
		return count;
	}
}

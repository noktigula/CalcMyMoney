package ru.nstudio.android.Storage;import android.content.ContentProvider;import android.content.ContentUris;import android.content.ContentValues;import android.content.Context;import android.content.UriMatcher;import android.database.Cursor;import android.database.sqlite.SQLiteDatabase;import android.database.sqlite.SQLiteOpenHelper;import android.database.sqlite.SQLiteQueryBuilder;import android.net.Uri;import android.text.TextUtils;import java.util.ArrayList;import java.util.Arrays;import java.util.HashMap;import java.util.List;import java.util.Map;import java.util.Set;/** * Created by noktigula on 12.03.14. */public class MoneyProvider extends ContentProvider{	private static Map<String, String> m_projectionMapFinance;	private static Map<String, String> m_projectionMapCategory;	private static Map<String, String> m_projectionMapViewYear;	static	{		m_projectionMapFinance = new HashMap<String, String>(  );		m_projectionMapCategory = new HashMap<String, String>(  );		m_projectionMapViewYear = new HashMap<String, String>(  );		m_projectionMapFinance.put( MoneyContract.Finance._ID, MoneyContract.Finance._ID );		m_projectionMapFinance.put( MoneyContract.Finance.DATE, MoneyContract.Finance.DATE );		m_projectionMapFinance.put( MoneyContract.Finance.PRICE, MoneyContract.Finance.PRICE );		m_projectionMapFinance.put( MoneyContract.Finance.REASON, MoneyContract.Finance.REASON );		m_projectionMapFinance.put( MoneyContract.Finance.TYPE, MoneyContract.Finance.TYPE );		m_projectionMapFinance.put( MoneyContract.Finance.CATEGORY, MoneyContract.Finance.CATEGORY );        m_projectionMapFinance.put( MoneyContract.Finance.QUANTITY, MoneyContract.Finance.QUANTITY );		m_projectionMapCategory.put( MoneyContract.Category._ID, MoneyContract.Category._ID );		m_projectionMapCategory.put( MoneyContract.Category.NAME, MoneyContract.Category.NAME );		m_projectionMapViewYear.put( MoneyContract.ViewYear._ID, MoneyContract.ViewYear._ID );		m_projectionMapViewYear.put( MoneyContract.ViewYear.MONTH, MoneyContract.ViewYear.MONTH );		m_projectionMapViewYear.put( MoneyContract.ViewYear.YEAR, MoneyContract.ViewYear.YEAR );		m_projectionMapViewYear.put( MoneyContract.ViewYear.TOTAL, MoneyContract.ViewYear.TOTAL );		m_projectionMapViewYear.put( MoneyContract.ViewYear.EXPEND, MoneyContract.ViewYear.EXPEND );		m_projectionMapViewYear.put( MoneyContract.ViewYear.INCOME, MoneyContract.ViewYear.INCOME );		m_projectionMapViewYear.put( MoneyContract.ViewYear.MONTH_TITLE, MoneyContract.ViewYear.MONTH_TITLE );	}	private static final UriMatcher _uriMatcher;	private static final int INCOMING_OPERATION_COLLECTION_INDICATOR = 1;	private static final int INCOMING_OPERATION_ITEM_INDICATOR = 2;	private static final int INCOMING_CATEGORY_COLLECTION_INDICATOR = 3;	private static final int INCOMING_CATEGORY_ITEM_INDICATOR = 4;	private static final int INCOMING_MONTH_OVERVIEW_COLLECTION_INDICATOR = 5;	private static final int INCOMING_MONTH_OVERVIEW_ITEM_INDICATOR = 6;	private static final int INCOMING_MONTH_CATEGORY_COLLECTION_INDICATOR = 7;	private static final int INCOMING_MONTH_CATEGORY_ITEM_INDICATOR = 8;	private static final int INCOMING_MONTH_OPERATION_COLLECTION_INDICATOR = 9;	private static final int INCOMING_MONTH_OPERATION_ITEM_INDICATOR = 10;	static	{		_uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "finance",				INCOMING_OPERATION_COLLECTION_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "finance/#",				INCOMING_OPERATION_ITEM_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "category",				INCOMING_CATEGORY_COLLECTION_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "category/#",				INCOMING_CATEGORY_ITEM_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "monthOverview",				INCOMING_MONTH_OVERVIEW_COLLECTION_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "monthOverview/#",				INCOMING_MONTH_OVERVIEW_ITEM_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "monthCategory",				INCOMING_MONTH_CATEGORY_COLLECTION_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "monthCategory/#",				INCOMING_MONTH_CATEGORY_ITEM_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "monthOperation",				INCOMING_MONTH_OPERATION_COLLECTION_INDICATOR );		_uriMatcher.addURI( MoneyContract.AUTHORITY, "monthOperation/#",				INCOMING_MONTH_OPERATION_ITEM_INDICATOR );	}	private static class DBHelper extends SQLiteOpenHelper	{		public static final String DEFAULT_CATEGORY = "Нет категории";		public DBHelper(Context context)		{			super(context, MoneyContract.DATABASE_NAME, null, MoneyContract.DATABASE_VERSION);		} // DBHelper		@Override		public void onCreate(SQLiteDatabase db)		{			String[] strArrMonth = new String[]{"Январь", "Февраль",					"Март",   "Апрель", "Май",					"Июнь",   "Июль",	"Август",					"Сентябрь", "Октябрь", "Ноябрь",					"Декабрь"};			String[] strCategories= new String[]{ DEFAULT_CATEGORY, "Транспорт", "Продукты", "Развлечения"};			String queryCreateFinance = new String("CREATE TABLE " + MoneyContract.Finance.TABLE_NAME + "( " +					MoneyContract.Finance._ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +					MoneyContract.Finance.REASON   + " TEXT 	 NOT NULL, " +					MoneyContract.Finance.PRICE    + " REAL 	 NOT NULL, " +					MoneyContract.Finance.QUANTITY + " INTEGER NOT NULL, " +					MoneyContract.Finance.TYPE     + " INTEGER NOT NULL, " +					MoneyContract.Finance.DATE     + " TEXT NOT NULL," +					MoneyContract.Finance.CATEGORY + " INTEGER NOT NULL)");			String queryCreateMonthTitle = new String("CREATE TABLE MonthTitle( " +					"idMonthTitle INTEGER PRIMARY KEY AUTOINCREMENT, " +					"title		STRING NOT NULL)");			String queryCreateCategoryTable = new String("CREATE TABLE " + MoneyContract.Category.TABLE_NAME + "(" +					MoneyContract.Category._ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +					MoneyContract.Category.NAME  + " TEXT NOT NULL)");			String queryCreateYearOperationsView = new String( "CREATE VIEW IF NOT EXISTS " + MoneyContract.ViewYear.VIEW_NAME + " AS " +					"SELECT " +					MoneyContract.Finance._ID + ", " +					"strftime('%Y', " + MoneyContract.Finance.DATE + ") AS " + MoneyContract.ViewYear.YEAR +", " +					"strftime('%m', " + MoneyContract.Finance.DATE + ") AS " + MoneyContract.ViewYear.MONTH +", " +					"mt.title AS " + MoneyContract.ViewYear.MONTH_TITLE +					", IFNULL(tmp1.plus, 0) AS " + MoneyContract.ViewYear.INCOME +", " +					"IFNULL(tmp2.minus, 0) AS " + MoneyContract.ViewYear.EXPEND +", " +					"(IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) AS " + MoneyContract.ViewYear.TOTAL + " " +					"FROM " + MoneyContract.Finance.TABLE_NAME + " AS f " +					"INNER JOIN MonthTitle AS mt ON mt.idMonthTitle = " + MoneyContract.ViewYear.MONTH + " " +					"LEFT JOIN (SELECT strftime('%m', " + MoneyContract.Finance.DATE + ") AS month, strftime('%Y', " +						MoneyContract.Finance.DATE + ") AS year, " +					"SUM( " + MoneyContract.Finance.PRICE + "*" + MoneyContract.Finance.QUANTITY + ") AS plus " +					"FROM " + MoneyContract.Finance.TABLE_NAME + " " +					"WHERE " + MoneyContract.Finance.TYPE + " = 1 " +					"GROUP BY year, month) AS tmp1 ON tmp1.month = fmonth AND tmp1.year = fyear " +					"LEFT JOIN (SELECT strftime('%m', " + MoneyContract.Finance.DATE +") AS month, strftime('%Y',"+ MoneyContract.Finance.DATE +") AS year, " +					"SUM("+MoneyContract.Finance.PRICE+"*"+MoneyContract.Finance.QUANTITY + ") AS minus " +					"FROM "+MoneyContract.Finance.TABLE_NAME+" " +					"WHERE " + MoneyContract.Finance.TYPE + " = 0 " +					"GROUP BY year, month) AS tmp2 ON tmp2.month = fmonth AND tmp2.year = fyear " +					"GROUP BY strftime('%Y', " + MoneyContract.Finance.DATE+"), strftime('%m', " + MoneyContract.Finance.DATE + "), mt.title, " +					"tmp1.plus, tmp2.minus, (IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) " );			db.execSQL(queryCreateFinance);			db.execSQL(queryCreateMonthTitle);			db.execSQL(queryCreateCategoryTable);			db.execSQL(queryCreateYearOperationsView);            ContentValues cv = new ContentValues();            for (String month : strArrMonth)            {                cv.clear();                cv.put("title", month);                db.insert("MonthTitle", null, cv);            }            for(String category : strCategories)            {                cv.clear();                cv.put( MoneyContract.Category.NAME, category );                db.insert( MoneyContract.Category.TABLE_NAME, null, cv);            }        }		@Override		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)		{		}	}	private DBHelper m_dbHelper;	@Override	public boolean onCreate()	{		m_dbHelper = new DBHelper( getContext() );		return true;	}	private Cursor simpleQuery( Uri uri, String[] projection, String selection, String[] selectionArgs,								String sortOrder )	{		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();		String orderBy;		switch( _uriMatcher.match( uri ) )		{			case INCOMING_OPERATION_COLLECTION_INDICATOR:			{				qb.setTables( MoneyContract.Finance.TABLE_NAME );				qb.setProjectionMap( m_projectionMapFinance );				orderBy = TextUtils.isEmpty( sortOrder )						? MoneyContract.Finance.DEFAULT_SORT_ORDER						: sortOrder;				break;			}			case INCOMING_OPERATION_ITEM_INDICATOR:			{				qb.setTables( MoneyContract.Finance.TABLE_NAME );				qb.setProjectionMap( m_projectionMapFinance );				qb.appendWhere( MoneyContract.Finance._ID + " = " + uri.getPathSegments().get( 1 ) );				orderBy = TextUtils.isEmpty( sortOrder )						? MoneyContract.Category.DEFAULT_SORT_ORDER						: sortOrder;				break;			}			case INCOMING_CATEGORY_COLLECTION_INDICATOR:			{				qb.setTables( MoneyContract.Category.TABLE_NAME );				qb.setProjectionMap( m_projectionMapCategory );				orderBy = TextUtils.isEmpty( sortOrder )						? MoneyContract.Category.DEFAULT_SORT_ORDER						: sortOrder;				break;			}			case INCOMING_CATEGORY_ITEM_INDICATOR:			{				qb.setTables( MoneyContract.Category.TABLE_NAME );				qb.setProjectionMap( m_projectionMapCategory );				qb.appendWhere( MoneyContract.Category._ID + " = " + uri.getPathSegments().get( 1 ) );				orderBy = TextUtils.isEmpty( sortOrder )						? MoneyContract.Category.DEFAULT_SORT_ORDER						: sortOrder;				break;			}			case INCOMING_MONTH_OVERVIEW_COLLECTION_INDICATOR:			{				qb.setTables( MoneyContract.ViewYear.VIEW_NAME );				qb.setProjectionMap( m_projectionMapViewYear );				orderBy = TextUtils.isEmpty( sortOrder )						? MoneyContract.ViewYear.DEFAULT_SORT_ORDER						: sortOrder;				break;			}			case INCOMING_MONTH_OVERVIEW_ITEM_INDICATOR:			{				qb.setTables( MoneyContract.ViewYear.VIEW_NAME );				qb.setProjectionMap( m_projectionMapViewYear );				qb.appendWhere( MoneyContract.ViewYear._ID + " = " + uri.getPathSegments().get(1) );				orderBy = TextUtils.isEmpty( sortOrder )						? MoneyContract.ViewYear.DEFAULT_SORT_ORDER						: sortOrder;				break;			}			default: throw new IllegalArgumentException( "Unknown URI: " + uri );		}		SQLiteDatabase db = m_dbHelper.getWritableDatabase();		Cursor c = qb.query( db, projection, selection, selectionArgs, null, null, orderBy );		c.setNotificationUri( getContext().getContentResolver(), uri );		return c;	}	private String getWhereClauseForId( String selection, String id, String tableProjection )	{		String whereClause = TextUtils.isEmpty( selection )				? tableProjection+"._id = " + id				: selection + " AND "+tableProjection+"._id = " + id;		return whereClause;	}	private String getCategoryQuery(boolean isIncome, String selection)	{		String typeCondition = isIncome ? "1" : "0";		return "SELECT IFNULL(c." + MoneyContract.Category._ID + ", -1) " +				"AS " + MoneyContract.ViewMonthCategories._ID + ", " +				"c." + MoneyContract.Category.NAME + " " +				"AS " + MoneyContract.ViewMonthCategories.CATEGORY_TITLE + ", " +				"SUM( " + MoneyContract.Finance.PRICE + " * " + MoneyContract.Finance.QUANTITY + " ) AS " +					MoneyContract.ViewMonthCategories.CATEGORY_SUM +", " +				"f." + MoneyContract.Finance.TYPE + " AS " + MoneyContract.ViewMonthCategories.CATEGORY_TYPE + " " +				"FROM " + MoneyContract.Finance.TABLE_NAME + " AS f " +				"LEFT JOIN " + MoneyContract.Category.TABLE_NAME + " AS c " +				"ON f." + MoneyContract.Finance.CATEGORY + " = c." + MoneyContract.Category._ID + " " +				"WHERE " + selection +				" AND " + MoneyContract.Finance.TYPE + " = " + typeCondition + " " +				"GROUP BY c." + MoneyContract.Category.NAME;	}	private Cursor complexQuery( Uri uri, String[] projection, String selection, String[] selectionArgs,								 String sortOrder )	{		String rawQuery;		switch( _uriMatcher.match( uri ) )		{			case INCOMING_MONTH_CATEGORY_COLLECTION_INDICATOR:			{				String[] tmp = Arrays.copyOf( selectionArgs, selectionArgs.length*2 );				System.arraycopy( selectionArgs, 0, tmp, selectionArgs.length, selectionArgs.length );				selectionArgs = tmp;				rawQuery = getCategoryQuery( true, selection ) + " UNION "						 + getCategoryQuery( false, selection ) + " "						 + "ORDER BY " + MoneyContract.ViewMonthCategories.CATEGORY_TYPE  + " DESC, " + MoneyContract.ViewMonthCategories._ID;				break;			}			case INCOMING_MONTH_CATEGORY_ITEM_INDICATOR:			{				String whereClause = getWhereClauseForId( selection, uri.getPathSegments().get( 1 ), "c" );				rawQuery = "SELECT IFNULL(c." + MoneyContract.Category._ID + ", -1) " +							" AS " + MoneyContract.ViewMonthCategories._ID + ", " +						"IFNULL(c." + MoneyContract.Category.NAME + ", \"Дебаг\")" +							" AS " + MoneyContract.ViewMonthCategories.CATEGORY_TITLE + ", " +						"SUM( " + MoneyContract.Finance.PRICE + " * " + MoneyContract.Finance.QUANTITY + " ) " +							"AS " + MoneyContract.ViewMonthCategories.CATEGORY_SUM + ", " +						"FROM " + MoneyContract.Finance.TABLE_NAME + " AS f " +						"LEFT JOIN " + MoneyContract.Category.TABLE_NAME + " AS c " +						"ON f." + MoneyContract.Finance.CATEGORY + " = c." + MoneyContract.Category._ID + " " +						"WHERE " + whereClause +						"GROUP BY c." + MoneyContract.Category.NAME + " " +						"ORDER BY c." + MoneyContract.Category._ID;				break;			}			case INCOMING_MONTH_OPERATION_COLLECTION_INDICATOR:			{				rawQuery = "SELECT " +						"f." + MoneyContract.Finance._ID + " " +							"AS " + MoneyContract.ViewMonthOperations._ID + ", " +						"f." + MoneyContract.Finance.REASON + " " +							"AS " + MoneyContract.ViewMonthOperations.REASON + ", " +						"f." + MoneyContract.Finance.PRICE 	+ " " +							"AS " + MoneyContract.ViewMonthOperations.PRICE + ", " +						"f." + MoneyContract.Finance.DATE + " " +							"AS " + MoneyContract.ViewMonthOperations.DATE + ", " +                        "f." + MoneyContract.Finance.QUANTITY + " " +                            "AS " + MoneyContract.ViewMonthOperations.QUANTITY + ", " +						"f." + MoneyContract.Finance.TYPE + " " +							"AS " + MoneyContract.ViewMonthOperations.TYPE + ", " +						"IFNULL(c." + MoneyContract.Category.NAME + ", \"Дебаг\") " +							"AS " + MoneyContract.ViewMonthOperations.CATEGORY_NAME + " " +						" FROM " + MoneyContract.Finance.TABLE_NAME + " AS f " +						" LEFT JOIN " + MoneyContract.Category.TABLE_NAME +  " AS c " +						" ON f." + MoneyContract.Finance.CATEGORY + " = c." + MoneyContract.Category._ID + " " +						" WHERE " + selection + " " +						" ORDER BY strftime('%d', " + MoneyContract.Finance.DATE + ") DESC, " + MoneyContract.Finance._ID + " ASC";				break;			}			case INCOMING_MONTH_OPERATION_ITEM_INDICATOR:			{				String whereClause = getWhereClauseForId( selection, uri.getPathSegments().get( 1 ), "f" );				rawQuery = "SELECT " +						"f." + MoneyContract.Finance._ID + " " +						    "AS " + MoneyContract.ViewMonthOperations._ID + ", " +						"f." + MoneyContract.Finance.REASON + " " +						    "AS " + MoneyContract.ViewMonthOperations.REASON + ", " +						"f." + MoneyContract.Finance.PRICE 	+ " " +						    "AS " + MoneyContract.ViewMonthOperations.PRICE + ", " +						"f." + MoneyContract.Finance.DATE + " " +						    "AS " + MoneyContract.ViewMonthOperations.DATE + ", " +                        "f." + MoneyContract.Finance.QUANTITY + " " +                            "AS " + MoneyContract.ViewMonthOperations.QUANTITY + ", " +						"f." + MoneyContract.Finance.TYPE + " " +						    "AS " + MoneyContract.ViewMonthOperations.TYPE + ", " +						"IFNULL(c." + MoneyContract.Category.NAME + ", \"Дебаг\") " +						"AS " + MoneyContract.ViewMonthOperations.CATEGORY_NAME + " " +						" FROM " + MoneyContract.Finance.TABLE_NAME + " AS f " +						" LEFT JOIN " + MoneyContract.Category.TABLE_NAME +  " AS c " +						" ON f." + MoneyContract.Finance.CATEGORY + " = c." + MoneyContract.Category._ID + " " +						"WHERE " + whereClause + " " +						" ORDER BY strftime('%d', " + MoneyContract.Finance.DATE + "), " + MoneyContract.Finance._ID;				break;			}			default: throw new RuntimeException( "Unknown URI: " + uri );		}		Cursor c = m_dbHelper.getWritableDatabase().rawQuery( rawQuery, selectionArgs );		c.setNotificationUri( getContext().getContentResolver(), uri );		return c;	}	@Override	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,						 String sortOrder )	{		switch( _uriMatcher.match( uri ) )		{			case INCOMING_CATEGORY_COLLECTION_INDICATOR:			case INCOMING_CATEGORY_ITEM_INDICATOR:			case INCOMING_OPERATION_COLLECTION_INDICATOR:			case INCOMING_OPERATION_ITEM_INDICATOR:			case INCOMING_MONTH_OVERVIEW_COLLECTION_INDICATOR:			case INCOMING_MONTH_OVERVIEW_ITEM_INDICATOR:			{				return simpleQuery( uri, projection, selection, selectionArgs, sortOrder );			}			case INCOMING_MONTH_CATEGORY_COLLECTION_INDICATOR:			case INCOMING_MONTH_CATEGORY_ITEM_INDICATOR:			case INCOMING_MONTH_OPERATION_COLLECTION_INDICATOR:			case INCOMING_MONTH_OPERATION_ITEM_INDICATOR:			{				return complexQuery( uri, projection, selection, selectionArgs, sortOrder );			}			default: throw new RuntimeException( "Unknown URI: " + uri );		}	}	@Override	public String getType( Uri uri )	{		switch( _uriMatcher.match( uri ) )		{			case INCOMING_OPERATION_COLLECTION_INDICATOR:				return MoneyContract.Finance.CONTENT_TYPE;			case INCOMING_OPERATION_ITEM_INDICATOR:				return MoneyContract.Finance.CONTENT_ITEM_TYPE;			case INCOMING_CATEGORY_COLLECTION_INDICATOR:				return MoneyContract.Category.CONTENT_TYPE;			case INCOMING_CATEGORY_ITEM_INDICATOR:				return MoneyContract.Category.CONTENT_ITEM_TYPE;			default: throw new IllegalArgumentException( "Unknown URI: " + uri );		}	}	private void checkInsertValues( ContentValues values, Set<String> checkValues )	{		final String initialErrorMessage = "Can't insert data - some fields are missed: ";		StringBuilder errorBuilder = new StringBuilder( initialErrorMessage );		for( String key : checkValues )		{			if( !values.containsKey( key ) )			{				if( key.equals( MoneyContract.Finance._ID ) || key.equals( MoneyContract.Finance.QUANTITY ) ) continue;				errorBuilder.append( key + ", " );			}		}		if( !errorBuilder.toString().equals( initialErrorMessage ) )		{			throw new IllegalArgumentException( errorBuilder.toString() );		}	}	@Override	public Uri insert( Uri uri, ContentValues initialValues )	{		if( _uriMatcher.match( uri ) != INCOMING_OPERATION_COLLECTION_INDICATOR &&			_uriMatcher.match( uri ) != INCOMING_CATEGORY_COLLECTION_INDICATOR  )		{			throw new IllegalArgumentException( "Unknown URI: " + uri );		}		if( initialValues == null )		{			throw new IllegalArgumentException( "No values to insert" );		}		String tableName;		Uri contentUri;		if( _uriMatcher.match( uri ) == INCOMING_OPERATION_COLLECTION_INDICATOR )		{			tableName = MoneyContract.Finance.TABLE_NAME;			contentUri = MoneyContract.Finance.CONTENT_URI;			checkInsertValues( initialValues, m_projectionMapFinance.keySet() );		}		else		{			tableName = MoneyContract.Category.TABLE_NAME;			contentUri = MoneyContract.Category.CONTENT_URI;			checkInsertValues( initialValues, m_projectionMapCategory.keySet() );		}		SQLiteDatabase db = m_dbHelper.getWritableDatabase();		long rowId = db.insert( tableName, null, initialValues );		if( rowId > 0 )		{			Uri insertedValueUri = ContentUris.withAppendedId( contentUri, rowId );			//getContext().getContentResolver().notifyChange( insertedValueUri, null );			getContext().getContentResolver().notifyChange( contentUri, null );			if( _uriMatcher.match( uri ) == INCOMING_OPERATION_COLLECTION_INDICATOR )			{				getContext().getContentResolver().notifyChange( MoneyContract.ViewMonthOperations.CONTENT_URI, null );				getContext().getContentResolver().notifyChange( MoneyContract.ViewMonthCategories.CONTENT_URI, null );				getContext().getContentResolver().notifyChange( MoneyContract.ViewYear.CONTENT_URI, null );			}			return insertedValueUri;		}		throw new RuntimeException("OOPS! Can't insert data");	}	private int getDefaultCategoryId()	{		SQLiteDatabase db = m_dbHelper.getWritableDatabase();		Cursor c = db.rawQuery( "SELECT " + MoneyContract.Category._ID + " FROM " + MoneyContract.Category.TABLE_NAME +		" WHERE " + MoneyContract.Category.NAME + " = ?", new String[]{ m_dbHelper.DEFAULT_CATEGORY});		if(!c.moveToFirst()) throw new RuntimeException( "Can't get default category" );		if(c.getCount() > 1) throw new RuntimeException( "Ambiguous default category" );		return c.getInt( c.getColumnIndex( MoneyContract.Category._ID ) );	}	@Override	public int delete( Uri uri, String where, String[] whereArgs )	{		SQLiteDatabase db = m_dbHelper.getWritableDatabase();		int count;		switch( _uriMatcher.match( uri ) )		{			case INCOMING_OPERATION_ITEM_INDICATOR:			{				String rowId = uri.getPathSegments().get(1);				count = db.delete( MoneyContract.Finance.TABLE_NAME,						MoneyContract.Finance._ID + "=" + rowId +						(!TextUtils.isEmpty( where ) ? " AND (" + where + ")" : "" ), whereArgs );				break;			}			case INCOMING_OPERATION_COLLECTION_INDICATOR:			{				count = db.delete( MoneyContract.Finance.TABLE_NAME, where, whereArgs );				break;			}			case INCOMING_CATEGORY_ITEM_INDICATOR:			{				String rowId = uri.getPathSegments().get(1);				if( Integer.valueOf( rowId ) == 1 )				{					throw new RuntimeException("Can't delete category \"No category\"");				}				String defaultId = Integer.toString( getDefaultCategoryId() );				db.execSQL( "UPDATE " + MoneyContract.Finance.TABLE_NAME + " SET " +						MoneyContract.Finance.CATEGORY + " = " + defaultId + " WHERE Finance." +						MoneyContract.Finance.CATEGORY + " = ?" , new String[]{ rowId } );				count = db.delete( MoneyContract.Category.TABLE_NAME,						MoneyContract.Category._ID + " = " + rowId, whereArgs);				break;			}			case INCOMING_CATEGORY_COLLECTION_INDICATOR:			{				Cursor c = db.rawQuery( "SELECT * FROM "+MoneyContract.Category.TABLE_NAME +										"WHERE " + where, whereArgs );				if( c.moveToFirst() )				{					ArrayList<String> ids = new ArrayList<String>();					do					{						ids.add( Integer.toString( c.getInt( c.getColumnIndex( MoneyContract.Category._ID ) ) ) );					} while( c.moveToNext() );					c.close();					String defaultId = Integer.toString( getDefaultCategoryId() );					db.execSQL( "UPDATE " + MoneyContract.Finance.TABLE_NAME + " SET " +							MoneyContract.Finance.CATEGORY + " = " + defaultId + " WHERE Finance." +							MoneyContract.Finance.CATEGORY + " = ?" , ids.toArray() );				}				StringBuilder whereBuilder = new StringBuilder( where );				if( !TextUtils.isEmpty( where ) )				{					whereBuilder.append( " AND " );				}				whereBuilder.append( MoneyContract.Category._ID + " != ? " );				List<String> args = new ArrayList<String>( Arrays.asList( whereArgs ) );				args.add( Integer.toString( getDefaultCategoryId() ) );				count = db.delete( MoneyContract.Category.TABLE_NAME, whereBuilder.toString(), (String[])args.toArray() );				break;			}			default: throw new IllegalArgumentException("Unknown URI: " + uri);		}		getContext().getContentResolver().notifyChange( uri, null );		getContext().getContentResolver().notifyChange( MoneyContract.Finance.CONTENT_URI, null );		getContext().getContentResolver().notifyChange( MoneyContract.ViewMonthCategories.CONTENT_URI, null );		getContext().getContentResolver().notifyChange( MoneyContract.ViewMonthOperations.CONTENT_URI, null );		return count;	}	@Override	public int update( Uri uri, ContentValues initialValues, String where, String[] whereArgs )	{		if( initialValues == null ) throw new IllegalArgumentException( "No values to update" );		SQLiteDatabase db = m_dbHelper.getWritableDatabase();		int count;		switch( _uriMatcher.match( uri ) )		{			case INCOMING_OPERATION_ITEM_INDICATOR:			{				String rowId = uri.getPathSegments().get( 1 );				count = db.update( MoneyContract.Finance.TABLE_NAME, initialValues,						MoneyContract.Finance._ID + "=" + rowId +						(!TextUtils.isEmpty( where ) ? " AND (" + where + ")" : ""), whereArgs);				break;			}			case INCOMING_OPERATION_COLLECTION_INDICATOR:			{				count = db.update( MoneyContract.Finance.TABLE_NAME, initialValues, where, whereArgs );				break;			}			case INCOMING_CATEGORY_ITEM_INDICATOR:			{				String rowId = uri.getPathSegments().get(1);				count = db.update( MoneyContract.Category.TABLE_NAME, initialValues,						MoneyContract.Category._ID + "=" + rowId +						(!TextUtils.isEmpty( where ) ? " AND (" + where + ")" : ""), whereArgs);				break;			}			case INCOMING_CATEGORY_COLLECTION_INDICATOR:			{				count = db.update( MoneyContract.Category.TABLE_NAME, initialValues, where, whereArgs );				break;			}			default: throw new IllegalArgumentException( "Unknown URI: " + uri );		}		getContext().getContentResolver().notifyChange( uri, null );		if( _uriMatcher.match( uri ) == INCOMING_OPERATION_ITEM_INDICATOR ||			_uriMatcher.match( uri ) == INCOMING_OPERATION_COLLECTION_INDICATOR )		{			getContext().getContentResolver().notifyChange( MoneyContract.ViewMonthOperations.CONTENT_URI, null );			getContext().getContentResolver().notifyChange( MoneyContract.ViewMonthCategories.CONTENT_URI, null );			getContext().getContentResolver().notifyChange( MoneyContract.ViewYear.CONTENT_URI, null );		}		return count;	}}
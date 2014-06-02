package ru.nstudio.android.Storage;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by noktigula on 13.03.14.
 */
public final class MoneyContract
{
	public static final String AUTHORITY = "ru.nstudio.android.provider.calcmoneyprovider";
	public static final String DATABASE_NAME = "calcmoney.db";
	public static final int DATABASE_VERSION = 1;

	public static final class Finance implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/finance" );
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nstudio.operations";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nstudio.operation";
		public static final String DEFAULT_SORT_ORDER = "_id ASC";

		public static final String TABLE_NAME    = "Finance";
		public static final String REASON   = "reason";
		public static final String PRICE    = "price";
		public static final String QUANTITY = "quantity";
		public static final String DATE     = "financeDate";
		public static final String TYPE     = "type";
		public static final String CATEGORY = "idCategory";
	}

	public static final class Category implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/category" );
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nstudio.categories";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nstudio.category";
		public static final String DEFAULT_SORT_ORDER = "_id ASC";

		public static final String TABLE_NAME = "Category";
		public static final String NAME 	  = "CategoryTitle";
	}

	public static final class ViewYear implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/monthOverview" );
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nstudio.monthOverviews";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nstudio.monthOverview";
		public static final String DEFAULT_SORT_ORDER = " fyear DESC, fmonth DESC";

		public static final String VIEW_NAME = "YearOperationsView";
		public static final String YEAR = "fyear";
		public static final String MONTH = "fmonth";
		public static final String INCOME = "plus";
		public static final String EXPEND = "minus";
		public static final String TOTAL = "diff";
		public static final String MONTH_TITLE = "title";
	}

	//this isn't a real view in database, it uses only for creating complex query
	public static final class ViewMonthCategories implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/monthCategory" );
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nstudio.monthCategories";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nstudio.monthCategory";

		public static final String VIEW_NAME = "ViewMonthCategories";

		public static final String CATEGORY_TITLE = Category.NAME;
		public static final String CATEGORY_SUM = "cost";
	}

	//this isn't a real view in database, it uses only for creating complex query
	public static final class ViewMonthOperations implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/monthOperation" );
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.android.nstudio.monthOperations";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.dir/vnd.android.nstudio.monthOperation";

		public static final String VIEW_NAME = "ViewMonthOperations";

		public static final String REASON = Finance.REASON;
		public static final String PRICE = Finance.PRICE;
		public static final String QUANTITY = Finance.QUANTITY;
		public static final String DATE = Finance.DATE;
		public static final String TYPE = Finance.TYPE;
		public static final String CATEGORY_NAME = Category.NAME;
	}
}

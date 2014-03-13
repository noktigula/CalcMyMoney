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
		public static final String DEFAULT_SORT_ORDER = "_id DESC";

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
		public static final String DEFAULT_SORT_ORDER = "_id DESC";

		public static final String TABLE_NAME = "Category";
		public static final String NAME 	  = "CategoryTitle";
	}

	public static final class ViewYear implements BaseColumns
	{
		public static final Uri CONTENT_URI = Uri.parse( "content://" + AUTHORITY + "/monthOverview" );
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nstudio.monthOverviews";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.nstudio.monthOverview";

		public static final String VIEW_NAME = "YearOperationsView";
		public static final String YEAR = "fyear";
		public static final String MONTH = "fmonth";
		public static final String INCOME = "plus";
		public static final String EXPEND = "minus";
		public static final String TOTAL = "diff";
	}
}

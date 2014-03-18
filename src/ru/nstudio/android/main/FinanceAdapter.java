package ru.nstudio.android.main;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.database.ContentObserver;

import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

public class FinanceAdapter extends BaseAdapter //implements OnItemClickListener 
{
	public static final int MONTH_VIEW_TYPE = 0;
	public static final int YEAR_VIEW_TYPE 	= 1;
	public static final int TYPE_COUNT 		= 2;
	public static final int YEAR_DELIMITER_ID = -1;

	//copied from android source
	public static final int FLAG_AUTO_REQUERY =  0x00000001;
	public static final int FLAG_REGISTER_CONTENT_OBSERVER = 0x00000002;
	
	private Cursor _cursor;
 
	private ArrayList<View> _alView;
	private Context _context;
	private LayoutInflater _inflater;

	private ChangeObserver _changeObserver;
	private DataSetObserver _dataSetObserver;

	private boolean _isDataValid;

	private String _moneyFormat;
	protected int _rowIdColumn;
	private boolean _isAutoRequery;
	
	public FinanceAdapter(Context context, LayoutInflater li, Cursor curs, int flags)
	{
		setupAutoRequery( flags );
		this._context = context;
		this._inflater = li;
		this._cursor = curs;
		this._alView = new ArrayList<View>();
		this._moneyFormat = this._context.getString( R.string.money_format);
		this.parseCursorToView();
	} // ctor1
		
	public int getCount() 
	{
		return this._alView.size();
	} // getCount
	
	public int getViewTypeCount()
	{
		return this.TYPE_COUNT;
	} // getViewTypeCount

	public boolean isEnabled(int position)
	{
		return (this.getItemViewType(position) == this.MONTH_VIEW_TYPE);
	} // isEnabled
	
	public Object getItem(int position) 
	{
		// TODO Auto-generated method stub
		return null;
	} // getItem

	public long getItemId(int position) 
	{
		View v = this._alView.get(position);
		int id = v.getId();
		return id;
	} // getItemId

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		convertView = this._alView.get(position);
		return convertView;
	} // getView
	
	public int getItemViewType(int position)
	{		
		View v = this._alView.get(position);
		if (v.getId() == this.YEAR_DELIMITER_ID)
			return this.YEAR_VIEW_TYPE;
		else
			return this.MONTH_VIEW_TYPE;
	} // getItemViewType
	
	private void parseCursorToView()
	{
		if(this._cursor == null)
		{
			String err = this._context.getString(R.string.errNoCursor);
			throw new IllegalArgumentException(err);
		} // if
		
		if( _cursor.moveToFirst())
		{
			int checkYear = -1;
			do
			{
				String monthTitle = new String( _cursor.getString( _cursor.getColumnIndex(
						MoneyContract.ViewYear.MONTH_TITLE )));
				Double income = _cursor.getDouble( _cursor.getColumnIndex(MoneyContract.ViewYear.INCOME));
				Double expend = _cursor.getDouble( _cursor.getColumnIndex(MoneyContract.ViewYear.EXPEND));
				Double diff = _cursor.getDouble( _cursor.getColumnIndex(MoneyContract.ViewYear.TOTAL));
					
				Integer curYear = _cursor.getInt( _cursor.getColumnIndex( MoneyContract.ViewYear.YEAR ));
				Integer curMonth = Integer.parseInt( _cursor.getString( _cursor.getColumnIndex(
						MoneyContract.ViewYear.MONTH )));
				int idItem = (curYear*100) + curMonth;
				
				if (curYear != checkYear)
				{
					checkYear = curYear;
					View vYear = this._inflater.inflate(R.layout.year_delimiter, null);
					TextView tvDelim = (TextView) vYear.findViewById(R.id.tvYearDelimiter); 
					tvDelim.setText( curYear.toString() + " " + this._context.getString( R.string.year_tag ) );
					vYear.setId(this.YEAR_DELIMITER_ID);
					this._alView.add( vYear );
				} // if
						
				View monthDetails = this._inflater.inflate(R.layout.main_menu_list_item, null);
				
				TextView tvMonthName = (TextView) monthDetails.findViewById(R.id.tvMainMonthTitle);
				tvMonthName.setText(monthTitle);
				
				TextView tvIncome = (TextView) monthDetails.findViewById(R.id.tvMainMonthIncome);
				tvIncome.setText(String.format(this._moneyFormat, income));
				
				TextView tvExpend = (TextView) monthDetails.findViewById(R.id.tvMainMonthExpend);
				tvExpend.setText(String.format(this._moneyFormat, expend));
				
				TextView tvBalance = (TextView) monthDetails.findViewById(R.id.tvMainMonthBalance);
				tvBalance.setText(String.format(this._moneyFormat, diff));
				
				monthDetails.setId(idItem);
				this._alView.add( monthDetails );
			} while( _cursor.moveToNext());
			
			this._cursor.close();
		} // if cursor not empty
	} // parseCursorToView

	//copied from github/android
	public Cursor swapCursor(Cursor newCursor)
	{
		if (newCursor == _cursor) {
			return null;
		}
		Cursor oldCursor = _cursor;
		if (oldCursor != null) {
			if (_changeObserver != null) oldCursor.unregisterContentObserver(_changeObserver);
			if (_dataSetObserver != null) oldCursor.unregisterDataSetObserver(_dataSetObserver);
		}
		_cursor = newCursor;
		if (newCursor != null) {
			if (_changeObserver != null) newCursor.registerContentObserver(_changeObserver);
			if (_dataSetObserver != null) newCursor.registerDataSetObserver(_dataSetObserver);
			_rowIdColumn = newCursor.getColumnIndexOrThrow("_id");
			_isDataValid = true;
			// notify the observers about the new cursor
			notifyDataSetChanged();
		} else {
			_rowIdColumn = -1;
			_isDataValid = false;
			// notify the observers about the lack of a data set
			notifyDataSetInvalidated();
		}
		return oldCursor;
	}

	private void setupAutoRequery( int flags )
	{
		if ((flags & FLAG_AUTO_REQUERY) == FLAG_AUTO_REQUERY) {
			flags |= FLAG_REGISTER_CONTENT_OBSERVER;
			_isAutoRequery = true;
		} else {
			_isAutoRequery = false;
		}
	}

	protected void onContentChanged()
	{
		if (_isAutoRequery && _cursor != null && !_cursor.isClosed()) {

			if (false) Log.v("Cursor", "Auto requerying " + _cursor + " due to update");
			_isDataValid = _cursor.requery();
		}
	}

	private class ChangeObserver extends ContentObserver {
		public ChangeObserver() {
			super(new Handler());
		}

		@Override
		public boolean deliverSelfNotifications() {
			return true;
		}

		@Override
		public void onChange(boolean selfChange) {
			onContentChanged();
		}
	}

	private class MyDataSetObserver extends DataSetObserver
	{
		@Override
		public void onChanged() {
			_isDataValid = true;
			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			_isDataValid = false;
			notifyDataSetInvalidated();
		}
	}
}// class FinanceAdapter

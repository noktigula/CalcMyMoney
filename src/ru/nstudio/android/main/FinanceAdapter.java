package ru.nstudio.android.main;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

public class FinanceAdapter extends BaseAdapter
{
	private static final String TAG = FinanceAdapter.class.toString();

	public static final int MONTH_VIEW_TYPE = 0;
	public static final int YEAR_VIEW_TYPE 	= 1;
	public static final int TYPE_COUNT 		= 2;
	public static final int YEAR_DELIMITER_ID = -1;
	
	private Cursor _cursor;
 
	private ArrayList<View> _alView;
	private Context _context;
	private LayoutInflater _inflater;

	private String _moneyFormat;
	
	public FinanceAdapter(Context context, LayoutInflater li, Cursor curs)
	{
		_context = context;
		_inflater = li;
		_cursor = curs;
		_alView = new ArrayList<View>();
		_moneyFormat = this._context.getString( R.string.money_format);
		parseCursorToView();
	}
		
	public int getCount() 
	{
		return this._alView.size();
	}
	
	public int getViewTypeCount()
	{
		return this.TYPE_COUNT;
	}

	public boolean isEnabled(int position)
	{
		return (this.getItemViewType(position) == this.MONTH_VIEW_TYPE);
	}
	
	public Object getItem(int position) 
	{
		return null;
	}

	public long getItemId(int position) 
	{
		View v = this._alView.get(position);
		int id = v.getId();
		return id;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		convertView = this._alView.get(position);
		return convertView;
	}
	
	public int getItemViewType(int position)
	{		
		View v = this._alView.get(position);
		if (v.getId() == this.YEAR_DELIMITER_ID)
			return this.YEAR_VIEW_TYPE;
		else
			return this.MONTH_VIEW_TYPE;
	}
	
	private void parseCursorToView()
	{
		if( _cursor == null)
		{
			return;
		}

		if( _alView.size() > 0 )
		{
			_alView.clear();
		}
		
		if( !_cursor.moveToFirst())
		{
			return;
		}

		int checkYear = -1;
		do
		{
			String monthTitle = new String( _cursor.getString( _cursor.getColumnIndex(
					MoneyContract.ViewYear.MONTH_TITLE ) ));
			Double income = _cursor.getDouble( _cursor.getColumnIndex( MoneyContract.ViewYear.INCOME ) );
			Double expend = _cursor.getDouble( _cursor.getColumnIndex( MoneyContract.ViewYear.EXPEND ) );
			Double diff = _cursor.getDouble( _cursor.getColumnIndex( MoneyContract.ViewYear.TOTAL ) );

			Integer curYear = _cursor.getInt( _cursor.getColumnIndex( MoneyContract.ViewYear.YEAR ) );
			Integer curMonth = Integer.parseInt( _cursor.getString( _cursor.getColumnIndex(
					MoneyContract.ViewYear.MONTH ) ));

			int idItem = (curYear*100) + curMonth;

			if (curYear != checkYear)
			{
				checkYear = curYear;
				View vYear = _inflater.inflate(R.layout.year_delimiter, null);
				TextView tvDelim = (TextView) vYear.findViewById(R.id.tvYearDelimiter);
				tvDelim.setText( curYear.toString() + " " + this._context.getString( R.string.year_tag ) );
				vYear.setId(YEAR_DELIMITER_ID);
				_alView.add( vYear );
			}

			View monthDetails = _inflater.inflate(R.layout.list_item_main_overview, null);

			TextView tvMonthName = (TextView) monthDetails.findViewById(R.id.tvMainMonthTitle);
			tvMonthName.setText(monthTitle);

			TextView tvIncome = (TextView) monthDetails.findViewById(R.id.tvMainMonthIncome);
			tvIncome.setText(getTextForIncome(income));

			TextView tvExpend = (TextView) monthDetails.findViewById(R.id.tvMainMonthExpend);
			tvExpend.setText(getTextForExpend(expend));

			TextView tvBalance = (TextView) monthDetails.findViewById(R.id.tvMainMonthBalance);
			tvBalance.setText(String.format(_moneyFormat, diff));

			monthDetails.setId(idItem);
			_alView.add( monthDetails );
		} while( _cursor.moveToNext());
	}

    private String getTextForIncome(double income)
    {
        return String.format(_context.getString(R.string.format_income), income);
    }

    private String getTextForExpend(double expend)
    {
        return String.format(_context.getString(R.string.format_expend), expend);
    }

	public void swapCursor(Cursor newCursor)
	{
		if (newCursor == _cursor )
		{
			return;
		}

		_cursor = newCursor;
		parseCursorToView();
		notifyDataSetChanged();
	}
}

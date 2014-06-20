package ru.nstudio.android.MonthDetails.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ru.nstudio.android.DateParser;
import ru.nstudio.android.R;
import ru.nstudio.android.Storage.MoneyContract;

public class MonthDetailsAdapter extends CursorAdapter //implements OnItemClickListener
{
	private String	_moneyFormat;
	private int 	_layout;
    private Map<Integer, Boolean> _itemTypes;

	public MonthDetailsAdapter( Context context, Cursor cursor, int layout )
	{
		super( context, cursor, 0 );
		_moneyFormat = context.getString( R.string.money_format);
		_layout = layout;
        _itemTypes = new HashMap<Integer, Boolean>();
	} // MonthDetailsAdapter

	public Object getItem(int position) 
	{
		return null;
	} // getItem

	@Override
	public View newView( Context context, Cursor cursor, ViewGroup parentGroup )
	{
		LayoutInflater inflater = LayoutInflater.from( context );
		return inflater.inflate( _layout, parentGroup, false );
	}

	@Override
	public void bindView( View v, Context context, Cursor c )
	{
		boolean isIncome = (c.getInt(c.getColumnIndex( MoneyContract.Finance.TYPE)) == 1);
		String explain = new String(c.getString(c.getColumnIndex(MoneyContract.Finance.REASON)));
		Double price = c.getDouble( c.getColumnIndex( MoneyContract.Finance.PRICE ) );
		Integer quant = c.getInt(c.getColumnIndex(MoneyContract.Finance.QUANTITY));
		int id = c.getInt(c.getColumnIndex("_id"));
		String date = new String(c.getString(c.getColumnIndex(MoneyContract.Finance.DATE)));
		String category = new String(c.getString(c.getColumnIndex(MoneyContract.Category.NAME)));

		String dateDescript =
				DateParser.format( date, DateParser.CALCMONEY_FORMAT );

		TextView tvExplain = (TextView) v.findViewById(R.id.tvFinanceOperationDetails1);
		tvExplain.setText(explain);

		TextView tvBalance = (TextView) v.findViewById(R.id.tvShowTotalCost1);
		tvBalance.setText(String.format( _moneyFormat, price * quant ) );

		TextView tvDate = (TextView) v.findViewById(R.id.tvShowDate1);
		tvDate.setText(dateDescript);

		TextView tvCategory = (TextView) v.findViewById(R.id.tvShowCategory);
		tvCategory.setText(category);

        _itemTypes.put(id, isIncome);

        v.setBackgroundColor(getColorForItem(id));

		v.setId(id);
	}

    public int getColorForItem(Integer id)
    {
        boolean isIncome = isIncomeView(id);
        return isIncome ? mContext.getResources().getColor(R.color.income_view_background)
                        : mContext.getResources().getColor(R.color.expend_view_background);
    }

    public boolean isIncomeView(Integer id)
    {
        if(!_itemTypes.containsKey(id))
        {
            throw new IllegalArgumentException("Unexpected view id");
        }
        return _itemTypes.get(id);
    }
}

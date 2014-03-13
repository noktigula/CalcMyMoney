package ru.nstudio.android.MonthDetails.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.nstudio.android.MonthDetails.Screens.MonthCategoryFragment;
import ru.nstudio.android.MonthDetails.Screens.MonthDetailsFragment;

/**
 * Created by noktigula on 03.03.14.
 */
public class MonthOverviewPagerAdapter extends FragmentStatePagerAdapter
{
	public static final int PAGES_COUNT = 2;

	private int _idItem;
	private String _monthTitle;

	public MonthOverviewPagerAdapter( int idItem, String monthTitle, FragmentManager fm )
	{
		super(fm);
		_idItem = idItem;
		_monthTitle = monthTitle;
	}

	@Override
	public Fragment getItem( int i )
	{
		switch(i)
		{
			case 0: return MonthDetailsFragment.getInstance( _idItem, _monthTitle );
			case 1: return MonthCategoryFragment.getInstance( _idItem, _monthTitle );
			default: throw new RuntimeException( "Unexpected index" );
		}
	}

	@Override
	public int getCount()
	{
		return PAGES_COUNT;
	}
}

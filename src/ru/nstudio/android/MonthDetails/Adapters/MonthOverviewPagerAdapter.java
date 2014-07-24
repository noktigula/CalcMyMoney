package ru.nstudio.android.MonthDetails.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.nstudio.android.MonthDetails.Screens.MonthCategoryFragment;
import ru.nstudio.android.MonthDetails.Screens.MonthDayFragment;
import ru.nstudio.android.MonthDetails.Screens.MonthDetailsFragment;

/**
 * Created by noktigula on 03.03.14.
 */
public class MonthOverviewPagerAdapter extends FragmentStatePagerAdapter
{
	public static final int PAGES_COUNT = 3;

	private int _idItem;

	public MonthOverviewPagerAdapter( int idItem, FragmentManager fm )
	{
		super(fm);
		_idItem = idItem;
	}

	@Override
	public Fragment getItem( int i )
	{
		switch(i)
		{
			case 0: return MonthDayFragment.getInstance( _idItem );
			case 1: return MonthDetailsFragment.getInstance( _idItem );
			case 2: return MonthCategoryFragment.getInstance( _idItem );
			default: throw new RuntimeException( "Unexpected index" );
		}
	}

	@Override
	public int getCount()
	{
		return PAGES_COUNT;
	}
}

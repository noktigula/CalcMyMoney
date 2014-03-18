package ru.nstudio.android.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by noktigula on 16.03.14.
 */
public class FinancePagerAdapter extends FragmentStatePagerAdapter
{
	private static final int PAGES_COUNT = 1;

	public FinancePagerAdapter( FragmentManager fm )
	{
		super(fm);
	}

	@Override
	public int getCount()
	{
		return PAGES_COUNT;
	}

	@Override
	public Fragment getItem( int i )
	{
		switch( i )
		{
			case 0: return MainOverviewFragment.getInstance();
			default: throw new RuntimeException("Not implemented yet");
		}
	}
}

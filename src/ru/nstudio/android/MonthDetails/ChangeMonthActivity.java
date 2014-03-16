package ru.nstudio.android.MonthDetails;

//import android.R;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import ru.nstudio.android.ContextMenuInitializer;
import ru.nstudio.android.MonthDetails.Adapters.MonthOverviewPagerAdapter;
import ru.nstudio.android.R;

public class ChangeMonthActivity extends ActionBarActivity //implements OnItemClickListener
{
	private SQLiteDatabase 	_db;
	public  boolean 		_wasChanges;
	private ViewPager		_pager;

	private static int RESULT_FIRST_USER_DETAIL = 11;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView( R.layout.activity_month_details );
	
		Intent parentIntent = getIntent();
		int idItem = parentIntent.getIntExtra("ru.nstudio.android.selectedItem", -1);
		String monthTitle = parentIntent.getStringExtra( "ru.nstudio.android.monthTitle" );

		if(idItem == -1) throw new IllegalArgumentException("ERROR: can`t load _month details - can`t get _month ID");

		_pager = (ViewPager)findViewById( R.id.pagerMonth );
		_pager.setAdapter( new MonthOverviewPagerAdapter( idItem, monthTitle, getSupportFragmentManager() ) );
        //registerForContextMenu(this._lvAddFinances );
	} // onCreate

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        ContextMenuInitializer initializer = new ContextMenuInitializer(menu, v, menuInfo);
        menu = initializer.getMenu();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) acmi.id;

        switch(item.getGroupId())
        {
            case ContextMenuInitializer.CONTEXT_MENU_CHANGE:
            {
                //this.showOperationDetails(id);
                break;
            }

            case ContextMenuInitializer.CONTEXT_MENU_DELETE:
            {
                //this.deleteOperation(id);
                break;
            }

            default:
            {
                break;
            }
        }

        return true;
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Intent intent = new Intent();
			intent.putExtra("ru.nstudio.android.changes", this._wasChanges );
			setResult(RESULT_OK, intent);
		}
		return super.onKeyDown(keyCode, event);
	}


//	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
//	{
//		this.showOperationDetails((int) id);
//	}

//    public void showOperationDetails(int id)
//    {
//        Intent intent = new Intent(this.INTENT_ACTION_SHOW_DETAILS);
//        intent.putExtra("ru.nstudio.android.idFinance", id);
//
//        intent.putExtra("ru.nstudio.android._month", this._month );
//        intent.putExtra("ru.nstudio.android._year", this._year );
//
//        startActivityForResult(intent, RESULT_FIRST_USER_DETAIL);
//    }
//
//    public void deleteOperation(int id)
//    {
//        DeleteDialog deleteDialog = new DeleteDialog(this, id);
//        deleteDialog.show();
//    }
	
//	public void createListView()
//	{
//		this.initDatabase();
//		if (this._lvAddFinances != null)
//		{
//			this._lvAddFinances.removeFooterView( this._vFooter );
//		}
//		this._lvAddFinances = null;
//		this._lvAddFinances = (ListView)findViewById( R.id.listOperations );//getListView();
//
//		this._lvAddFinances.addFooterView(this._vFooter, null, true);
//
//		String monthWithLeadingZero;
//		if ((this._month / 10) == 0)
//		{
//			monthWithLeadingZero = new String("0" + Integer.toString(this._month ));
//		}
//		else
//		{
//			monthWithLeadingZero = new String(Integer.toString(this._month ));
//		}
//
//		String[] whereArgs = new String[] {Integer.toString(this._year ), monthWithLeadingZero};
//
//		String query = " SELECT " +
//					   "f." + DBHelper.Finance.ID 		+ ", " +
//					   "f." + DBHelper.Finance.REASON 	+ ", " +
//					   "f." + DBHelper.Finance.PRICE 	+ ", " +
//					   "f." + DBHelper.Finance.QUANTITY + ", " +
//					   "f." + DBHelper.Finance.DATE 	+ ", " +
//					   "f." + DBHelper.Finance.TYPE 	+ ", " +
//					   "c." + DBHelper.Category.NAME    +
//					   " FROM " + DBHelper.Finance.TABLE_NAME + " AS f " +
//					   " INNER JOIN " + DBHelper.Category.TABLE_NAME +  " AS c " +
//					   		"ON f." + DBHelper.Finance.CATEGORY + " = c." + DBHelper.Category.ID +
//					   " WHERE strftime('%Y', " + DBHelper.Finance.DATE + ") = ? " +
//						  "AND strftime('%m', " + DBHelper.Finance.DATE + ") = ? " +
//					   " ORDER BY strftime('%d', " + DBHelper.Finance.DATE + "), " + DBHelper.Finance.ID;
//
//
//		Cursor c = this._db.rawQuery(query, whereArgs);
//
//		MonthDetailsAdapter mda = new MonthDetailsAdapter(this, this.getLayoutInflater(), c);
//
//		_lvAddFinances.setAdapter(mda);
//		_lvAddFinances.setOnItemClickListener( this );
//
//		c.close();
//		this._db.close();
//	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent outputIntent)
	{
		if (resultCode == RESULT_FIRST_USER_DETAIL)
		{
			this._wasChanges = outputIntent.getBooleanExtra("ru.nstudio.android.success", false);
			if (this._wasChanges )
			{
				//TODO refresh list view in fragment
				//this.createListView();
			}
		}
	}
}

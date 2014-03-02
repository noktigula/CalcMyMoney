package ru.nstudio.android;

//import android.R;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import ru.nstudio.android.MonthDetails.Adapters.MonthDetailsAdapter;

public class ChangeMonthActivity extends ActionBarActivity implements OnItemClickListener
{
	private Button 			_btnOk;
	private ListView 		_lvAddFinances;
	private View 			_vFooter;
	private View 			_vHeader;
	private DBHelper 		_dbHelper;
	private SQLiteDatabase 	_db;
	private int 			_month;
	private int 			_year;
	public  boolean 		_wasChanges;
	private TextView 		_tvMonthDescription;

    private final String INTENT_ACTION_SHOW_DETAILS = "ru.nstudio.android.showDetails";
		
	private static int RESULT_FIRST_USER_DETAIL = 11;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.month_operations );
	
		Intent parentIntent = getIntent();
		int idItem = parentIntent.getIntExtra("ru.nstudio.android.selectedItem", -1);

		if(idItem == -1) throw new IllegalArgumentException("ERROR: can`t load _month details - can`t get _month ID");
		
		this._month = idItem % 100;
		this._year = idItem / 100;
		
		String monthTitle = new String(parentIntent.getStringExtra("ru.nstudio.android.monthTitle") + " " + Integer.toString( _year ));

		this._tvMonthDescription = (TextView)findViewById(R.id.tvMonthDescription);
		this._tvMonthDescription.setText( monthTitle );
		
		this.initDatabase();

		this._vFooter = getLayoutInflater().inflate(R.layout.tip_add_new_details, null);
		
		createListView();
		
		this._wasChanges = false;

        registerForContextMenu(this._lvAddFinances );
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
                this.showOperationDetails(id);
                break;
            }

            case ContextMenuInitializer.CONTEXT_MENU_DELETE:
            {
                this.deleteOperation(id);
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
	
	public void initDatabase()
	{
		if(this._dbHelper == null)
		{
			this._dbHelper = new DBHelper(this, DBHelper.CURRENT_DATABASE_VERSION);
		}
		if(this._db == null)
		{
			this._db = this._dbHelper.getWritableDatabase();
		}
		
		if(!this._db.isOpen())
			this._db = this._dbHelper.getWritableDatabase();
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) 
	{
		this.showOperationDetails((int) id);
	}

    public void showOperationDetails(int id)
    {
        Intent intent = new Intent(this.INTENT_ACTION_SHOW_DETAILS);
        intent.putExtra("ru.nstudio.android.idFinance", id);

        intent.putExtra("ru.nstudio.android._month", this._month );
        intent.putExtra("ru.nstudio.android._year", this._year );

        startActivityForResult(intent, RESULT_FIRST_USER_DETAIL);
    }

    public void deleteOperation(int id)
    {
        DeleteDialog deleteDialog = new DeleteDialog(this, id);
        deleteDialog.show();
    }
	
	public void createListView()
	{
		this.initDatabase();
		if (this._lvAddFinances != null)
		{
			this._lvAddFinances.removeFooterView( this._vFooter );
		}
		this._lvAddFinances = null;
		this._lvAddFinances = (ListView)findViewById( R.id.listOperations );//getListView();
				
		this._lvAddFinances.addFooterView(this._vFooter, null, true);

		String monthWithLeadingZero;
		if ((this._month / 10) == 0)
		{
			monthWithLeadingZero = new String("0" + Integer.toString(this._month ));
		}
		else
		{
			monthWithLeadingZero = new String(Integer.toString(this._month ));
		}

		String[] whereArgs = new String[] {Integer.toString(this._year ), monthWithLeadingZero};

		String query = " SELECT " +
					   "f." + DBHelper.Finance.ID 		+ ", " +
					   "f." + DBHelper.Finance.REASON 	+ ", " +
					   "f." + DBHelper.Finance.PRICE 	+ ", " +
					   "f." + DBHelper.Finance.QUANTITY + ", " +
					   "f." + DBHelper.Finance.DATE 	+ ", " +
					   "f." + DBHelper.Finance.TYPE 	+ ", " +
					   "c." + DBHelper.Category.NAME    +
					   " FROM " + DBHelper.Finance.TABLE_NAME + " AS f " +
					   " INNER JOIN " + DBHelper.Category.TABLE_NAME +  " AS c " +
					   		"ON f." + DBHelper.Finance.CATEGORY + " = c." + DBHelper.Category.ID +
					   " WHERE strftime('%Y', " + DBHelper.Finance.DATE + ") = ? " +
						  "AND strftime('%m', " + DBHelper.Finance.DATE + ") = ? " +
					   " ORDER BY strftime('%d', " + DBHelper.Finance.DATE + "), " + DBHelper.Finance.ID;


		Cursor c = this._db.rawQuery(query, whereArgs);
		
		MonthDetailsAdapter mda = new MonthDetailsAdapter(this, this.getLayoutInflater(), c);
		
		_lvAddFinances.setAdapter(mda);
		_lvAddFinances.setOnItemClickListener( this );
		
		c.close();
		this._db.close();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent outputIntent)
	{
		if (resultCode == RESULT_FIRST_USER_DETAIL)
		{
			this._wasChanges = outputIntent.getBooleanExtra("ru.nstudio.android.success", false);
			if (this._wasChanges )
			{
				this.createListView();
			}
		}
	}
}

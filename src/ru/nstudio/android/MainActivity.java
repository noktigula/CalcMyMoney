package ru.nstudio.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnItemClickListener, OnClickListener
{
	private DBHelper _dbHelper;
	private SQLiteDatabase _db;
	private ListView _lv;
	private View _vFooter;
	private FinanceAdapter _fAdapter;
	private Menu			menu;
	private MenuListener	menuListener;
	
	public static final int 	RESULT_FIRST_USER_MAIN = 10;
	public static final int		RESULT_FIRST_USER_DETAIL = 11;

    private static String INTENT_ACTION_CHANGE = "ru.nstudio.android.changeMonth";
    private static String INTENT_ACTION_ADD    = "ru.nstudio.android.addDetails";
	
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		setContentView( R.layout.main );

        initDatabase();

		_lv = (ListView) findViewById( R.id.lvMain );
    	_vFooter = getLayoutInflater().inflate(R.layout.tip_add_new_details, null);
        
    	makeListCalculations();
        registerForContextMenu( _lv );
    	
    	menuListener = new MenuListener( this );

		//ActionBar actionBar = getSupportActionB
    } // onCreate

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        ContextMenuInitializer initializer = new ContextMenuInitializer(menu, v, menuInfo);
        menu = initializer.getMenu();
    } // onCreateContextMenu

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TableLayout tl = (TableLayout) acmi.targetView;

        if(tl.getChildCount() <= 0)
        {
            return false;
        } // if

        TextView tvMonth = (TextView) tl.getChildAt(0);
        String monthTitle = tvMonth.getText().toString();

        Intent intent;

        switch (item.getGroupId())
        {
            case ContextMenuInitializer.CONTEXT_MENU_CHANGE:
            {
                intent = this.getIntentForChange((int)acmi.id, monthTitle);
                runChangeActivity(intent);
                break;
            }  // case change

            case ContextMenuInitializer.CONTEXT_MENU_DELETE:
            {
                deleteMonthInfo((int)acmi.id);
                break;
            }  // case delete

            default:
            {
                break;
            } // default
        } // switch

        return super.onContextItemSelected(item);
    }   // onContextItemSelected

    public void deleteMonthInfo(int monthYearCode)
    {
        DeleteDialog dialog = new DeleteDialog(this, monthYearCode);
        dialog.show();
    } // deleteMonthInfo

    public void onClick(DialogInterface v, int buttonID)
    {
        switch (buttonID)
        {
            case AlertDialog.BUTTON_POSITIVE:

                return;

            default: break;
        }
    }  // onClick
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		
		this.menu = menu;
		//return true;
		return super.onCreateOptionsMenu(menu);
	} // onCreateOptionsMenu

	
	void makeListCalculations()
	{
		this.initDatabase();
		
		if (this._lv != null)
		{
			this._lv.removeFooterView( this._vFooter );
		} // if
		
		this._lv = null;
		this._lv = (ListView) findViewById( R.id.lvMain );
		
		String [] args = new String[]{};
	        
		Cursor c = _db.rawQuery("SELECT " +
							   "f.idFinance, " +
							   "strftime('%Y', f.financeDate) AS fyear, " + 
							   "strftime('%m', f.financeDate) AS fmonth, " + 
							   "mt.title, IFNULL(tmp1.plus, 0) AS plus, IFNULL(tmp2.minus, 0) AS minus, " + 
							   "(IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) AS diff " +
							   "FROM Finance AS f " +
							   "INNER JOIN MonthTitle AS mt ON mt.idMonthTitle = fmonth " +
							   "LEFT JOIN (SELECT strftime('%m', financeDate) AS month, strftime('%Y', financeDate) AS year, " +
							   "SUM(price*quantity) AS plus " +
							   "FROM Finance " + 
							   "WHERE type = 1 " + 
							   "GROUP BY year, month) AS tmp1 ON tmp1.month = fmonth AND tmp1.year = fyear " +
							   "LEFT JOIN (SELECT strftime('%m', financeDate) AS month, strftime('%Y', financeDate) AS year, " +
							   "SUM(price*quantity) AS minus " +
							   "FROM Finance " + 
							   "WHERE type = 0 " +
							   "GROUP BY year, month) AS tmp2 ON tmp2.month = fmonth AND tmp2.year = fyear " +
							   "GROUP BY strftime('%Y', f.financeDate), strftime('%m', f.financeDate), mt.title, " + 
							   "tmp1.plus, tmp2.minus, (IFNULL(tmp1.plus, 0) - IFNULL(tmp2.minus, 0)) " +
							   "ORDER BY fyear, fmonth", args);
	
		_lv.addFooterView( this._vFooter, null, true );
		
		this._fAdapter = new FinanceAdapter(this, this.getLayoutInflater(), c);
		
		try
		{
			_lv.setAdapter( this._fAdapter );
			_lv.setOnItemClickListener( this );
		} // try
		catch (IllegalStateException e)
		{
			Toast.makeText(this,"IllegalStateException", 10000000).show();
		} // catch
		catch(Exception exc)
		{
			
			Toast.makeText(this, exc.getMessage(), 10000000).show();
		} // catch*/
		
		c.close();
		this._db.close();
	} // makeListCalculations
	
	public void initDatabase()
	{
		if(this._dbHelper == null)
		{
			this._dbHelper = new DBHelper(this, DBHelper.CURRENT_DATABASE_VERSION);
		} // if
		if(this._db == null)
		{
			this._db = this._dbHelper.getWritableDatabase();
		} // if
		
		if(!this._db.isOpen())
			this._db = this._dbHelper.getWritableDatabase();
	} // initDatabase
	  
	public void onItemClick(AdapterView<?> adView, View target, int position, long id)
	{
		Intent intent;
		if (id == -1)
		{
			intent = new Intent(INTENT_ACTION_ADD);
		} // if
		else
		{
			TextView tvMonth = (TextView)target.findViewById(R.id.tvMainMonthTitle);
			intent = this.getIntentForChange((int)id, tvMonth.getText().toString());
		} //if
		
        runChangeActivity(intent);
	} // onItemCLick

    public void runChangeActivity(Intent intent)
    {
        try
        {
            startActivityForResult(intent, RESULT_FIRST_USER_MAIN);
        } // try
        catch(IllegalArgumentException iae)
        {
            Toast.makeText(this, iae.getMessage(), 10000).show();
        } // catch
    } // runChangeActivity

    public Intent getIntentForChange(int id, String monthTitle)
    {
        Intent intent = new Intent(INTENT_ACTION_CHANGE);
        intent.putExtra("ru.nstudio.android.selectedItem", id);
        intent.putExtra("ru.nstudio.android.monthTitle", monthTitle);
        return intent;
    } // getIntentForChange
	  
	  public void onActivityResult(int requestCode, int resultCode, Intent outputIntent)
	  {
		  boolean refresh = false;
		  
		  if (resultCode == RESULT_OK)
		  {
			  refresh = outputIntent.getBooleanExtra("ru.nstudio.android.changes", false);
		  } // if
		  else if (resultCode == RESULT_FIRST_USER_DETAIL)
		  {
			  refresh = outputIntent.getBooleanExtra("ru.nstudio.android.success", false);
		  } // else if
		  
		  if (refresh)
		  {
			  this.makeListCalculations();
		  } // if
	  } // onActivityResult
} // class MainActivity
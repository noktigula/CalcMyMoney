package ru.nstudio.android;

import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created with IntelliJ IDEA.
 * User: noktigula
 * Date: 03.03.13
 * Time: 1:38
 * To change this template use File | Settings | File Templates.
 */
public class ContextMenuInitializer
{
    private ContextMenu contextMenu;
    private ContextMenuInfo menuInfo;
    private View            view;

    public static final int     CONTEXT_MENU_CHANGE = 20;
    public static final int     CONTEXT_MENU_DELETE = 30;

    public ContextMenuInitializer(ContextMenu menu, View v, ContextMenuInfo info)
    {
        this.menuInfo    = info;
        this.view        = v;
        this.contextMenu = menu;
    } // ContextMenuCreator

    public ContextMenu getMenu()
    {
        int id = this.view.getId();

        this.contextMenu.add(CONTEXT_MENU_CHANGE, id, 1, R.string.menuChangeRequest);
        this.contextMenu.add(CONTEXT_MENU_DELETE, id, 2, R.string.menuDeleteRequest);

        return  this.contextMenu;
    } // getMenu
} // class ContextMenuCreator

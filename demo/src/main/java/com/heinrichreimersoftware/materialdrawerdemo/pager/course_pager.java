package com.heinrichreimersoftware.materialdrawerdemo.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.heinrichreimersoftware.materialdrawerdemo.Menu1;
import com.heinrichreimersoftware.materialdrawerdemo.into_course.announce;
import com.heinrichreimersoftware.materialdrawerdemo.into_course.discussion;
import com.heinrichreimersoftware.materialdrawerdemo.into_course.document;
import com.heinrichreimersoftware.materialdrawerdemo.into_course.group;

public class course_pager extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public course_pager(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount= tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                announce tab0 = new announce();
                return tab0;
            case 1:
                document tab1 = new document();
                return tab1;
            case 2:
                Menu1 tab2 = new Menu1();
                return tab2;
            case 3:
                discussion tab3 = new discussion();
                return tab3;
            case 4:
                group tab4 = new group();
                return tab4;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}

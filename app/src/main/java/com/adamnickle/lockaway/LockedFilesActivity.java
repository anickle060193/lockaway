package com.adamnickle.lockaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class LockedFilesActivity extends AppCompatActivity
{
    public static final String EXTRA_LOCKER = "extra_locker";

    private static final String[] IMAGE_EXTENSIONS = { "jpg", "jpeg", "gif", "png", "bmp", "webp" };
    private static final String[] VIDEO_EXTENSIONS = { "3gp", "mp4", "ts", "webm", "mkv" };
    private static final int CHOOSE_FILE_REQUEST = 1001;

    private Key mKey;

    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_locked_files );

        mKey = getIntent().getParcelableExtra( LockedFilesActivity.EXTRA_LOCKER );

        mPagerAdapter = new PagerAdapter( getSupportFragmentManager() );
        mViewPager = (ViewPager)findViewById( R.id.viewPager );
        mViewPager.setAdapter( mPagerAdapter );
        mViewPager.addOnPageChangeListener( new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected( int position )
            {
                final ActionBar actionBar = getSupportActionBar();
                if( actionBar != null )
                {
                    actionBar.setSelectedNavigationItem( position );
                }
            }
        } );

        final ActionBar actionBar = getSupportActionBar();
        if( actionBar != null )
        {
            actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_TABS );
            ActionBar.TabListener tabListener = new ActionBar.TabListener()
            {
                @Override
                public void onTabSelected( ActionBar.Tab tab, FragmentTransaction ft )
                {
                    mViewPager.setCurrentItem( tab.getPosition() );
                }

                @Override
                public void onTabUnselected( ActionBar.Tab tab, FragmentTransaction ft )
                {

                }

                @Override
                public void onTabReselected( ActionBar.Tab tab, FragmentTransaction ft )
                {

                }
            };
            final ActionBar.Tab images = actionBar.newTab()
                    .setText( "Images" )
                    .setTabListener( tabListener );

            final ActionBar.Tab videos = actionBar.newTab()
                    .setText( "Videos" )
                    .setTabListener( tabListener );

            final ActionBar.Tab other = actionBar.newTab()
                    .setText( "Other" )
                    .setTabListener( tabListener );

            actionBar.addTab( images );
            actionBar.addTab( videos );
            actionBar.addTab( other );
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        getMenuInflater().inflate( R.menu.menu_locked_files, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.actionAdd:
                openFileManager( IMAGE_EXTENSIONS );
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void openFileManager( String[] extensions )
    {
        Intent intent = new Intent( this, FileSelectorActivity.class );
        intent.putExtra( FileSelectorActivity.EXTRA_FILE_EXTENSIONS, extensions );
        startActivity( intent );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if( requestCode == CHOOSE_FILE_REQUEST )
        {
            if( resultCode == Activity.RESULT_OK )
            {
            }
        }
    }

    private class PagerAdapter extends FragmentPagerAdapter
    {
        public PagerAdapter( FragmentManager fm )
        {
            super( fm );
        }

        @Override
        public Fragment getItem( int position )
        {
            return LockedImageFileFragment.newInstance( mKey );
        }

        @Override
        public int getCount()
        {
            return 3;
        }
    }
}

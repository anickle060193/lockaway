package com.adamnickle.lockaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.security.InvalidParameterException;

public class LockedFilesActivity extends AppCompatActivity
{
    public interface LockedFragment
    {
        void add( String filename );
    }

    public static final String EXTRA_KEY = "extra_key";

    private static final String[] IMAGE_EXTENSIONS = { "jpg", "jpeg", "gif", "png", "bmp", "webp" };
    private static final String[] VIDEO_EXTENSIONS = { "3gp", "mp4", "ts", "webm", "mkv" };

    private static final int CHOOSE_FILE_REQUEST = 1001;

    private static final int IMAGES_TAB = 0;
    private static final int VIDEOS_TAB = 1;
    private static final int OTHERS_TAB = 2;

    private static final String[] TAB_TITLES = { "Images", "Videos", "Others" };

    private Key mKey;

    private PagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private PagerTabStrip mPagerTabStrip;

    private LockedImageFilesFragment mLockedImageFilesFragment;
    private LockedImageFilesFragment mLockedVideoFilesFragment;
    private LockedImageFilesFragment mLockedOtherFilesFragment;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_locked_files );

        mKey = getIntent().getParcelableExtra( LockedFilesActivity.EXTRA_KEY );

        mLockedImageFilesFragment = LockedImageFilesFragment.newInstance( mKey );
        mLockedVideoFilesFragment = LockedImageFilesFragment.newInstance( mKey );
        mLockedOtherFilesFragment = LockedImageFilesFragment.newInstance( mKey );

        mPagerAdapter = new PagerAdapter( getSupportFragmentManager() );
        mViewPager = (ViewPager)findViewById( R.id.viewPager );
        mViewPager.setAdapter( mPagerAdapter );

        mPagerTabStrip = (PagerTabStrip)findViewById( R.id.pagerTabStrip );
        mPagerTabStrip.setTabIndicatorColorResource( R.color.bright_light_blue );
        mPagerTabStrip.setDrawFullUnderline( true );
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
                switch( mViewPager.getCurrentItem() )
                {
                    case IMAGES_TAB:
                        openFileManager( IMAGE_EXTENSIONS );
                        break;
                    case VIDEOS_TAB:
                        openFileManager( VIDEO_EXTENSIONS );
                        break;
                    case OTHERS_TAB:
                        openFileManager( null );
                        break;
                }
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void openFileManager( String[] extensions )
    {
        Intent intent = new Intent( this, FileSelectorActivity.class );
        intent.putExtra( FileSelectorActivity.EXTRA_FILE_EXTENSIONS, extensions );
        startActivityForResult( intent, CHOOSE_FILE_REQUEST );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        if( requestCode == CHOOSE_FILE_REQUEST )
        {
            if( resultCode == Activity.RESULT_OK )
            {
                final String filename = data.getStringExtra( FileSelectorActivity.EXTRA_FILENAME );
                if( filename != null )
                {
                    switch( mViewPager.getCurrentItem() )
                    {
                        case IMAGES_TAB:
                            mLockedImageFilesFragment.add( filename );
                            break;
                        case VIDEOS_TAB:
                            mLockedVideoFilesFragment.add( filename );
                            break;
                        case OTHERS_TAB:
                            mLockedOtherFilesFragment.add( filename );
                            break;
                    }
                }
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
            switch( position )
            {
                case IMAGES_TAB:
                    return mLockedImageFilesFragment;
                case VIDEOS_TAB:
                    return mLockedVideoFilesFragment;
                case OTHERS_TAB:
                    return mLockedOtherFilesFragment;
                default:
                    throw new InvalidParameterException( "Invalid tab: " + position );
            }
        }

        @Override
        public int getCount()
        {
            return 3;
        }

        @Override
        public CharSequence getPageTitle( int position )
        {
            return TAB_TITLES[ position ];
        }
    }
}

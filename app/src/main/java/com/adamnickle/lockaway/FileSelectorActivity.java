package com.adamnickle.lockaway;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FileSelectorActivity extends AppCompatActivity
{
    public static final String EXTRA_INITIAL_DIRECTORY = "extra_initial_directory";
    public static final String EXTRA_FILENAME = "extra_filename";

    private FileSelectorFragment mFragment;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_file_selector );

        mFragment = (FileSelectorFragment)getSupportFragmentManager()
                .findFragmentById( R.id.fileSelectorFragment );
    }

    @Override
    public void onBackPressed()
    {
        if( !mFragment.onBackPressed() )
        {
            super.onBackPressed();
        }
    }
}

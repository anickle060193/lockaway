package com.adamnickle.lockaway;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;


public class SecretFileFragment extends Fragment
{
    private static final int CHOOSE_FILE_REQUEST = 1001;

    private Password mPassword;

    // UI Elements
    private View mMainView;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayRecyclerAdapter<File, ViewHolder> mSecretFileAdapter;

    public static SecretFileFragment newInstance( Password password )
    {
        SecretFileFragment fragment = new SecretFileFragment();
        fragment.mPassword = password;
        return fragment;
    }

    public SecretFileFragment(){ }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setRetainInstance( true );
        setHasOptionsMenu( true );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        if( mMainView == null )
        {
            mMainView = inflater.inflate( R.layout.fragment_file, container, false );

            mRecyclerView = (RecyclerView)mMainView.findViewById( R.id.fileGridView );
            mLayoutManager = new GridLayoutManager( getActivity(), 4, LinearLayoutManager.VERTICAL, false );
            mRecyclerView.setLayoutManager( mLayoutManager );

            File folder = getActivity().getFilesDir();
            File[] files = folder.listFiles();
            mSecretFileAdapter = new SecretFileRecyclerAdapter( files );

            mRecyclerView.setAdapter( mSecretFileAdapter );
        }
        else
        {
            ViewGroup parent = (ViewGroup)mMainView.getParent();
            if( parent != null )
            {
                parent.removeView( mMainView );
            }
        }
        return mMainView;
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
    {
        inflater.inflate( R.menu.menu_secret_file, menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.actionAdd:
                openFileManager();
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void openFileManager()
    {
        Intent intent = new Intent( getActivity(), FileSelectorActivity.class );
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

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View MainView;
        TextView Text;

        public ViewHolder( View itemView )
        {
            super( itemView );

            MainView = itemView;
            Text = (TextView)MainView.findViewById( R.id.text );
        }

        public void setHolderText( final String text )
        {
            new Handler( Looper.getMainLooper() ).post( new Runnable()
            {
                @Override
                public void run()
                {
                    ViewHolder.this.Text.setText( text );
                }
            } );
        }
    }

    public class SecretFileRecyclerAdapter extends ArrayRecyclerAdapter<File, ViewHolder>
    {
        public SecretFileRecyclerAdapter( File[] files )
        {
            super( Arrays.asList( files ) );
        }

        @Override
        public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i )
        {
            View view = LayoutInflater
                    .from( viewGroup.getContext() )
                    .inflate( R.layout.secret_file_layout, viewGroup, false );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder( final ViewHolder viewHolder, final int i )
        {
            new Thread()
            {
                @Override
                public void run()
                {
                    File file = get( i );
                    FileInputStream input = null;
                    ByteArrayOutputStream output = null;
                    try
                    {
                        input = getActivity().openFileInput( file.getName() );

                        output = new ByteArrayOutputStream();
                        SecretStream.decrypt( mPassword, input, output );
                        String text = new String( output.toByteArray(), "UTF-8" );
                        viewHolder.setHolderText( text );
                    }
                    catch( IOException e )
                    {
                        LockAway.log( e );
                        viewHolder.setHolderText( file.getName() );
                    }
                    finally
                    {
                        if( input != null )
                        {
                            try
                            {
                                input.close();
                            }
                            catch( IOException ex )
                            {
                                LockAway.log( ex );
                            }
                        }
                        if( output != null )
                        {
                            try
                            {
                                output.close();
                            }
                            catch( IOException ex )
                            {
                                LockAway.log( ex );
                            }
                        }
                    }
                }
            }.start();
        }
    }
}

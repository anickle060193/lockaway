package com.adamnickle.lockaway;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class SecretFileFragment extends Fragment
{
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

        try
        {
            FileOutputStream output = getActivity().openFileOutput( "test_" + System.currentTimeMillis(), Context.MODE_PRIVATE );
            InputStream input = new ByteArrayInputStream( "This is a test.".getBytes( "UTF-8" ) );
            SecretStream.encrypt( mPassword, input, output );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView Text;

        public ViewHolder( View itemView )
        {
            super( itemView );

            Text = (TextView)itemView;
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
        public void onBindViewHolder( ViewHolder viewHolder, int i )
        {
            File file = get( i );
            try
            {
                FileInputStream input = getActivity().openFileInput( file.getName() );
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                SecretStream.decrypt( mPassword, input, output );
                String text = new String( output.toByteArray(), "UTF-8" );
                viewHolder.Text.setText( text );
            }
            catch( IOException e )
            {
                e.printStackTrace();
                viewHolder.Text.setText( file.getName() );
            }
        }
    }
}

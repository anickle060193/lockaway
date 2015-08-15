package com.adamnickle.lockaway;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class LockedImageFilesFragment extends Fragment implements LockedFilesActivity.LockedFragment
{
    private static final String IMAGES_SUB_DIRECTORY = "images";

    private Key mKey;

    private View mMainView;
    private RecyclerView mRecycleView;

    private LockedImageFilesArrayAdapter mAdapter;

    public static LockedImageFilesFragment newInstance( Key key )
    {
        LockedImageFilesFragment fragment = new LockedImageFilesFragment();
        fragment.mKey = key;
        return fragment;
    }

    public LockedImageFilesFragment() { }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        if( mMainView == null )
        {
            mMainView = inflater.inflate( R.layout.recycle_view, container, false );
            mRecycleView = (RecyclerView)mMainView.findViewById( R.id.recycleView );
            mRecycleView.setLayoutManager( new GridLayoutManager( getActivity(), 3, LinearLayoutManager.VERTICAL, false ) );

            mAdapter = new LockedImageFilesArrayAdapter();
            mRecycleView.setAdapter( mAdapter );

            final File imagesDirectory = new File( getActivity().getFilesDir(), IMAGES_SUB_DIRECTORY );
            final File[] images = imagesDirectory.listFiles();
            if( images != null )
            {
                mAdapter.addAll( Arrays.asList( images ) );
            }
        }
        else
        {
            final ViewGroup parent = (ViewGroup)mMainView.getParent();
            if( parent != null )
            {
                parent.removeView( mMainView );
            }
        }
        return mMainView;
    }

    @Override
    public void add( String filename )
    {
        FileInputStream input = null;
        LockingOutputStream output = null;
        try
        {
            input = new FileInputStream( filename );
            final File outputDirectory = new File( getActivity().getFilesDir(), IMAGES_SUB_DIRECTORY );
            final String lockedFilename = Long.toString( System.currentTimeMillis() ) + "." + Helper.getExtension( filename );
            final File outputFile = new File( outputDirectory, lockedFilename );
            if( !outputDirectory.exists() && !outputDirectory.mkdirs() )
            {
                throw new IOException( "Could not create locked file directories." );
            }
            output = new LockingOutputStream( mKey, outputFile.getAbsolutePath() );

            int b;
            while( ( b = input.read() ) != -1 )
            {
                output.write( b );
            }
            LockAway.toast( "'" + new File( filename ).getName() + "' has been locked away." );
            int i = 0;
            while( i < mAdapter.getItemCount() && lockedFilename.compareTo( mAdapter.get( i ).getName() ) > 0 )
            {
                i++;
            }
            mAdapter.notifyItemInserted( i );
        }
        catch( IOException ex )
        {
            LockAway.log( ex );
        }
        finally
        {
            Helper.close( input );
            Helper.close( output );
        }
    }

    private void unlock( String filename )
    {
        UnlockingInputStream input;
        FileOutputStream output;
        try
        {
            input = new UnlockingInputStream( mKey, filename );
            output = new FileOutputStream( "/mnt/sdcard/DCIM/test" + Helper.getExtension( filename ) );
            int b;
            while( ( b = input.read() ) != -1 )
            {
                output.write( b );
            }
        }
        catch( IOException ex )
        {
            LockAway.log( ex );
        }
    }

    private class LockedImageFilesArrayAdapter extends ArrayRecyclerAdapter<File, ThumbnailViewHolder>
    {
        @Override
        public ThumbnailViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
        {
            final View view = LayoutInflater.from( parent.getContext() )
                    .inflate( R.layout.thumbnail_file_layout, parent, false );
            return new ThumbnailViewHolder( view );
        }

        @Override
        public void onBindViewHolder( final ThumbnailViewHolder holder, int position )
        {
            final File file = get( position );
            final Thread thread = new Thread()
            {
                @Override
                public void run()
                {
                    final int thumbnailSize = getResources().getDimensionPixelSize( R.dimen.thumbnail_size );
                    final Bitmap thumbnail = Helper.createScaledBitmapFromLocked( mKey, file.getAbsolutePath(), thumbnailSize, thumbnailSize );
                    if( thumbnail != null )
                    {
                        final Drawable thumbnailDrawable = new BitmapDrawable( getResources(), thumbnail );
                        holder.setThumbnail( thumbnailDrawable );
                    }
                }
            };
            holder.PopulatingThreadId = thread.getId();
            thread.start();
            holder.MainView.setOnLongClickListener( new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick( View v )
                {
                    unlock( file.getAbsolutePath() );
                    return true;
                }
            } );
            holder.MainView.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    if( file.delete() )
                    {
                        final int index = mAdapter.indexOf( file );
                        mAdapter.notifyItemRemoved( index );
                    }
                }
            } );
        }

        @Override
        public void onViewRecycled( ThumbnailViewHolder holder )
        {
            holder.PopulatingThreadId = -1;
        }
    }
}

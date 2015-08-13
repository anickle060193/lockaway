package com.adamnickle.lockaway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSelectorFragment extends Fragment
{
    private View mMainView;
    private RecyclerView mRecyclerView;
    private TextView mParentDirectory;
    private Button mToParentDirectory;

    private FileSystemRecyclerAdapter mFileSystemAdapter;

    public FileSelectorFragment(){ }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        if( mMainView == null )
        {
            mMainView = inflater.inflate( R.layout.fragment_file_selector, container, false );
            mParentDirectory = (TextView)mMainView.findViewById( R.id.parentDirectory );
            mToParentDirectory = (Button)mMainView.findViewById( R.id.toParentDirectory );
            mToParentDirectory.setOnClickListener( new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    mFileSystemAdapter.gotoParentDirectory();
                }
            } );

            mRecyclerView = (RecyclerView)mMainView.findViewById( R.id.childrenRecyclerView );
            mRecyclerView.setLayoutManager( new LinearLayoutManager( getActivity() ) );
            mFileSystemAdapter = new FileSystemRecyclerAdapter();
            mRecyclerView.setAdapter( mFileSystemAdapter );

            Intent intent = getActivity().getIntent();
            File root = null;
            if( intent != null )
            {
                String filename = intent.getStringExtra( FileSelectorActivity.EXTRA_FILENAME );
                if( filename != null )
                {
                    File file = new File( filename );
                    if( file.exists() )
                    {
                        root = new File( filename );
                    }
                }
            }
            if( root == null )
            {
                root = getActivity().getFilesDir();
                File parent;
                while( ( parent = root.getParentFile() ) != null )
                {
                    root = parent;
                }
            }
            mFileSystemAdapter.setDirectory( root );
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
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );

        final View view = getView();
        if( view != null )
        {
            view.setFocusableInTouchMode( true );
            view.requestFocus();
            view.setOnKeyListener( new View.OnKeyListener()
            {
                @Override
                public boolean onKey( View v, int keyCode, KeyEvent event )
                {
                    if( event.getAction() == KeyEvent.ACTION_DOWN )
                    {
                        if( keyCode == KeyEvent.KEYCODE_BACK )
                        {
                            return mFileSystemAdapter.gotoParentDirectory();
                        }
                    }
                    return false;
                }
            } );
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder
    {
        View MainView;
        ImageView Icon;
        TextView Name;
        TextView Size;

        long PopulatingThreadId;

        public ViewHolder( View itemView )
        {
            super( itemView );

            MainView = itemView;
            Icon = (ImageView)MainView.findViewById( R.id.icon );
            Name = (TextView)MainView.findViewById( R.id.item_name );
            Size = (TextView)MainView.findViewById( R.id.size );
        }

        public void setIcon( final Bitmap bitmap )
        {
            if( Thread.currentThread().getId() == PopulatingThreadId )
            {
                getActivity().runOnUiThread( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Icon.setImageBitmap( bitmap );
                    }
                } );
            }
        }
    }

    private class FileSystemRecyclerAdapter extends ArrayRecyclerAdapter<File, ViewHolder>
    {
        private File mCurrentDirectory;

        public void setDirectory( File file )
        {
            if( file.exists() )
            {
                final File directory = file.isDirectory() ? file : file.getParentFile();
                final File[] children = directory.listFiles();
                if( !file.canRead() )
                {
                    LockAway.toast( "Access denied to '" + directory.getName() + "'." );
                }
                else if( children != null )
                {
                    this.clear();
                    Arrays.sort( children, mFileSorter );
                    this.addAll( Arrays.asList( children ) );

                    mCurrentDirectory = directory;
                    update();
                }
            }
        }

        private void update()
        {
            mToParentDirectory.setEnabled( mCurrentDirectory.getParentFile() != null );
            mParentDirectory.setText( mCurrentDirectory.getAbsolutePath() );
        }

        public boolean gotoParentDirectory()
        {
            if( mCurrentDirectory != null )
            {
                final File parent = mCurrentDirectory.getParentFile();
                if( parent != null )
                {
                    setDirectory( parent );
                    return true;
                }
            }
            return false;
        }

        private final Comparator<File> mFileSorter = new Comparator<File>()
        {
            @Override
            public int compare( File lhs, File rhs )
            {
                if( lhs == null )
                {
                    return rhs == null ? 0 : 1;
                }
                else if( rhs == null )
                {
                    return -1;
                }
                else if( lhs.isDirectory() )
                {
                    if( !rhs.isDirectory() )
                    {
                        return -1;
                    }
                }
                else if( rhs.isDirectory() )
                {
                    return 1;
                }
                return lhs.getName().compareTo( rhs.getName() );
            }
        };

        @Override
        public ViewHolder onCreateViewHolder( ViewGroup parent, int viewType )
        {
            final View view = LayoutInflater
                    .from( parent.getContext() )
                    .inflate( R.layout.file_system_item_layout, parent, false );
            return new ViewHolder( view );
        }

        @Override
        public void onBindViewHolder( final ViewHolder holder, int position )
        {
            final File file = get( position );
            if( file.isDirectory() )
            {
                holder.Icon.setImageResource( R.drawable.ic_folder_white_48dp );
                holder.Size.setVisibility( View.GONE );

                holder.MainView.setOnClickListener( new View.OnClickListener()
                {
                    @Override
                    public void onClick( View v )
                    {
                        FileSystemRecyclerAdapter.this.setDirectory( file );
                    }
                } );
            }
            else
            {
                holder.Icon.setImageResource( R.drawable.ic_insert_drive_file_white_48dp );
                holder.Size.setVisibility( View.VISIBLE );
                holder.Size.setText( Formatter.formatFileSize( getActivity(), file.length() ) );
                holder.MainView.setClickable( false );

                final Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        final int thumbnailSize = getActivity().getResources().getDimensionPixelSize( R.dimen.file_item_thumbnail );
                        Bitmap icon = BitmapHelper.createScaledBitmap( file.getAbsolutePath(), thumbnailSize, thumbnailSize );
                        if( icon != null )
                        {
                            holder.setIcon( icon );
                            return;
                        }
                        icon = ThumbnailUtils.createVideoThumbnail( file.getAbsolutePath(), MediaStore.Images.Thumbnails.MICRO_KIND );
                        if( icon != null )
                        {
                            holder.setIcon( icon );
                            return;
                        }
                    }
                };
                holder.PopulatingThreadId = t.getId();
                t.start();
            }
            holder.Name.setText( file.getName() );
        }

        @Override
        public void onViewRecycled( ViewHolder holder )
        {
            holder.PopulatingThreadId = -1;
        }
    }
}

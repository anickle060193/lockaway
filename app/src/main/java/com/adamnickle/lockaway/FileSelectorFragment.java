package com.adamnickle.lockaway;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A placeholder fragment containing a simple view.
 */
public class FileSelectorFragment extends Fragment
{
    private View mMainView;
    private RecyclerView mRecyclerView;
    private TextView mParentDirectory;
    private ImageButton mToParentDirectory;

    private ViewHolder mSelectedFile;

    private FileSystemRecyclerAdapter mFileSystemAdapter;

    public FileSelectorFragment(){ }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        if( mMainView == null )
        {
            mMainView = inflater.inflate( R.layout.fragment_file_selector, container, false );
            mParentDirectory = (TextView)mMainView.findViewById( R.id.parentDirectory );
            mToParentDirectory = (ImageButton)mMainView.findViewById( R.id.toParentDirectory );
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

            final Intent data = getActivity().getIntent();
            final String[] extensions = data.getStringArrayExtra( FileSelectorActivity.EXTRA_FILE_EXTENSIONS );
            mFileSystemAdapter.setFileTypes( extensions );

            mFileSystemAdapter.setDirectory( getInitialDirectory() );
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

    private File getInitialDirectory()
    {
        final Intent intent = getActivity().getIntent();
        if( intent != null )
        {
            final String filename = intent.getStringExtra( FileSelectorActivity.EXTRA_INITIAL_DIRECTORY );
            if( filename != null )
            {
                final File file = new File( filename );
                if( file.exists() )
                {
                    return new File( filename );
                }
            }
        }
        File root = getActivity().getFilesDir();
        File parent;
        while( ( parent = root.getParentFile() ) != null )
        {
            root = parent;
        }
        return root;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState )
    {
        super.onActivityCreated( savedInstanceState );

        getActivity().setResult( Activity.RESULT_CANCELED );
    }

    public boolean onBackPressed()
    {
        return mFileSystemAdapter.gotoParentDirectory();
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
    {
        inflater.inflate( R.menu.menu_file_selector, menu );
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        switch( item.getItemId() )
        {
            case R.id.done:
                Intent data = new Intent();
                data.putExtra( FileSelectorActivity.EXTRA_FILENAME, mSelectedFile.File.getAbsolutePath() );
                getActivity().setResult( Activity.RESULT_OK, data );
                getActivity().finish();
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }

    private void update()
    {
        mToParentDirectory.setEnabled( mFileSystemAdapter.getDirectory().getParentFile() != null );
        mParentDirectory.setText( mFileSystemAdapter.getDirectory().getAbsolutePath() );
        if( mSelectedFile != null )
        {
            setHasOptionsMenu( true );
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder
    {
        File File;

        View MainView;
        ImageView Icon;
        TextView Name;
        TextView Size;

        long PopulatingThreadId;

        private boolean mSelected = false;

        public ViewHolder( View itemView )
        {
            super( itemView );

            MainView = itemView;
            Icon = (ImageView)MainView.findViewById( R.id.icon );
            Name = (TextView)MainView.findViewById( R.id.item_name );
            Size = (TextView)MainView.findViewById( R.id.size );
        }

        public void setIcon( final Drawable icon )
        {
            if( Thread.currentThread().getId() == PopulatingThreadId )
            {
                Icon.post( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Icon.setImageDrawable( icon );
                    }
                } );
            }
        }

        public void toggleSelect()
        {
            if( this.mSelected )
            {
                deselect();
            }
            else
            {
                select();
            }
        }

        public void select()
        {
            if( mSelectedFile != null )
            {
                mSelectedFile.deselect();
            }
            mSelectedFile = this;
            this.mSelected = true;
            MainView.setBackgroundResource( R.color.bright_blue );
            update();
        }

        public void deselect()
        {
            MainView.setBackgroundResource( R.color.background_color );
            mSelectedFile = null;
            this.mSelected = false;
            update();
        }
    }

    private class FileSystemRecyclerAdapter extends ArrayRecyclerAdapter<File, ViewHolder>
    {
        private File mCurrentDirectory;
        private FileFilter mFileFilter;

        public void setFileTypes( final String[] extensions )
        {
            if( extensions != null )
            {
                final HashSet<String> extensionSet = new HashSet<>( Arrays.asList( extensions ) );
                mFileFilter = new FileFilter()
                {
                    @Override
                    public boolean accept( File file )
                    {
                        if( file.isDirectory() )
                        {
                            return true;
                        }
                        else if( file.isFile() )
                        {
                            final String extension = Helper.getExtension( file );
                            return extensionSet.contains( extension );
                        }
                        else
                        {
                            return false;
                        }
                    }
                };
            }
        }

        public File getDirectory()
        {
            return mCurrentDirectory;
        }

        public void setDirectory( File file )
        {
            if( file.exists() )
            {
                final File directory = file.isDirectory() ? file : file.getParentFile();
                final File[] children = directory.listFiles( mFileFilter );
                if( !file.canRead() )
                {
                    LockAway.toast( "Access denied to '" + directory.getName() + "'." );
                }
                else if( children != null )
                {
                    this.clear();
                    Helper.sort( children );
                    this.addAll( Arrays.asList( children ) );

                    mCurrentDirectory = directory;
                    update();
                }
            }
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
            holder.File = get( position );
            holder.deselect();

            if( holder.File.isDirectory() )
            {
                if( holder.File.canRead() )
                {
                    holder.Icon.setImageResource( R.drawable.ic_folder_open_white_48dp );
                }
                else
                {
                    holder.Icon.setImageResource( R.drawable.ic_locked_folder );
                }
                holder.Size.setVisibility( View.GONE );

                holder.MainView.setOnClickListener( new View.OnClickListener()
                {
                    @Override
                    public void onClick( View v )
                    {
                        FileSystemRecyclerAdapter.this.setDirectory( holder.File );
                    }
                } );
            }
            else
            {
                holder.Icon.setImageResource( R.drawable.ic_file );
                holder.Size.setVisibility( View.VISIBLE );
                holder.Size.setText( Formatter.formatFileSize( getActivity(), holder.File.length() ) );
                holder.MainView.setOnClickListener( new View.OnClickListener()
                {
                    @Override
                    public void onClick( View v )
                    {
                        holder.toggleSelect();
                    }
                } );

                final Thread t = new Thread()
                {
                    @Override
                    public void run()
                    {
                        final Drawable icon = Helper.getThumbnail( getActivity().getResources(), holder.File.getAbsolutePath() );
                        holder.setIcon( icon );
                    }
                };
                holder.PopulatingThreadId = t.getId();
                t.start();
            }
            holder.Name.setText( holder.File.getName() );
        }

        @Override
        public void onViewRecycled( ViewHolder holder )
        {
            holder.PopulatingThreadId = -1;
        }
    }
}

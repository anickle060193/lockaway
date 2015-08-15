package com.adamnickle.lockaway;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;


public class SecretImageFileFragment extends Fragment
{
    private Password mPassword;

    private View mMainView;
    private RecyclerView mRecycleView;

    public static SecretImageFileFragment newInstance( Password password )
    {
        SecretImageFileFragment fragment = new SecretImageFileFragment();
        fragment.mPassword = password;
        return fragment;
    }

    public SecretImageFileFragment() { }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        return inflater.inflate( R.layout.recycle_view, container, false );
    }

    private class SecretImageFileArrayAdapter extends ArrayRecyclerAdapter<File, ThumbnailViewHolder>
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
                    final Bitmap thumbnail = Helper.createScaledBitmapFromSecret( mPassword, file.getAbsolutePath(), thumbnailSize, thumbnailSize );
                    final Drawable thumbnailDrawable = new BitmapDrawable( getResources(), thumbnail );
                    holder.setThumbnail( thumbnailDrawable );
                }
            };
            holder.PopulatingThreadId = thread.getId();
            thread.start();
        }

        @Override
        public void onViewRecycled( ThumbnailViewHolder holder )
        {
            holder.PopulatingThreadId = -1;
        }
    }
}

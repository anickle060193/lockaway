package com.adamnickle.lockaway;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Adam on 8/14/2015.
 */
public class ThumbnailViewHolder extends RecyclerView.ViewHolder
{
    View MainView;
    ImageView Thumbnail;

    long PopulatingThreadId = -1;

    public ThumbnailViewHolder( View itemView )
    {
        super( itemView );

        MainView = itemView;
        Thumbnail = (ImageView)MainView.findViewById( R.id.thumbnail );
    }

    public void setThumbnail( final Drawable thumbnail )
    {
        if( PopulatingThreadId == Thread.currentThread().getId() )
        {
            Thumbnail.post( new Runnable()
            {
                @Override
                public void run()
                {
                    Thumbnail.setImageDrawable( thumbnail );
                }
            } );
        }
    }
}

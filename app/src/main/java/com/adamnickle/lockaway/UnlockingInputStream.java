package com.adamnickle.lockaway;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Adam on 8/12/2015.
 */
public class UnlockingInputStream extends InputStream
{
    private final Key mKey;
    private final InputStream mInput;
    private int mByteOffset = 0;

    public UnlockingInputStream( Key key, InputStream inputStream )
    {
        mKey = key;
        mInput = inputStream;
    }

    public UnlockingInputStream( Key key, String filename ) throws FileNotFoundException
    {
        this( key, new FileInputStream( filename ) );
    }

    @Override
    public int available() throws IOException
    {
        return mInput.available();
    }

    @Override
    public int read() throws IOException
    {
        int b = mInput.read();
        if( b != -1 )
        {
            b = mKey.decryptData( b, mByteOffset );
            mByteOffset++;
        }
        return b;
    }

    @Override
    public void close() throws IOException
    {
        mInput.close();
    }
}

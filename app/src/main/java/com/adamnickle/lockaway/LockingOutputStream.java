package com.adamnickle.lockaway;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Adam on 8/12/2015.
 */
public class LockingOutputStream extends OutputStream
{
    private final Key mKey;
    private final OutputStream mOutput;
    private int mByteOffset = 0;

    public LockingOutputStream( Key key, OutputStream outputStream )
    {
        mKey = key;
        mOutput = outputStream;
    }

    public LockingOutputStream( Key key, String filename ) throws FileNotFoundException
    {
        this( key, new FileOutputStream( filename ) );
    }

    @Override
    public void write( int oneByte ) throws IOException
    {
        mOutput.write( mKey.encryptData( oneByte, mByteOffset ) );
        mByteOffset++;
    }

    @Override
    public void close() throws IOException
    {
        mOutput.close();
    }

    @Override
    public void flush() throws IOException
    {
        mOutput.flush();
    }
}

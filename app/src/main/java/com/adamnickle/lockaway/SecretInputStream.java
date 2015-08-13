package com.adamnickle.lockaway;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Adam on 8/12/2015.
 */
public class SecretInputStream extends InputStream
{
    private final Password mPassword;
    private final InputStream mInput;
    private int mByteOffset = 0;

    public SecretInputStream( Password pw, InputStream inputStream )
    {
        mPassword = pw;
        mInput = inputStream;
    }

    public SecretInputStream( Password pw, String filename ) throws FileNotFoundException
    {
        this( pw, new FileInputStream( filename ) );
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
            b = mPassword.decryptData( b, mByteOffset );
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

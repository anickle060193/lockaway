package com.adamnickle.lockaway;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Adam on 8/12/2015.
 */
public class SecretOutputStream extends OutputStream
{
    private final Password mPassword;
    private final OutputStream mOutput;
    private int mByteOffset = 0;

    public SecretOutputStream( Password pw, OutputStream outputStream )
    {
        mPassword = pw;
        mOutput = outputStream;
    }

    public SecretOutputStream( Password pw, String filename ) throws FileNotFoundException
    {
        this( pw, new FileOutputStream( filename ) );
    }

    @Override
    public void write( int oneByte ) throws IOException
    {
        mOutput.write( mPassword.encryptData( oneByte, mByteOffset ) );
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

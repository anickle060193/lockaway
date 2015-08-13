package com.adamnickle.lockaway;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Adam on 8/8/2015.
 */
public abstract class SecretStream
{
    private SecretStream() { }

    public static void encrypt( Password pw, InputStream plainText, OutputStream encrypted ) throws IOException
    {
        int b;
        int byteCount = 0;
        while( ( b = plainText.read() ) != -1 )
        {
            encrypted.write( pw.encryptData( b, byteCount ) );
            byteCount++;
        }
    }

    public static void decrypt( Password pw, InputStream encrypted, OutputStream decrypted ) throws IOException
    {
        int b;
        int byteCount = 0;
        while( ( b = encrypted.read() ) != -1 )
        {
            decrypted.write( pw.decryptData( b, byteCount ) );
            byteCount++;
        }
    }

    public static boolean encryptAndSave( Context context, Password password, String fileName )
    {
        FileInputStream input = null;
        FileOutputStream output = null;
        try
        {
            input = new FileInputStream( fileName );
            File file = new File( fileName );
            output = context.openFileOutput( file.getName(), Context.MODE_PRIVATE );
            SecretStream.encrypt( password, input, output );
            output.flush();
            return true;
        }
        catch( IOException ex )
        {
            LockAway.log( ex );
        }
        finally
        {
            if( input != null )
            {
                try
                {
                    input.close();
                }
                catch( IOException ex )
                {
                    LockAway.log( ex );
                }
            }
            if( output != null )
            {
                try
                {
                    output.close();
                }
                catch( IOException ex )
                {
                    LockAway.log( ex );
                }
            }
        }
        return false;
    }
}

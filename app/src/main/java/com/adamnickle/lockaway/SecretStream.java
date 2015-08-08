package com.adamnickle.lockaway;

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
}

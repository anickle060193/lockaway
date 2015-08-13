package com.adamnickle.lockaway;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Adam on 8/8/2015.
 */
public class Password
{
    private byte[] mDigest;
    private int mDigestLength;

    public Password( String pw )
    {
        try
        {
            MessageDigest digester = MessageDigest.getInstance( "SHA256" );
            digester.update( pw.getBytes() );
            mDigest = digester.digest();
        }
        catch( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
            LockAway.log( e.getMessage() );
            mDigest = new byte[]{ 0 };
        }
        mDigestLength = mDigest.length;
    }

    public int encryptData( int input, int byteOffset )
    {
        return input ^ mDigest[ byteOffset % mDigestLength ];
    }

    public int decryptData( int input, int byteOffset )
    {
        return encryptData( input, byteOffset );
    }
}

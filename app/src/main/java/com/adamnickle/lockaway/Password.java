package com.adamnickle.lockaway;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Adam on 8/8/2015.
 */
public class Password implements Parcelable
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

    protected Password( Parcel in )
    {
        mDigest = in.createByteArray();
        mDigestLength = in.readInt();
    }

    public int encryptData( int input, int byteOffset )
    {
        return input ^ mDigest[ byteOffset % mDigestLength ];
    }

    public int decryptData( int input, int byteOffset )
    {
        return encryptData( input, byteOffset );
    }

    public static final Creator<Password> CREATOR = new Creator<Password>()
    {
        @Override
        public Password createFromParcel( Parcel in )
        {
            return new Password( in );
        }

        @Override
        public Password[] newArray( int size )
        {
            return new Password[ size ];
        }
    };

    @Override
    public void writeToParcel( Parcel dest, int flags )
    {
        dest.writeByteArray( mDigest );
        dest.writeInt( mDigestLength );
    }

    @Override
    public int describeContents()
    {
        return 0;
    }
}

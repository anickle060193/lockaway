package com.adamnickle.lockaway;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Adam on 8/8/2015.
 */
public class Key implements Parcelable
{
    private byte[] mDigest;
    private int mDigestLength;

    public Key( String locker )
    {
        try
        {
            MessageDigest digester = MessageDigest.getInstance( "SHA256" );
            digester.update( locker.getBytes() );
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

    protected Key( Parcel in )
    {
        mDigest = in.createByteArray();
        mDigestLength = in.readInt();
    }

    public int lock( int input, int byteOffset )
    {
        return input ^ mDigest[ byteOffset % mDigestLength ];
    }

    public int unlock( int input, int byteOffset )
    {
        return lock( input, byteOffset );
    }

    public static final Creator<Key> CREATOR = new Creator<Key>()
    {
        @Override
        public Key createFromParcel( Parcel in )
        {
            return new Key( in );
        }

        @Override
        public Key[] newArray( int size )
        {
            return new Key[ size ];
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

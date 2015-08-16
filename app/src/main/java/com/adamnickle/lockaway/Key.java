package com.adamnickle.lockaway;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Adam on 8/8/2015.
 */
public class Key
{
    private static final byte[] SALT = new byte[]{
            -126, -37, -29, 86, -81, 114, 9, -37, 96, -7, 87, 25, 23, 0, 127, -40, -72, 101, 48, -108,
            -121, 92, 57, -43, -83, 21, -93, -6, 30, 22, -31, -44, 82, 123, 51, 126, -48, -2, -100, -102,
            1, -58, 86, 53, 35, -124, 16, 48, 12, 60, 122, -122, -123, 82, -118, -114, 28, 65, -53, -21,
            -91, 76, 25, -38, 82, -39, -89, 102, 88, -66, -110, 8, 34, -60, -69, -44, -23, 88, 71, 61,
            20, 63, -68, -5, -87, 11, 52, -120, -38, 3, -36, 95, -121, -125, -20, -85, -63, -77, 2, -16,
            -127, -66, -40, -74, 58, -56, 55, 46, -62, 112, 88, 110, 109, 0, -24, 96, 68, -118, -7, 27,
            27, -10, 70, -40, 34, -44, 28, 105, 115, 69, -103, 112, 87, 12, 38, 43, -94, 77, 5, -30,
            -30, -6, 55, 116, 65, 114, -68, -59, 80, 63, -18, -101, -55, 39, 13, 19, -109, -21, -114, 43,
            49, -29, 100, 33, -97, 39, -89, 27, 7, -128, -93, 118, 14, 90, 62, 34, 116, 48, -93, 57,
            -45, -107, -11, 120, -61, -34, 23, 56, -16, 18, -101, -61, -8, -72, -104, -19, -27, 57, -92, 126,
            115, -112, -124, -39, -111, -19, 13, -57, -68, -81, 112, -48, 47, 112, -8, -50, -1, -3, 92, -14,
            1, 44, -3, 48, 3, 104, 22, -37, 90, 93, -107, 16, -8, -45, 86, 51, -3, -7, -1, -41,
            -59, -88, -127, -5, 120, -103, -103, 36, 29, 66, -9, 92, 51, -113, -37, -87
    };

    private Cipher mUnlocker;
    private Cipher mLocker;

    public Key( String passcode )
    {
        try
        {
            final SecretKeyFactory factory = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA1" );
            final KeySpec spec = new PBEKeySpec( passcode.toCharArray(), SALT, 65536, 128 );
            final SecretKey temp = factory.generateSecret( spec );
            final SecretKey secret = new SecretKeySpec( temp.getEncoded(), "AES" );
            mLocker = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
            mLocker.init( Cipher.ENCRYPT_MODE, secret );

            final AlgorithmParameters params = mLocker.getParameters();
            final byte[] iv = params.getParameterSpec( IvParameterSpec.class ).getIV();
            mUnlocker = Cipher.getInstance( "AES/CBC/PKCS5Padding" );
            mUnlocker.init( Cipher.DECRYPT_MODE, secret, new IvParameterSpec( iv ) );
        }
        catch( NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | InvalidParameterSpecException ex )
        {
            LockAway.log( ex );
            throw new RuntimeException( "An error occurred while setting up the Key.", ex );
        }
    }

    public Cipher getLocker()
    {
        return mLocker;
    }

    public Cipher getUnlocker()
    {
        return mUnlocker;
    }
}

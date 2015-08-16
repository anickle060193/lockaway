package com.adamnickle.lockaway;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;


public class LockUnlockTest
{
    @Test
    public void test()
    {
        final Random r = new Random( 1 );

        final Key key = new Key( "1234" );

        final byte[] data = new byte[ 1024 ];
        r.nextBytes( data );

        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        final ByteArrayInputStream input = new ByteArrayInputStream( data );

        final CipherInputStream unlockingInput = new CipherInputStream( input, key.getLocker() );
        final CipherOutputStream lockingOutput = new CipherOutputStream( output, key.getUnlocker() );

        try
        {
            int b;
            while( ( b = unlockingInput.read() ) != -1 )
            {
                lockingOutput.write( b );
            }
        }
        catch( IOException e )
        {
            fail( "An unexpected exception occurred:\n" + e.toString() );
        }

        final byte[] actual = output.toByteArray();
        System.out.println( Arrays.toString( actual ) );
        System.out.println( Arrays.toString( data ) );

        assertArrayEquals( actual, data );
    }
}
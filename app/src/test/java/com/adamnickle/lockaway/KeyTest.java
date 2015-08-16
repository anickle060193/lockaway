package com.adamnickle.lockaway;

import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class KeyTest
{
    @Test
    public void test()
    {
        final Key key = new Key( "1234" );
        assertTrue( "Encryption/Decryption is not a linear conversion.", key.getUnlocker().getOutputSize( 100 ) == 100 );
    }
}
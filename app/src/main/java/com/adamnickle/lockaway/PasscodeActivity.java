package com.adamnickle.lockaway;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class PasscodeActivity extends AppCompatActivity
{
    public static final String EXTRA_PASSCODE = "extra_passcode";

    private static final int[] KEYPAD_BUTTON_IDS = { R.id.b0, R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9 };
    private static final int PASSCODE_LENGTH = 4;

    private TextView mPromptTextView;
    private TextView mPasscodeTextView;
    private Button[] mKeypad;
    private ImageButton mEnter;
    private ImageButton mDelete;

    private String mPasscode = "";

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_passcode );
        setResult( Activity.RESULT_CANCELED );

        mPromptTextView = (TextView)findViewById( R.id.promptText );
        mPasscodeTextView = (TextView)findViewById( R.id.passcodeText );

        final View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                onButtonClick( v );
            }
        };

        mKeypad = new Button[ KEYPAD_BUTTON_IDS.length ];
        for( int i = 0; i < KEYPAD_BUTTON_IDS.length; i++ )
        {
            mKeypad[ i ] = (Button)findViewById( KEYPAD_BUTTON_IDS[ i ] );
            mKeypad[ i ].setOnClickListener( onClickListener );
        }

        mEnter = (ImageButton)findViewById( R.id.enter );
        mEnter.setOnClickListener( onClickListener );

        mDelete = (ImageButton)findViewById( R.id.delete );
        mDelete.setOnClickListener( onClickListener );
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        mPasscode = "";
        updateDisplay();
    }

    private void onButtonClick( View b )
    {
        if( b == mEnter )
        {
            if( mPasscode.length() == PASSCODE_LENGTH )
            {
                final Intent intent = new Intent( this, LockedFilesActivity.class );
                intent.putExtra( LockedFilesActivity.EXTRA_PASSCODE, mPasscode );
                startActivity( intent );
            }
        }
        else if( b == mDelete )
        {
            int len = mPasscode.length();
            if( len > 0 )
            {
                mPasscode = mPasscode.substring( 0, len - 1 );
            }
        }
        else
        {
            if( mPasscode.length() < PASSCODE_LENGTH )
            {
                mPasscode += ( (Button)b ).getText();
            }
        }

        updateDisplay();
    }

    private void updateDisplay()
    {
        mPasscodeTextView.setText( mPasscode );

        if( mPasscode.length() == 0 )
        {
            mPromptTextView.setVisibility( View.VISIBLE );
            mPasscodeTextView.setVisibility( View.GONE );
        }
        else
        {
            mPromptTextView.setVisibility( View.GONE );
            mPasscodeTextView.setVisibility( View.VISIBLE );
        }
    }
}

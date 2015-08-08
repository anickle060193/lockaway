package com.adamnickle.lockaway;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class PasscodeFragment extends Fragment
{
    // Constant DDecelerations
    private static final int[] KEYPAD_BUTTON_IDS = { R.id.b0, R.id.b1, R.id.b2, R.id.b3, R.id.b4, R.id.b5, R.id.b6, R.id.b7, R.id.b8, R.id.b9 };
    private static final int PASSCODE_LENGTH = 4;

    // UI Elements
    private View mMainView;
    private TextView mPromptTextView;
    private TextView mPasscodeTextView;
    private Button[] mKeypad;
    private Button mEnter;
    private Button mDelete;

    private String mPasscode;

    public static PasscodeFragment newInstance()
    {
        return new PasscodeFragment();
    }

    public PasscodeFragment(){ }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setRetainInstance( true );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
    {
        if( mMainView == null )
        {
            mMainView = inflater.inflate( R.layout.fragment_passcode, container, false );

            mPromptTextView = (TextView)mMainView.findViewById( R.id.promptText );
            mPasscodeTextView = (TextView)mMainView.findViewById( R.id.passcodeText );

            View.OnClickListener onClickListener = new View.OnClickListener()
            {
                @Override
                public void onClick( View v )
                {
                    onButtonClick( (Button)v );
                }
            };

            mKeypad = new Button[ KEYPAD_BUTTON_IDS.length ];
            for( int i = 0; i < KEYPAD_BUTTON_IDS.length; i++ )
            {
                mKeypad[ i ] = (Button)mMainView.findViewById( KEYPAD_BUTTON_IDS[ i ] );
                mKeypad[ i ].setOnClickListener( onClickListener );
            }

            mEnter = (Button)mMainView.findViewById( R.id.enter );
            mEnter.setOnClickListener( onClickListener );

            mDelete = (Button)mMainView.findViewById( R.id.delete );
            mDelete.setOnClickListener( onClickListener );
        }
        else
        {
            ViewGroup parent = (ViewGroup)mMainView.getParent();
            if( parent != null )
            {
                parent.removeView( mMainView );
            }
        }

        mPasscode = "";
        updateDisplay();
        return mMainView;
    }

    private void onButtonClick( Button b )
    {
        if( b == mEnter )
        {
            if( mPasscode.length() == PASSCODE_LENGTH )
            {
                Password password = new Password( mPasscode );
                FragmentHelper.transition( getFragmentManager(), R.id.content, SecretFileFragment.newInstance( password ) );
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
                mPasscode += b.getText();
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

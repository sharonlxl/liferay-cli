package com.liferay.cli.shell.osgi;

import com.liferay.cli.support.logging.HandlerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

/**
 * @author Sharon Li
 */
public abstract class BaseOSCommandProvider implements OSCommandProvider
{
    private static final Logger LOGGER = HandlerUtils.getLogger( BaseOSCommandProvider.class );

    protected void execute( String command, String argLines, File dir )
    {
        Process p = null;
        try
        {
            p = Runtime.getRuntime().exec( command + " " + argLines, null, dir );

            p.waitFor();

            InputStream errors = p.getErrorStream();

            InputStream input = p.getInputStream();

            if ( errors.available() > 0 )
            {
                printMessage( errors );
            }
            else
            {
                if( input.available() > 0 )
                {
                    printMessage( input );
                }
            }
        }
        catch( Exception e )
        {
            LOGGER.warning( "The command did not complete successfully" );
            return;
        }
    }

    protected String getWorkingDir()
    {
        //There should be a method that get the directory of "Ray"
        return System.getProperty( "user.dir" );
    }

    private void printMessage( InputStream contents )
    {

        final char[] buffer = new char[0x10000];

        StringBuilder out = new StringBuilder();

        Reader in = new InputStreamReader( contents );

        try
        {
            int read;
            do
            {
                read = in.read( buffer, 0, buffer.length );

                if( read > 0 )
                {
                    out.append( buffer, 0, read );
                }
            }
            while( read >= 0 );

            if( out.toString().equals( "" ) ) {
                return;
            }

            System.out.println( out.toString() );
        }
        catch( IOException e )
        {
            e.printStackTrace();
        }
    }

}

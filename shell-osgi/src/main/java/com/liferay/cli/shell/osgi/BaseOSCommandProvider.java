package com.liferay.cli.shell.osgi;

import com.liferay.cli.metadata.MetadataService;
import com.liferay.cli.shell.Shell;
import com.liferay.cli.support.logging.HandlerUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Logger;

/**
 * @author Sharon Li
 * @author Gregory Amerson
 */
public abstract class BaseOSCommandProvider implements OSCommandProvider
{
    private static final Logger LOGGER = HandlerUtils.getLogger( BaseOSCommandProvider.class );

    private Shell shell;

    private MetadataService metadataService;

    public BaseOSCommandProvider( Shell shell, MetadataService metadataService )
    {
        this.shell = shell;
        this.metadataService = metadataService;
    }

    @Override
    public void delete( String fileName )
    {
        if( fileName != null )
        {
            if( new File( fileName ).isDirectory() )
            {
                execute( getRemoveDirectoryCommand(), fileName, getWorkingDir() );
            }
            else
            {
                execute( getRemoveFileCommand(), fileName, getWorkingDir() );
            }
        }
    }

    protected void execute( String command, String argLines, File workingDir )
    {
        try
        {
            final Process p = Runtime.getRuntime().exec( command + " " + argLines, null, workingDir );

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
        }
    }

    protected abstract String getListCommand();

    protected abstract String getMkdirCommand();

    protected abstract String getRemoveDirectoryCommand();

    protected abstract String getRemoveFileCommand();

    protected File getWorkingDir()
    {
        return new File( shell.getWorkingDir() );
    }

    @Override
    public void list( String pathName )
    {
        if( pathName == null )
        {
            pathName = "";
        }

        execute( getListCommand(), pathName, getWorkingDir() );
    }

    @Override
    public void mkdir( String dirName )
    {
        if( dirName != null )
        {
            execute( getMkdirCommand(), dirName, getWorkingDir() );
        }
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

            if( out.toString().equals( "" ) )
            {
                return;
            }

            System.out.println( out.toString() );
        }
        catch( IOException e )
        {
            LOGGER.warning( e.getMessage() );
        }
    }

}

package com.liferay.cli.shell.osgi;

import java.io.File;

/**
 * @author Sharon Li
 */
public class UnixOSCommandProvider extends BaseOSCommandProvider
{

    private static final String REMOVE_FILE_COMMAND = "rm";
    private static final String REMOVE_DIRECTORY_COMMAND = "rm -r";
    private static final String LS_COMMAND = "ls -l";
    private static final String MKDIR_COMMAND = "mkdir -p";

    public UnixOSCommandProvider()
    {
    }

    @Override
    public void delete( String fileName )
    {
        if( fileName == null )
        {
            fileName = "";
        }

        if( new File( fileName ).isDirectory() )
        {
            execute( REMOVE_DIRECTORY_COMMAND, fileName, null );
        }
        else
        {
            execute( REMOVE_FILE_COMMAND, fileName, null );
        }
    }

    @Override
    public void list( String pathName )
    {
        if( pathName == null )
        {
            pathName = getWorkingDir();
        }

        execute( LS_COMMAND, pathName, null );
    }

    @Override
    public void mkdir( String dirName )
    {
        if( dirName == null )
        {
            dirName = "";
        }

        execute( MKDIR_COMMAND, dirName, null );
    }

}

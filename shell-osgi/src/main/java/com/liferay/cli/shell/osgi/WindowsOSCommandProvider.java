package com.liferay.cli.shell.osgi;

import java.io.File;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

@Component
@Service
public class WindowsOSCommandProvider extends BaseOSCommandProvider
{

    private static final String DIR_COMMAND = "cmd /C dir";
    private static final String DELETE_FILE_COMMAND = "cmd /C del /Q";
    private static final String DELETE_DIRECTORY_COMMAND = "cmd /C rmdir";
    private static final String MKDIR_COMMAND = "cmd /C mkdir";

    public WindowsOSCommandProvider()
    {
    }

    @Override
    public void delete( String fileName )
    {
        if (fileName == null)
        {
            fileName = "";
        }

        if( new File( fileName ).isDirectory() )
        {
            execute( DELETE_DIRECTORY_COMMAND, fileName, null );
        }
        else
        {
            execute( DELETE_FILE_COMMAND, fileName, null );
        }
    }

    @Override
    public void list( String pathName )
    {
        if( pathName == null )
        {
            pathName = getWorkingDir();
        }

        execute( DIR_COMMAND, pathName, null );
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

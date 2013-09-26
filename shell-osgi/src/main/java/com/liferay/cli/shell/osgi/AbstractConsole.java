package com.liferay.cli.shell.osgi;

import java.io.File;
import java.io.IOException;


/**
 * @author Gregory Amerson
 */
public abstract class AbstractConsole implements ExternalConsole
{

    @Override
    public Process execute( String workingDir, String cmd, String argLine )
    {
        Runtime runtime = Runtime.getRuntime();

        try
        {
            return runtime.exec( getExecCommand( cmd, argLine ), null, new File( workingDir ) );
        }
        catch( IOException e )
        {
            // TODO RAY handle this better
            e.printStackTrace();
        }

        return null;
    }

    protected abstract String getExecCommand( String cmd, String argLine );

}

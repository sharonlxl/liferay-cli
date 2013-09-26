package com.liferay.cli.shell.osgi;


/**
 * @author Gregory Amerson
 */
public interface ExternalConsole
{

    Process execute( String workingDir, String cmd, String argLine );

}

package com.liferay.cli.shell.osgi;



/**
 * @author Gregory Amerson
 */
public class CmdConsole extends AbstractConsole
{

    @Override
    public String getExecCommand( String cmd, String argLine )
    {
        return "cmd.exe /c start cmd.exe /k " + cmd + " " + argLine;
    }

    @Override
    protected String getExecQuietCommand( String cmd, String argLine )
    {
        return "cmd.exe /c /k " + cmd + " " + argLine;
    }

}

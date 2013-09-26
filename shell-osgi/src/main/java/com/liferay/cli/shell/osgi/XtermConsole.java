package com.liferay.cli.shell.osgi;



/**
 * @author Gregory Amerson
 */
public class XtermConsole extends AbstractConsole
{

    private String xtermCmd;

    public XtermConsole( String xtermCmd )
    {
        this.xtermCmd = xtermCmd;
    }

    @Override
    protected String getExecCommand(String cmd, String argLine)
    {
        return this.xtermCmd + " -ls -T liferay_console -e " + cmd + " " + argLine;
    }

    @Override
    protected String getExecQuietCommand( String cmd, String argLine )
    {
        return cmd + " " + argLine;
    }
}

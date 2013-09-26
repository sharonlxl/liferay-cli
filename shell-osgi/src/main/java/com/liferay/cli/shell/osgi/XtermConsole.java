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

    protected String getExecCommand(String cmd, String argLine)
    {
        return this.xtermCmd + " -ls -T liferay_console -e " + cmd + " " + argLine;
    }

}

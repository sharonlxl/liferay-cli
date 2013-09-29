package com.liferay.cli.shell.osgi;

/**
 * @author Sharon Li
 */
public class MacOSCommandProvider extends UnixOSCommandProvider
{
    public MacOSCommandProvider()
    {
        super();
    }

    @Override
    public void delete( String fileName )
    {
        super.delete( fileName );
    }

    @Override
    public void list( String pathName )
    {
        super.list( pathName );
    }

    @Override
    public void mkdir( String dirName )
    {
        super.mkdir( dirName );
    }

}

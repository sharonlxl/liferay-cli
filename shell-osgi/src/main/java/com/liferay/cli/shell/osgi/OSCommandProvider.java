package com.liferay.cli.shell.osgi;

/**
 * @author Sharon Li
 */
public interface OSCommandProvider
{
    void delete( String fileName );

    void list( String pathName );

    void mkdir( String dirName );
}
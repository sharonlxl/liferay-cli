package com.liferay.cli.shell.osgi;

import com.liferay.cli.metadata.MetadataService;
import com.liferay.cli.shell.Shell;

/**
 * @author Sharon Li
 */
public class WindowsOSCommandProvider extends BaseOSCommandProvider
{
    private static final String DIR_COMMAND = "cmd /C dir";
    private static final String DELETE_FILE_COMMAND = "cmd /C del /Q";
    private static final String DELETE_DIRECTORY_COMMAND = "cmd /C rmdir";
    private static final String MKDIR_COMMAND = "cmd /C mkdir";

    public WindowsOSCommandProvider( Shell shell, MetadataService metadataService )
    {
        super( shell, metadataService );
    }

    @Override
    protected String getRemoveFileCommand()
    {
        return DELETE_FILE_COMMAND;
    }

    @Override
    protected String getRemoveDirectoryCommand()
    {
        return DELETE_DIRECTORY_COMMAND;
    }

    @Override
    protected String getListCommand()
    {
        return DIR_COMMAND;
    }

    @Override
    protected String getMkdirCommand()
    {
        return MKDIR_COMMAND;
    }

}

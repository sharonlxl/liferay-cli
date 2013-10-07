package com.liferay.cli.shell.osgi;

import com.liferay.cli.metadata.MetadataService;
import com.liferay.cli.shell.Shell;


/**
 * @author Sharon Li
 */
public class UnixOSCommandProvider extends BaseOSCommandProvider
{
    private static final String LS_COMMAND = "ls -l";
    private static final String MKDIR_COMMAND = "mkdir -p";
    private static final String REMOVE_DIRECTORY_COMMAND = "rm -rf";
    private static final String REMOVE_FILE_COMMAND = "rm -f";

    public UnixOSCommandProvider( Shell shell, MetadataService metadataService )
    {
        super( shell, metadataService );
    }

    @Override
    protected String getListCommand()
    {
        return LS_COMMAND;
    }

    @Override
    protected String getMkdirCommand()
    {
        return MKDIR_COMMAND;
    }

    @Override
    protected String getRemoveDirectoryCommand()
    {
        return REMOVE_DIRECTORY_COMMAND;
    }

    @Override
    protected String getRemoveFileCommand()
    {
        return REMOVE_FILE_COMMAND;
    }

}

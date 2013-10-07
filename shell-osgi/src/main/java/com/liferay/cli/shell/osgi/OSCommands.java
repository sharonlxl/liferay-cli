package com.liferay.cli.shell.osgi;

import com.liferay.cli.shell.CliCommand;
import com.liferay.cli.shell.CliOption;
import com.liferay.cli.shell.SystemCommandMarker;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * @author Sharon Li
 */
@Component( immediate = true )
@Service
public class OSCommands implements SystemCommandMarker
{

    private static final String REMOVE_FILE_WINDOWS = "del";
    private static final String LIST_COMMAND_UNIX = "ls";
    private static final String LIST_COMMAND_WINDOWS = "dir";
    private static final String MAKE_DIRECTORY_COMMAND = "mkdir";
    private static final String REMOVE_DIRECTORY = "rmdir";
    private static final String REMOVE_FILE_ON_UNIX = "rm";

    @Reference private OSCommandService osCommandService;

    @CliCommand(
        value = { REMOVE_FILE_WINDOWS, REMOVE_DIRECTORY, REMOVE_FILE_ON_UNIX },
        help = "Remove one or more files",
        system = true )
    public void delete(
        @CliOption(
            key = { "" },
            mandatory = false,
            help = "parameter" )
        final String fileName )
    {
        osCommandService.getOSCommandProvider().delete( fileName );
    }

    @CliCommand(
        value = { LIST_COMMAND_WINDOWS, LIST_COMMAND_UNIX },
        help = "Display a list of files and subfolders",
        system = true )
    public void listFilesAndFolders(
        @CliOption(
            key = { "" },
            mandatory = false,
            help = "Specifies the working directory" )
        final String pathName ) throws Exception
    {
        osCommandService.getOSCommandProvider().list( pathName );
    }

    @CliCommand(
        value = { MAKE_DIRECTORY_COMMAND },
        help = "Create new folder(s)",
        system = true )
    public void makeDirectory(
        @CliOption(
            key = { "" },
            mandatory = false,
            help = "Name of directory" )
        final String dirName)
    {
        osCommandService.getOSCommandProvider().mkdir( dirName );
    }

}
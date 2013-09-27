
package com.liferay.cli.project.ray;

import com.liferay.cli.shell.CliAvailabilityIndicator;
import com.liferay.cli.shell.CliCommand;
import com.liferay.cli.shell.CliOption;
import com.liferay.cli.shell.CommandMarker;
import com.liferay.cli.shell.converters.StaticFieldConverter;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;

/**
 * Shell commands for {@link ServerOperations} commands.
 *
 * @author Gregory Amerson
 */
@Component( immediate = true )
@Service
public class ServerCommands implements CommandMarker
{
    private static final String SERVER_SETUP_COMMAND = "server setup";
    private static final String SERVER_START_COMMAND = "server start";
    private static final String SERVER_STOP_COMMAND = "server stop";

    @Reference
    private StaticFieldConverter staticFieldConverter;

    protected void activate(final ComponentContext context)
    {
        staticFieldConverter.add( ServerType.class );
        staticFieldConverter.add( ServerVersion.class );
        staticFieldConverter.add( ServerEdition.class );
    }

    protected void deactivate(final ComponentContext context)
    {
        staticFieldConverter.remove( ServerType.class );
        staticFieldConverter.remove( ServerVersion.class );
        staticFieldConverter.remove( ServerEdition.class );
    }

    @Reference
    private ServerOperations serverOperations;

    @CliCommand( value = SERVER_SETUP_COMMAND, help = "Configures server for Liferay project" )
    public void serverSetup(
        @CliOption(
            key = { "", "type" },
            mandatory = true,
            help = "The type of server to set as target for project" )
        final ServerType serverType,
        @CliOption(
            key = { "version" },
            mandatory = true,
            help = "The version of server to set as target for project" )
        final ServerVersion serverVersion,
        @CliOption(
            key = { "edition" },
            mandatory = false,
            help = "The edition of the server to set as target for project" )
        final ServerEdition serverEdition )
    {
        serverOperations.serverSetup( serverType, serverVersion, serverEdition );
    }

    @CliCommand( value = SERVER_START_COMMAND, help = "Starts Liferay portal as embedded server" )
    public void serverStart()
    {
        serverOperations.serverStart();
    }

    @CliCommand( value = SERVER_STOP_COMMAND, help = "Stops the embedded Liferay server" )
    public void serverStop()
    {
        serverOperations.serverStop();
    }

    @CliAvailabilityIndicator( SERVER_SETUP_COMMAND )
    public boolean isServerSetupAvailable()
    {
        return serverOperations.isServerSetupAvailable();
    }

    @CliAvailabilityIndicator( SERVER_START_COMMAND )
    public boolean isServerStartAvailable()
    {
        return serverOperations.isServerStartAvailable();
    }

}

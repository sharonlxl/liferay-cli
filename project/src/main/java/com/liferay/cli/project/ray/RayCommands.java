package com.liferay.cli.project.ray;

import com.liferay.cli.model.JavaPackage;
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
 * Shell commands for {@link RayOperations} commands.
 *
 * @author Gregory Amerson
 */
@Component( immediate = true )
@Service
public class RayCommands implements CommandMarker
{

    private static final String DEPLOY_COMMAND = "deploy";
    private static final String PROJECT_COMMAND = "project";
    private static final String PLUGIN_CREATE_COMMAND = "plugin create";

    @Reference
    private StaticFieldConverter staticFieldConverter;

    protected void activate(final ComponentContext context)
    {
        staticFieldConverter.add( PluginType.class );
    }

    protected void deactivate(final ComponentContext context)
    {
        staticFieldConverter.remove( PluginType.class );
    }

    @Reference private RayOperations rayOperations;

    @CliCommand( value = PROJECT_COMMAND, help = "Creates a new Liferay project" )
    public void createProject(
        @CliOption(
            key = { "", "projectName" },
            help = "The name of the project (last segment of package name used as default)" )
        final String projectName,
        @CliOption(
            key = { "topLevelPackage" },
            mandatory = false,
            optionContext = "update",
            help = "The uppermost package name (this becomes the <groupId> in Maven and also the '~' value when using Ray's shell)" )
        final JavaPackage topLevelPackage )
    {
        rayOperations.createProject( projectName, topLevelPackage );
    }

    @CliCommand( value = PLUGIN_CREATE_COMMAND, help = "Creates a new Liferay plugin" )
    public void createPlugin(
        @CliOption(
            key = { "", "pluginName" },
            mandatory = true,
            help = "The name of the plugin" )
        final String pluginName,
        @CliOption(
            key = { "type" },
            mandatory = true,
            help = "The type of plugin to create" )
        final PluginType pluginType )
    {
        rayOperations.createPlugin( pluginName, pluginType );
    }

    @CliCommand( value = DEPLOY_COMMAND, help = "Deploy the focused plugin" )
    public void deploy()
    {
        rayOperations.deploy();
    }

    @CliAvailabilityIndicator( PROJECT_COMMAND )
    public boolean isCreateProjectAvailable()
    {
        return rayOperations.isCreateProjectAvailable();
    }

    @CliAvailabilityIndicator( PLUGIN_CREATE_COMMAND )
    public boolean isPluginCreateAvailable()
    {
        return rayOperations.isPluginCreateAvailable();
    }

    @CliAvailabilityIndicator( DEPLOY_COMMAND )
    public boolean isDeployAvailable()
    {
        return rayOperations.isDeployAvailable();
    }


}

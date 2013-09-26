
package com.liferay.cli.project.packaging;

import com.liferay.cli.model.JavaPackage;
import com.liferay.cli.project.GAV;
import com.liferay.cli.project.Path;
import com.liferay.cli.project.PomManagementService;
import com.liferay.cli.project.ProjectOperations;
import com.liferay.cli.project.maven.Pom;
import com.liferay.cli.shell.osgi.ExternalConsoleProvider;
import com.liferay.cli.shell.osgi.ExternalConsoleProviderRegistry;

import java.io.File;
import java.util.Collection;

import org.apache.felix.scr.annotations.Reference;

/**
 * The Maven "server" {@link PackagingProvider}
 *
 * @author Gregory Amerson
 */
public abstract class PluginPackaging extends AbstractCorePackagingProvider
{

    @Reference
    private ExternalConsoleProviderRegistry externalShellProviderRegistry;

    @Reference
    private PomManagementService pomManagementService;

    /**
     * Constructor
     */
    public PluginPackaging( String id )
    {
        super( id, "war", id + "-pom-template.xml" );
    }

    @Override
    public String createArtifacts(
        JavaPackage topLevelPackage, String nullableProjectName, String artifactId, String javaVersion, GAV parentPom,
        String module, ProjectOperations projectOperations )
    {
        // execute mvn archetype command
        final Pom pluginsPom = pomManagementService.getPomFromModuleName( "plugins" );
        final File pluginsPomFile = new File( pluginsPom.getPath() );
        final String workingDir = pluginsPomFile.getParent(); // TODO RAY handle unexpected results better

        ExternalConsoleProvider externalShellProvider = externalShellProviderRegistry.getExternalShellProvider();

        final String args = getNewLiferayMavenArchetypeArgs( getId(), artifactId, parentPom.getGroupId() );

        Process process = externalShellProvider.getConsole().execute( workingDir, "mvn", args );

        try
        {
            process.waitFor();
        }
        catch( InterruptedException e )
        {
            //TODO RAY handle this error
        }

        // delete the pom file created from archetype
        final File portletPomFile = new File( pluginsPomFile.getParentFile(), artifactId + "/pom.xml" );
        portletPomFile.delete();

        return super.createArtifacts(
            topLevelPackage, nullableProjectName, artifactId, javaVersion, parentPom, module, projectOperations );
    }

    private String getNewLiferayMavenArchetypeArgs( String pluginType, String artifactId, String groupId )
    {
        return "archetype:generate -DinteractiveMode=false " +
            "-DarchetypeArtifactId=liferay-" + pluginType + "-archetype " +
            "-DarchetypeGroupId=com.liferay.maven.archetypes -DarchetypeVersion=6.2.0-SNAPSHOT " +
            "-DartifactId=" + artifactId + " -DgroupId=" + groupId + " Dversion=0.1.0-SNAPSHOT";
    }

    @Override
    protected void createOtherArtifacts(
        final JavaPackage topLevelPackage, final String module, final ProjectOperations projectOperations )
    {
    }

    public Collection<Path> getPaths()
    {
        return null;
    }
}

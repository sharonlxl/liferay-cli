
package com.liferay.cli.project.packaging;

import com.liferay.cli.model.JavaPackage;
import com.liferay.cli.project.GAV;
import com.liferay.cli.project.MavenOperations;
import com.liferay.cli.project.Path;
import com.liferay.cli.project.PomManagementService;
import com.liferay.cli.project.ProjectOperations;
import com.liferay.cli.shell.osgi.ExternalConsoleProviderRegistry;
import com.liferay.cli.support.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;

/**
 * The Maven "server" {@link PackagingProvider}
 *
 * @author Gregory Amerson
 */
@Component( componentAbstract = true )
public abstract class PluginPackaging extends AbstractCorePackagingProvider
{

    @Reference
    private ExternalConsoleProviderRegistry externalShellProviderRegistry;

    @Reference
    private PomManagementService pomManagementService;

    @Reference
    private MavenOperations mavenOperations;

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
        // create local archetypeCatalog
        String archetypeCatalog = null;

        try
        {
            final String storageLocation = System.getProperty( "org.osgi.framework.storage" );
            final File archetypeCatalogFile = new File( storageLocation + "/" + "archetype-catalog.xml" );

            final InputStream input = FileUtils.getInputStream( getClass(), "archetype-catalog.xml" );

            final FileOutputStream output = new FileOutputStream( archetypeCatalogFile );

            IOUtils.copy( input, output );
            IOUtils.closeQuietly( input );
            IOUtils.closeQuietly( input );

            archetypeCatalog = archetypeCatalogFile.getParentFile().toURI().toURL().toString().replace( "file:/", "file://" );
        }
        catch( Exception e )
        {
            LOGGER.log( Level.SEVERE, e.getMessage() );
        }

        // execute mvn archetype command
        final String args = getNewLiferayMavenArchetypeArgs( getId(), archetypeCatalog, artifactId, parentPom.getGroupId() );

        try
        {
            mavenOperations.executeMvnCommand( args );
        }
        catch( IOException e1 )
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return super.createArtifacts(
            topLevelPackage, nullableProjectName, artifactId, javaVersion, parentPom, module, projectOperations );
    }

    private String getNewLiferayMavenArchetypeArgs(
        final String pluginType, final String archetypeCatalog, final String artifactId, final String groupId )
    {
        String archetypeCatalogValue = archetypeCatalog;

        if( archetypeCatalogValue == null )
        {
            archetypeCatalogValue = "remote";
        }

        return "archetype:generate -DinteractiveMode=false -DarchetypeCatalog=\"" + archetypeCatalogValue + "\" " +
            "-DarchetypeArtifactId=liferay-" + pluginType + "-archetype " +
            "-DarchetypeGroupId=com.liferay.maven.archetypes -DarchetypeVersion=6.2.0-SNAPSHOT " + "-DartifactId=" +
            artifactId + " -DgroupId=" + groupId + " -Dversion=0.1.0-SNAPSHOT";
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

package com.liferay.cli.project.ray;

import com.liferay.cli.model.JavaPackage;
import com.liferay.cli.process.manager.ProcessManager;
import com.liferay.cli.project.GAV;
import com.liferay.cli.project.MavenOperationsImpl;
import com.liferay.cli.project.Path;
import com.liferay.cli.project.maven.Module;
import com.liferay.cli.project.maven.Pom;
import com.liferay.cli.project.packaging.PackagingProvider;
import com.liferay.cli.project.packaging.PluginsPackaging;
import com.liferay.cli.project.packaging.PomPackaging;
import com.liferay.cli.support.logging.HandlerUtils;
import com.liferay.cli.support.util.DomUtils;
import com.liferay.cli.support.util.XmlUtils;

import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of {@link RayOperations}.
 *
 * @author Gregory Amerson
 */
@Component( immediate = true )
@Service
public class RayOperationsImpl extends MavenOperationsImpl implements RayOperations
{
    private static final Logger LOGGER = HandlerUtils.getLogger( RayOperationsImpl.class );

    @Reference
    private ProcessManager processManager;

    public void createProject( final String projectName, final JavaPackage topLevelPackage )
    {
        PackagingProvider packagingProvider = packagingProviderRegistry.getPackagingProvider( PomPackaging.NAME );
        Validate.isTrue( isCreateProjectAvailable(), "Project creation is unavailable at this time" );

        String finalProjectName = projectName;

        if( StringUtils.isEmpty( finalProjectName ) )
        {
            finalProjectName = getProjectNameFromDirectory();
        }

        String artifactId = getArtifactId( finalProjectName );

        JavaPackage javaPackage = topLevelPackage;

        if( javaPackage == null )
        {
            javaPackage = new JavaPackage( getDefaultJavaPackageFromProjectName(projectName) );
        }

        packagingProvider.createArtifacts( javaPackage, projectName, artifactId, getJavaVersion( 6 ), null, "", this );
    }

    private String getArtifactId( String finalProjectName )
    {
        return finalProjectName.replaceAll( "\\s+", "-" ).toLowerCase();
    }

    //TODO RAY finish impl
    private String getProjectNameFromDirectory()
    {
        return "RayDemo";
    }

    //TODO RAY improve impl
    private String getDefaultJavaPackageFromProjectName( String projectName )
    {
        return projectName.replaceAll( "\\s+", "." ).toLowerCase();
    }

    /**
     * Returns the project's target Java version in POM format
     *
     * @param majorJavaVersion the major version provided by the user; can be
     *            <code>null</code> to auto-detect it
     * @return a non-blank string
     */
    private String getJavaVersion(final Integer majorJavaVersion) {
        if (majorJavaVersion != null && majorJavaVersion >= 6
                && majorJavaVersion <= 7) {
            return String.valueOf(majorJavaVersion);
        }
        // To be running Ray they must be on Java 6 or above
        return "1.6";
    }

    public String getProjectRoot() {
        return pathResolver.getRoot(Path.ROOT
                .getModulePathId(pomManagementService.getFocusedModuleName()));
    }

    public boolean isCreateModuleAvailable() {
        return true;
    }

    public boolean isCreateProjectAvailable()
    {
        return !isProjectAvailable( getFocusedModuleName() ) && isSafeLocation();
    }

    private boolean isSafeLocation()
    {
        // TODO RAY finish implementation, should make sure that we aren't in bad directories like
        // ray source code (liferay-cli) or in the user's home directory, others???
        return true;
    }

    /* (non-Javadoc)
     * @see com.liferay.cli.project.RayOperations#createPlugin(java.lang.String, com.liferay.cli.project.plugin.PluginType)
     */
    @Override
    public void createPlugin( String pluginName, PluginType pluginType )
    {
        final Pom rootPom = pomManagementService.getRootPom();
        final JavaPackage rootPackage = new JavaPackage( rootPom.getGroupId() );
        final GAV parentGAV = new GAV( rootPom.getGroupId(), rootPom.getArtifactId(), rootPom.getVersion() );
        final String rootPath = rootPom.getPath();

        // check to see if we need to create the plugins module
        Module plugins = rootPom.getModule( "plugins" );

        if( plugins == null )
        {
            final PackagingProvider pluginsPackagingProvider =
                packagingProviderRegistry.getPackagingProvider( PluginsPackaging.NAME );
            final String pluginsArtifactId = rootPom.getArtifactId() + "-plugins";

            createModule( rootPackage, parentGAV, "plugins", pluginsPackagingProvider, 6, pluginsArtifactId );
        }

        final Pom pluginsPom = pomManagementService.getPomFromModuleName( "plugins" );

        final Document pluginsPomDocument = XmlUtils.readXml( fileManager.getInputStream( pluginsPom.getPath() ) );
        final Element pluginsPomRoot = pluginsPomDocument.getDocumentElement();

        // add <properties> element
        final Element pluginsPropertiesElement =
            DomUtils.createChildIfNotExists( "properties", pluginsPomRoot, pluginsPomDocument );

        addPropertyElement(
            "liferay.maven.plugin.version", "6.2.0-SNAPSHOT", pluginsPropertiesElement, pluginsPomDocument );
        addPropertyElement(
            "liferay.auto.deploy.dir", "../../server/target/deploy/", pluginsPropertiesElement, pluginsPomDocument );
        addPropertyElement(
            "liferay.app.server.deploy.dir", "../../server/target/tomcat/webapps", pluginsPropertiesElement,
            pluginsPomDocument );
        addPropertyElement(
            "liferay.app.server.portal.dir", "../../server/target/tomcat/webapps/portal-web/",
            pluginsPropertiesElement, pluginsPomDocument );

        addPluginModule( pluginsPom, pluginName, pluginType, rootPackage );
    }

    private void addPropertyElement( String elementName, String textContent, Element parentElement, Document document )
    {
        final Element propertyElement = DomUtils.createChildIfNotExists( elementName, parentElement, document );
        propertyElement.setTextContent( textContent );
    }

    private void addPluginModule( Pom pluginsPom, String pluginName, PluginType pluginType, JavaPackage rootPackage )
    {
        final GAV pluginsGAV = new GAV( pluginsPom.getGroupId(), pluginsPom.getArtifactId(), pluginsPom.getVersion() );

        final PackagingProvider pluginPackagingProvider =
                        packagingProviderRegistry.getPackagingProvider( pluginType.getKey() );
        final String pluginArtifactId = getArtifactId( pluginName );

        createModule( rootPackage, pluginsGAV, pluginName, pluginPackagingProvider, 6, pluginArtifactId );
    }

    @Override
    public boolean isPluginCreateAvailable()
    {
        return isProjectAvailable( getFocusedModuleName() ) &&
            pomManagementService.getRootPom().equals( getFocusedModule() );
    }

}

package com.liferay.cli.project;

import com.liferay.cli.model.JavaPackage;
import com.liferay.cli.process.manager.ActiveProcessManager;
import com.liferay.cli.process.manager.ProcessManager;
import com.liferay.cli.project.maven.Pom;
import com.liferay.cli.project.packaging.PackagingProvider;
import com.liferay.cli.project.packaging.PackagingProviderRegistry;
import com.liferay.cli.support.logging.HandlerUtils;
import com.liferay.cli.support.util.DomUtils;
import com.liferay.cli.support.util.XmlUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementation of {@link MavenOperations}.
 *
 * @author Ben Alex
 * @author Alan Stewart
 * @since 1.0
 */
@Component(immediate = true)
@Service
public class MavenOperationsImpl extends AbstractProjectOperations implements
        MavenOperations {

    private static class LoggingInputStream extends Thread {
        private InputStream inputStream;
        private final ProcessManager processManager;

        /**
         * Constructor
         *
         * @param inputStream
         * @param processManager
         */
        public LoggingInputStream(final InputStream inputStream,
                final ProcessManager processManager) {
            this.inputStream = inputStream;
            this.processManager = processManager;
        }

        @Override
        public void run() {
            ActiveProcessManager.setActiveProcessManager(processManager);
            try {
                for (String line : IOUtils.readLines(inputStream)) {
                    if (line.startsWith("[ERROR]")) {
                        LOGGER.severe(line);
                    }
                    else if (line.startsWith("[WARNING]")) {
                        LOGGER.warning(line);
                    }
                    else {
                        LOGGER.info(line);
                    }
                }
            }
            catch (final IOException e) {
                // 1st condition for *nix/Mac, 2nd condition for Windows
                if (e.getMessage().contains("No such file or directory")
                        || e.getMessage().contains("CreateProcess error=2")) {
                    LOGGER.severe("Could not locate Maven executable; please ensure mvn command is in your path");
                }
            }
            finally {
                IOUtils.closeQuietly(inputStream);
                ActiveProcessManager.clearActiveProcessManager();
            }
        }
    }

    private static final Logger LOGGER = HandlerUtils
            .getLogger(MavenOperationsImpl.class);

    @Reference
    protected PackagingProviderRegistry packagingProviderRegistry;
    @Reference private ProcessManager processManager;

    protected void addModuleDeclaration(final String moduleName,
            final Document pomDocument, final Element root) {
        final Element modulesElement = createModulesElementIfNecessary(
                pomDocument, root);
        if (!isModuleAlreadyPresent(moduleName, modulesElement)) {
            modulesElement.appendChild(XmlUtils.createTextElement(pomDocument,
                    "module", moduleName));
        }
    }

    public void addPluginDependency( final String moduleName, final GAV plugin, final Dependency newPluginDependency, Document document, Element root )
    {
        Validate.notNull( plugin, "Plugin required" );
        Validate.notNull( newPluginDependency, "Dependency required" );

        final Pom pom = getPomFromModuleName( moduleName );
        Validate.notNull( pom, "The pom is not available, so dependencies cannot be added" );

        final Element pluginElement = XmlUtils.findRequiredElement( "//build/pluginManagement/plugins/plugin[artifactId='" + plugin.getArtifactId() + "']", root );

        final Element dependenciesElement = DomUtils.createChildIfNotExists( "dependencies", pluginElement, document );
        final List<Element> existingDependencyElements = XmlUtils.findElements( "dependency", dependenciesElement);

        // Look for any existing instances of this dependency
        boolean inserted = false;
        final List<String> addedDependencies = new ArrayList<String>();
        final List<String> removedDependencies = new ArrayList<String>();
        final List<String> skippedDependencies = Collections.emptyList();

        for( Element existingDependencyElement : existingDependencyElements )
        {
            final Dependency existingDependency = new Dependency( existingDependencyElement );

            if ( existingDependency.hasSameCoordinates( newPluginDependency ) )
            {
                // It's the same artifact, but might have a different
                // version, exclusions, etc.
                if (!inserted)
                {
                    // We haven't added the new one yet; do so now
                    dependenciesElement.insertBefore( newPluginDependency.getElement( document ), existingDependencyElement );
                    inserted = true;

                    if ( !newPluginDependency.getVersion().equals( existingDependency.getVersion()))
                    {
                        // It's a genuine version change => mention the
                        // old and new versions in the message
                        addedDependencies.add(newPluginDependency.getSimpleDescription());
                        removedDependencies.add(existingDependency.getSimpleDescription());
                    }
                }
                // Either way, we remove the previous one in case it was
                // different in any way
                dependenciesElement
                        .removeChild(existingDependencyElement);
            }
            // Keep looping in case it's present more than once
        }

        if (!inserted)
        {
            // We didn't encounter any existing dependencies with the
            // same coordinates; add it now
            dependenciesElement.appendChild(newPluginDependency.getElement(document));
            addedDependencies.add(newPluginDependency.getSimpleDescription());
        }

        final String message = getPomDependenciesUpdateMessage( addedDependencies, removedDependencies, skippedDependencies );
        final String newContents = XmlUtils.nodeToString(document);
    }

    private String getPomDependenciesUpdateMessage(
        final Collection<String> addedDependencies, final Collection<String> removedDependencies,
        final Collection<String> skippedDependencies )
    {
        final List<String> changes = new ArrayList<String>();
        changes.add( getDescriptionOfChange( ADDED, addedDependencies, "dependency", "dependencies" ) );
        changes.add( getDescriptionOfChange( REMOVED, removedDependencies, "dependency", "dependencies" ) );
        changes.add( getDescriptionOfChange( SKIPPED, skippedDependencies, "dependency", "dependencies" ) );
        for( final Iterator<String> iter = changes.iterator(); iter.hasNext(); )
        {
            if( StringUtils.isBlank( iter.next() ) )
            {
                iter.remove();
            }
        }
        return StringUtils.join( changes, "; " );
    }

    public void createModule(final JavaPackage topLevelPackage,
            final GAV parentPom, final String moduleName,
            final PackagingProvider selectedPackagingProvider,
            final Integer majorJavaVersion, final String artifactId) {
        Validate.isTrue(isCreateModuleAvailable(),
                "Cannot create modules at this time");
        final PackagingProvider packagingProvider = getPackagingProvider(selectedPackagingProvider);
        final String pathToNewPom = packagingProvider.createArtifacts(
                topLevelPackage, artifactId, getJavaVersion(majorJavaVersion),
                parentPom, moduleName, this);
        updateParentModulePom(moduleName);
        setModule(pomService.getPomFromPath(pathToNewPom));
    }

    private Element createModulesElementIfNecessary(final Document pomDocument,
            final Element root) {
        Element modulesElement = XmlUtils.findFirstElement("/project/modules",
                root);
        if (modulesElement == null) {
            modulesElement = pomDocument.createElement("modules");
            final Element build = XmlUtils.findFirstElement( "/project/build", root);
            root.insertBefore(modulesElement, build);
        }
        return modulesElement;
    }

    public void createProject(final JavaPackage topLevelPackage,
            final String projectName, final Integer majorJavaVersion,
            final GAV parentPom,
            final PackagingProvider selectedPackagingProvider) {
        Validate.isTrue(isCreateProjectAvailable(),
                "Project creation is unavailable at this time");
        final PackagingProvider packagingProvider = getPackagingProvider(selectedPackagingProvider);
        packagingProvider.createArtifacts(topLevelPackage, projectName,
                getJavaVersion(majorJavaVersion), parentPom, "", this);
    }

    public void executeMvnCommand(final String extra) throws IOException {
        final File root = new File(getProjectRoot());
        Validate.isTrue(root.isDirectory() && root.exists(),
                "Project root does not currently exist as a directory ('%s')",
                root.getCanonicalPath());

        final String cmd = (File.separatorChar == '\\' ? "mvn.bat " : "mvn ")
                + extra;
        final Process p = Runtime.getRuntime().exec(cmd, null, root);

        // Ensure separate threads are used for logging, as per ROO-652
        final LoggingInputStream input = new LoggingInputStream(
                p.getInputStream(), processManager);
        final LoggingInputStream errors = new LoggingInputStream(
                p.getErrorStream(), processManager);

        // Close OutputStream to avoid blocking by Maven commands that expect
        // input, as per ROO-2034
        IOUtils.closeQuietly(p.getOutputStream());
        input.start();
        errors.start();

        try {
            if (p.waitFor() != 0) {
                LOGGER.warning("The command '" + cmd
                        + "' did not complete successfully");
            }
        }
        catch (final InterruptedException e) {
            throw new IllegalStateException(e);
        }
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
        // To be running Roo they must be on Java 6 or above
        return "1.6";
    }

    private PackagingProvider getPackagingProvider(
            final PackagingProvider selectedPackagingProvider) {
        return ObjectUtils.defaultIfNull(selectedPackagingProvider,
                packagingProviderRegistry.getDefaultPackagingProvider());
    }

    public String getProjectRoot() {
        return pathResolver.getRoot(Path.ROOT
                .getModulePathId(pomService.getFocusedModuleName()));
    }

    public boolean isCreateModuleAvailable() {
        return true;
    }

    public boolean isCreateProjectAvailable() {
        return !isProjectAvailable(getFocusedModuleName());
    }

    private boolean isModuleAlreadyPresent(final String moduleName,
            final Element modulesElement) {
        for (final Element element : XmlUtils.findElements("module",
                modulesElement)) {
            if (element.getTextContent().trim().equals(moduleName)) {
                return true;
            }
        }
        return false;
    }

    private void updateParentModulePom(final String moduleName) {
        final String parentPomPath = pomService.getFocusedModule()
                .getPath();
        final Document parentPomDocument = XmlUtils.readXml(fileManager
                .getInputStream(parentPomPath));
        final Element parentPomRoot = parentPomDocument.getDocumentElement();
        DomUtils.createChildIfNotExists("packaging", parentPomRoot,
                parentPomDocument).setTextContent("pom");
        addModuleDeclaration(moduleName, parentPomDocument, parentPomRoot);
        final String addModuleMessage = getDescriptionOfChange(ADDED,
                Collections.singleton(moduleName), "module", "modules");
        fileManager.createOrUpdateTextFileIfRequired(getFocusedModule()
                .getPath(), XmlUtils.nodeToString(parentPomDocument),
                addModuleMessage, false);
    }
}

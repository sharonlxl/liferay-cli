
package com.liferay.cli.project.ray;

import com.liferay.cli.process.manager.FileManager;
import com.liferay.cli.project.Path;
import com.liferay.cli.project.PathResolver;
import com.liferay.cli.project.ProjectOperations;
import com.liferay.cli.shell.AbstractShell;

import java.io.File;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * Base implementation of {@link HintOperations}. <p> This implementation relies on a predefined resource bundle
 * containing all of this hints. This is likely to be replaced in the future with a more extensible implementation so
 * third-party add-ons can provide their own hints (see ROO-610 for details).
 *
 * @author Ben Alex
 * @author Alan Stewart
 * @since 1.1
 */
@Service
@Component
public class HintOperationsImpl implements HintOperations
{

    private static final String ANT_MATCH_DIRECTORY_PATTERN = File.separator + "**" + File.separator;
    private static ResourceBundle bundle = ResourceBundle.getBundle( HintCommands.class.getName() );

    @Reference
    private FileManager fileManager;
    @Reference
    private PathResolver pathResolver;
    @Reference
    private ProjectOperations projectOperations;

    private String determineTopic()
    {
        if( !projectOperations.isFocusedProjectAvailable() )
        {
            return "start";
        }

        if( ! ( hasServerPom() || hasPluginsPom() ) )
        {
            return "project";
        }

        if( hasServerPom() && ! hasPluginsPom() )
        {
            return "server";
        }

        if( hasServerPom() && hasPluginsPom() )
        {
            return "plugins";
        }

        return "general";
    }

    private boolean hasPluginsPom()
    {
        return fileExists( Path.ROOT, "plugins/pom.xml" );
    }

    private boolean fileExists( Path path, String file )
    {
        return fileManager.exists( pathResolver.getFocusedIdentifier( path, file ) );
    }

    private boolean hasServerPom()
    {
        return fileExists( Path.ROOT, "server/pom.xml" );
    }

    public SortedSet<String> getCurrentTopics()
    {
        final SortedSet<String> result = new TreeSet<String>();
        final String topic = determineTopic();
        if( "general".equals( topic ) )
        {
            for( final Enumeration<String> keys = bundle.getKeys(); keys.hasMoreElements(); )
            {
                result.add( keys.nextElement() );
            }
            // result.addAll(bundle.keySet()); ResourceBundle.keySet() method in
            // JDK 6+
        }
        else
        {
            result.add( topic );
        }
        return result;
    }

    private int getItdCount( final String itdUniquenessFilenameSuffix )
    {
        return fileManager.findMatchingAntPath(
            pathResolver.getFocusedRoot( Path.SRC_MAIN_JAVA ) + ANT_MATCH_DIRECTORY_PATTERN + "*_Roo_" +
                itdUniquenessFilenameSuffix + ".aj" ).size();
    }

    public String hint( String topic )
    {
        if( StringUtils.isBlank( topic ) )
        {
            topic = determineTopic();
        }
        try
        {
            final String message = bundle.getString( topic );
            return message.replace( "\r", IOUtils.LINE_SEPARATOR ).replace(
                "${completion_key}", AbstractShell.completionKeys );
        }
        catch( final MissingResourceException exception )
        {
            return "Cannot find topic '" + topic + "'";
        }
    }
}

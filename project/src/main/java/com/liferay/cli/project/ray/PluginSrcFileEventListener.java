package com.liferay.cli.project.ray;

import com.liferay.cli.file.monitor.event.FileEvent;
import com.liferay.cli.file.monitor.event.FileEventListener;
import com.liferay.cli.file.monitor.event.FileOperation;
import com.liferay.cli.project.PomManagementService;
import com.liferay.cli.project.maven.Pom;
import com.liferay.cli.support.IPath;
import com.liferay.cli.support.Path;
import com.liferay.cli.support.logging.HandlerUtils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;


@Component( immediate = true )
@Service
public class PluginSrcFileEventListener implements FileEventListener
{
    private static final Logger LOGGER = HandlerUtils.getLogger( PluginSrcFileEventListener.class );

    @Reference
    PomManagementService pomService;

    @Override
    public void onFileEvent( FileEvent fileEvent )
    {
        final Pom pluginsPom = pomService.getPomFromModuleName( "plugins" );

        if( pluginsPom != null )
        {
            final IPath pluginsDirPath = new Path( pluginsPom.getPath() ).removeLastSegments( 1 );
            final IPath modifiedFile = new Path( fileEvent.getFileDetails().getCanonicalPath() );

            if( pluginsDirPath.isPrefixOf( modifiedFile ) )
            {
                final IPath relativePluginPath = modifiedFile.makeRelativeTo( pluginsDirPath );

                // check to see if the changed happened in the src/ folder
                if( relativePluginPath.segments().length > 1 && "src".equals( relativePluginPath.segment( 1 ) ) )
                {
                    // get the plugin dir
                    final IPath pluginDirPath = pluginsDirPath.append( relativePluginPath.segment( 0 ) );

                    publishChangesToTargetFolder( fileEvent.getOperation(), modifiedFile, pluginDirPath, relativePluginPath );
                }
            }
        }
    }

    /**
     * @param operation
     * @param modifiedFilePath
     * @param pluginDirPath
     * @param relativePluginPath
     */
    private void publishChangesToTargetFolder( FileOperation operation, IPath modifiedFilePath, IPath pluginDirPath, IPath relativePluginPath )
    {
        final Pom pluginPom =
            pomService.getModuleForFileIdentifier( pluginDirPath.append( "pom.xml" ).toOSString() );
        final String outputName = pluginPom.getArtifactId() + "-" + pluginPom.getVersion();
        final IPath targetOutputPath = pluginDirPath.append( "target" ).append( outputName );
        final Pom serverPom = pomService.getPomFromModuleName( "server" );
        final IPath serverModulePath = new Path( serverPom.getPath() ).removeLastSegments( 1 );
        final IPath webappPath = serverModulePath.append( "target/tomcat/webapps" ).append( outputName );

        final IPath[] outputPaths = { targetOutputPath, webappPath };

        // get relative path into output
        final IPath webappOutputRelativePath = getWebappOutputRelativePath( pluginDirPath, modifiedFilePath );

        if( webappOutputRelativePath != null )
        {
            switch( operation )
            {
                case CREATED:
                case UPDATED:
                case DELETED:

                    final File modifiedFile = modifiedFilePath.toFile();

                    for( IPath outputPath : outputPaths )
                    {
                        if( outputPath.toFile().exists() )
                        {
                            final File destFile = outputPath.append( webappOutputRelativePath ).toFile();

                            try
                            {
                                if( operation == FileOperation.DELETED )
                                {
                                    FileUtils.deleteQuietly( destFile );
                                }
                                else
                                {
                                    if(  modifiedFile.isFile() )
                                    {
                                        FileUtils.copyFile( modifiedFile, destFile );
                                    }
                                    else
                                    {
                                        destFile.mkdirs();
                                    }
                                }
                            }
                            catch( IOException e )
                            {
                                LOGGER.log( Level.WARNING, e.getMessage(), e );
                            }
                        }
                    }

                    break;
                case MONITORING_FINISH:
                    break;
                case MONITORING_START:
                    break;
                case RENAMED:
                    break;
            }
        }
    }

    private static final IPath webappRoot = new Path( "src/main/webapp" );

    private IPath getWebappOutputRelativePath( IPath pluginDirPath, IPath modifiedFile )
    {
        final IPath pluginWebappRoot = pluginDirPath.append( webappRoot );

        if( pluginWebappRoot.isPrefixOf( modifiedFile ) )
        {
            return modifiedFile.makeRelativeTo( pluginWebappRoot );
        }

        return null;
    }

}

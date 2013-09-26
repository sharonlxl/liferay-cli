
package com.liferay.cli.project.ray;

import com.liferay.cli.support.DisplayName;
import com.liferay.cli.support.KeyType;

/**
 * Provides plugin types for plugin create setup command
 *
 * @author Gregory Amerson
 */
public class PluginType extends KeyType
{
    @DisplayName( "portlet" )
    public static final PluginType PORTLET = new PluginType( "PORTLET" );

    @DisplayName( "hook" )
    public static final PluginType HOOK = new PluginType( "HOOK" );

    @DisplayName( "ext" )
    public static final PluginType EXT = new PluginType( "EXT" );

    @DisplayName( "layouttpl" )
    public static final PluginType LAYOUTTPL = new PluginType( "LAYOUTTPL" );

    @DisplayName( "theme" )
    public static final PluginType THEME = new PluginType( "THEME" );

    @DisplayName( "web" )
    public static final PluginType WEB = new PluginType( "WEB" );

    public PluginType( final String key )
    {
        super( key );
    }

}

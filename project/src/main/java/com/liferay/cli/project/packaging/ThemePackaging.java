
package com.liferay.cli.project.packaging;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

/**
 * @author Gregory Amerson
 */
@Component
@Service
public class ThemePackaging extends PluginPackaging
{

    public static final String NAME = "theme";

    /**
     * Constructor
     */
    public ThemePackaging()
    {
        super( NAME );
    }

}

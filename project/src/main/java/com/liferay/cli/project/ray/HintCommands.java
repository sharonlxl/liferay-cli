
package com.liferay.cli.project.ray;

import com.liferay.cli.shell.CliCommand;
import com.liferay.cli.shell.CliOption;
import com.liferay.cli.shell.CommandMarker;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

/**
 * Shell commands for hinting services.
 *
 * @author Ben Alex
 * @since 1.0
 */
@Component
@Service
public class HintCommands implements CommandMarker
{

    @Reference
    private HintOperations hintOperations;

    @CliCommand( value = "hint", help = "Provides step-by-step hints and context-sensitive guidance" )
    public String hint( @CliOption(
                    key = { "topic", "" },
                    mandatory = false,
                    unspecifiedDefaultValue = "",
                    optionContext = "disable-string-converter,topics",
                    help = "The topic for which advice should be provided" ) final String topic )
    {
        return hintOperations.hint( topic );
    }
}

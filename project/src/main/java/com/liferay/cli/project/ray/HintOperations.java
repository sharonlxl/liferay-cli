package com.liferay.cli.project.ray;

import java.util.SortedSet;

public interface HintOperations
{
    SortedSet<String> getCurrentTopics();

    String hint( String topic );
}

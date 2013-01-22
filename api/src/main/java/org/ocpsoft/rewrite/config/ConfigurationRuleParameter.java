package org.ocpsoft.rewrite.config;

import java.util.regex.Pattern;

public interface ConfigurationRuleParameter extends ConfigurationRuleParameterMatches
{
   /**
    * Configure the regular expression {@link Pattern} to which this parameter must match.
    */
   ConfigurationRuleParameterMatches matches(String string);
}

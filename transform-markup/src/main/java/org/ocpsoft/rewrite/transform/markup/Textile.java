/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.transform.markup;

import java.util.Arrays;
import java.util.List;

import org.jruby.embed.ScriptingContainer;

/**
 * Transformer that translates Textile markup into HTML.
 * 
 * @author Christian Kaltepoth
 */
public class Textile extends JRubyTransformer<Textile>
{

   private static final String SCRIPT = "require 'redcloth'\n" +
            "RedCloth.new(input).to_html\n";

   private final boolean fullDocument;

   /**
    * Creates a {@link Textile} instance that renders a full HTML document structure.
    */
   public Textile()
   {
      this(true);
   }

   /**
    * Creates a {@link Textile} instance.
    */
   public Textile(boolean fullDocument)
   {
      this.fullDocument = fullDocument;
   }

   /**
    * Creates a {@link Textile} instance that renders a full HTML document structure.
    */
   public static Textile fullDocument()
   {
      return new Textile(true);
   }

   /**
    * Creates a {@link Textile} instance that just renders the textile without adding the HTML scaffold like a body or
    * head.
    */
   public static Textile partialDocument()
   {
      return new Textile(false);
   }

   @Override
   public List<String> getLoadPaths()
   {
      return Arrays.asList("ruby/redcloth/lib");
   }

   @Override
   public Object runScript(ScriptingContainer container)
   {

      Object fragment = container.runScriptlet(SCRIPT);

      if (fragment != null) {

         // create complete HTML structure
         if (fullDocument) {
            StringBuilder result = new StringBuilder();
            result.append("<!DOCTYPE html>\n");
            result.append("<html>\n<body>\n");
            result.append(fragment.toString());
            result.append("</body>\n</html>\n");
            return result.toString();
         }

         // output just the fragment
         else {
            return fragment.toString();
         }
      }
      return null;

   }

   @Override
   public Textile self()
   {
      return this;
   }

}

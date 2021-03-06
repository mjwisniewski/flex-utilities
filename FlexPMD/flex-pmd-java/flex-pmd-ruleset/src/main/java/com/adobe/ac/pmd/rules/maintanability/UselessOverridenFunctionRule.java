/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adobe.ac.pmd.rules.maintanability;

import java.util.List;

import com.adobe.ac.pmd.nodes.IFunction;
import com.adobe.ac.pmd.parser.IParserNode;
import com.adobe.ac.pmd.parser.KeyWords;
import com.adobe.ac.pmd.rules.core.AbstractAstFlexRule;
import com.adobe.ac.pmd.rules.core.ViolationPriority;

/**
 * @author xagnetti
 */
public class UselessOverridenFunctionRule extends AbstractAstFlexRule
{
   /*
    * (non-Javadoc)
    * @see
    * com.adobe.ac.pmd.rules.core.AbstractAstFlexRule#findViolations(com.adobe
    * .ac.pmd.nodes.IFunction)
    */
   @Override
   protected final void findViolations( final IFunction function )
   {
      final int statementNbAtFirstLevelInBody = function.getStatementNbInBody();

      if ( function.getBody() != null
            && function.isOverriden() && statementNbAtFirstLevelInBody == 1 )
      {
         final List< IParserNode > statements = function.findPrimaryStatementsInBody( KeyWords.SUPER.toString() );

         if ( statements != null
               && statements.size() == 1 && function.getBody().getChild( 0 ).getChild( 1 ) != null
               && function.getBody().getChild( 0 ).getChild( 1 ).getChild( 1 ) != null
               && !areArgumentsModified( function.getBody().getChild( 0 ).getChild( 1 ).getChild( 1 ) ) )
         {
            addViolation( function );
         }
      }
   }

   /*
    * (non-Javadoc)
    * @see com.adobe.ac.pmd.rules.core.AbstractFlexRule#getDefaultPriority()
    */
   @Override
   protected final ViolationPriority getDefaultPriority()
   {
      return ViolationPriority.LOW;
   }

   private boolean areArgumentsModified( final IParserNode args )
   {
      if ( args.getChildren() != null )
      {
         for ( final IParserNode arg : args.getChildren() )
         {
            if ( arg.getChildren() != null )
            {
               return true;
            }
         }
      }
      return false;
   }
}

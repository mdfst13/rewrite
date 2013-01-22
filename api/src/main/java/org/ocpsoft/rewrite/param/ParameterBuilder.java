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
package org.ocpsoft.rewrite.param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ocpsoft.rewrite.bind.Bindable;
import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Convertable;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.DefaultBindable;
import org.ocpsoft.rewrite.bind.HasBindings;
import org.ocpsoft.rewrite.bind.HasConverter;
import org.ocpsoft.rewrite.bind.HasValidator;
import org.ocpsoft.rewrite.bind.Validatable;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.util.ValueHolderUtil;

/**
 * An base implementation of {@link Parameter}
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ParameterBuilder<PARAMTYPE extends ParameterBuilder<PARAMTYPE, VALUETYPE>, VALUETYPE> extends DefaultBindable<PARAMTYPE>
         implements Parameter<PARAMTYPE, VALUETYPE>
{
   private final List<Transform<VALUETYPE>> transforms = new ArrayList<Transform<VALUETYPE>>();
   private final List<Constraint<VALUETYPE>> constraints = new ArrayList<Constraint<VALUETYPE>>();
   private Converter<?> converter = null;
   private Validator<?> validator = null;
   private List<Object> delegates = new ArrayList<Object>(0);

   /**
    * Create a new {@link ParameterBuilder} instance.
    */
   public ParameterBuilder()
   {}

   /**
    * Create a {@link ParameterBuilder} with the given delegates to which all operations must also be applied if any
    * applicable interfaces are implemented. Potential interfaces include: {@link Constrainable}, {@link Transformable},
    * {@link Bindable}, {@link HasValidator}, {@link HasConverter}
    */
   public ParameterBuilder(Object... delegates)
   {
      this.delegates = Arrays.asList(delegates);
   }

   @Override
   public Object convert(Rewrite event, EvaluationContext context, Object value)
   {
      return ValueHolderUtil.convert(event, context, converter, value);
   }

   @Override
   public boolean validate(Rewrite event, EvaluationContext context, Object value)
   {
      return ValueHolderUtil.validates(event, context, validator, value);
   }

   @Override
   public PARAMTYPE bindsTo(Binding binding)
   {
      if (binding instanceof Convertable && converter != null)
      {
         ((Convertable<?>) binding).convertedBy(converter);
      }
      if (binding instanceof Validatable && validator != null)
      {
         ((Validatable<?>) binding).validatedBy(validator);
      }

      for (Object delegate : delegates) {
         if (delegate instanceof Bindable)
         {
            ((Bindable<?>) delegate).bindsTo(binding);
         }
      }

      return super.bindsTo(binding);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <X extends Converter<?>> PARAMTYPE convertedBy(Class<X> type)
   {
      this.converter = ValueHolderUtil.resolveConverter(type);
      for (Binding binding : getBindings()) {
         if (binding instanceof Convertable)
         {
            ((Convertable<PARAMTYPE>) binding).convertedBy(converter);
         }
      }

      for (Object delegate : delegates) {
         if (delegate instanceof HasBindings)
         {
            HasBindings bindable = (HasBindings) delegate;
            for (Binding binding : bindable.getBindings()) {
               if (binding instanceof Convertable)
               {
                  ((Convertable<PARAMTYPE>) binding).convertedBy(converter);
               }
            }
         }
      }

      return (PARAMTYPE) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public PARAMTYPE convertedBy(Converter<?> converter)
   {
      this.converter = converter;
      for (Binding binding : getBindings()) {
         if (binding instanceof Convertable)
         {
            ((Convertable<PARAMTYPE>) binding).convertedBy(converter);
         }
      }

      for (Object delegate : delegates) {
         if (delegate instanceof HasBindings)
         {
            HasBindings bindable = (HasBindings) delegate;
            for (Binding binding : bindable.getBindings()) {
               if (binding instanceof Convertable)
               {
                  ((Convertable<PARAMTYPE>) binding).convertedBy(converter);
               }
            }
         }
      }

      return (PARAMTYPE) this;
   }

   @Override
   public Converter<?> getConverter()
   {
      return converter;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <X extends Validator<?>> PARAMTYPE validatedBy(Class<X> type)
   {
      this.validator = ValueHolderUtil.resolveValidator(type);
      for (Binding binding : getBindings()) {
         if (binding instanceof Validatable)
         {
            ((Validatable<PARAMTYPE>) binding).validatedBy(validator);
         }
      }

      for (Object delegate : delegates) {
         if (delegate instanceof HasBindings)
         {
            HasBindings bindable = (HasBindings) delegate;
            for (Binding binding : bindable.getBindings()) {
               if (binding instanceof Validatable)
               {
                  ((Validatable<PARAMTYPE>) binding).validatedBy(validator);
               }
            }
         }
      }

      return (PARAMTYPE) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public PARAMTYPE validatedBy(Validator<?> validator)
   {
      this.validator = validator;
      for (Binding binding : getBindings()) {
         if (binding instanceof Validatable)
         {
            ((Validatable<PARAMTYPE>) binding).validatedBy(validator);
         }
      }

      for (Object delegate : delegates) {
         if (delegate instanceof Bindable)
         {
            HasBindings bindable = (HasBindings) delegate;
            for (Binding binding : bindable.getBindings()) {
               if (binding instanceof Validatable)
               {
                  ((Validatable<PARAMTYPE>) binding).validatedBy(validator);
               }
            }
         }
      }

      return (PARAMTYPE) this;
   }

   @Override
   public Validator<?> getValidator()
   {
      return validator;
   }

   @Override
   @SuppressWarnings("unchecked")
   public PARAMTYPE constrainedBy(Constraint<VALUETYPE> constraint)
   {
      this.constraints.add(constraint);

      for (Object delegate : delegates) {
         if (delegate instanceof Constrainable)
         {
            ((Constrainable<PARAMTYPE, VALUETYPE>) delegate).constrainedBy(constraint);
         }
      }

      return (PARAMTYPE) this;
   }

   @Override
   public List<Constraint<VALUETYPE>> getConstraints()
   {
      return constraints;
   }

   @Override
   @SuppressWarnings("unchecked")
   public PARAMTYPE transformedBy(Transform<VALUETYPE> transform)
   {
      this.transforms.add(transform);

      for (Object delegate : delegates) {
         if (delegate instanceof Transformable)
         {
            ((Transformable<PARAMTYPE, VALUETYPE>) delegate).transformedBy(transform);
         }
      }

      return (PARAMTYPE) this;
   }

   @Override
   public List<Transform<VALUETYPE>> getTransforms()
   {
      return transforms;
   }

   @Override
   public String toString()
   {
      return "ParameterBuilder [transforms=" + transforms + ", constraints=" + constraints + ", bindings="
               + getBindings() + ", converter=" + converter + ", validator=" + validator + "]";
   }

}

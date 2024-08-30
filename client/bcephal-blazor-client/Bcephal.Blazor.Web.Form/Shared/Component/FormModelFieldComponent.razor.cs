using Bcephal.Blazor.Web.Base.Services;
using Bcephal.Models.Forms;
using Microsoft.AspNetCore.Components;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Bcephal.Blazor.Web.Form.Shared.Component
{
   public partial class FormModelFieldComponent
    {
        [Inject]
        public AppState AppState { get; set; }
        [Parameter]
        public  FormModelField formModelField { get; set; }
        [Parameter]
        public Action<long, FormDataValue> AddOrUpdateHandler { get; set; }
        [Parameter]
        public Func<long?, FormDataValue> ValueHandler { get; set; }
        [Parameter]
        public IEnumerable<object> Data { get; set; }
        bool IsSmallScreen { get; set; }

        FormDataValue Value = null;
        private object Value_ { 
            get 
                {
                if (Value == null)
                {
                    if (formModelField != null && formModelField.Id.HasValue)
                    {
                        Value = ValueHandler?.Invoke(formModelField.Id.Value);
                    }
                }
                if (Value == null)
                {
                    return null;
                }
                if (formModelField.IsPeriod)
                {
                    return Value.DateTimeValue;
                }
                else
               if (formModelField.IsMeasure)
                {
                    return Value.DecimalValue;
                }
                else
                {
                    return Value.StringValue;
                }
            }
            set {
                if (Value == null)
                {
                    Value = new();
                }
                if (formModelField.IsPeriod)
                {
                    if (value == null)
                    {
                        Value.DateTimeValue = null;
                    }
                    else
                    {
                        try
                        {
                            DateTime.TryParse(value.ToString(), out DateTime dat);
                            Value.DateTimeValue = dat;
                        }
                        catch
                        {
                            Value.DateTimeValue = null;
                        }
                    }
                }
                else
                if (formModelField.IsMeasure)
                {
                    if (value == null)
                    {
                        Value.DecimalValue = null;
                    }
                    else
                    {
                        try
                        {
                            decimal.TryParse(value.ToString(), out decimal DecimalValue);
                            Value.DecimalValue = DecimalValue;
                        }
                        catch
                        {
                            Value.DecimalValue = null;
                        }
                    }
                }
                else
                {
                    if (value == null)
                    {
                        Value.StringValue = null;
                    }
                    else
                    {
                       Value.StringValue = value.ToString();                        
                    }
                }
                AppState.Update = true;
                AddOrUpdateHandler?.Invoke(formModelField.Id.Value, Value);
            }            
        }

        private decimal? decimalValue_
        {
            get { 
                if(Value_ == null || string.IsNullOrWhiteSpace(Value_.ToString()))
                {
                    return null;
                }
                decimal.TryParse(Value_.ToString(), out decimal val);
                return val;
               }
            set
            {
                Value_ = value;
            }
        }

        private DateTime? DateTimeValue_
        {
            get
            {
                if (Value_ == null || string.IsNullOrWhiteSpace(Value_.ToString()))
                {
                    return null;
                }
                DateTime.TryParse(Value_.ToString(), out DateTime val);
                return val;
            }
            set
            {
                Value_ = value;
            }
        }

        private string StringValue_
        {
            get
            {
                if (Value_ == null)
                {
                    return null;
                }
                return Value_.ToString();
            }
            set
            {
                Value_ = value;
            }
        }

        string Format { get
            {
                if (formModelField.IsPeriod)
                {
                    if(formModelField.Format != null && !string.IsNullOrWhiteSpace(formModelField.Format.DefaultFormat))
                    {
                        return formModelField.Format.DefaultFormat;
                    }
                    return "dd/MM/yyyy";
                }
                else
                if (formModelField.IsMeasure)
                {
                    if (formModelField.Format != null)
                    {
                        return "F" + formModelField.Format.NbrOfDecimal;
                    }                    
                    return "F2";
                }
                return null;
            }
         }
        bool isEdition => FormModelFieldType.EDITION.Equals(formModelField.FieldType);
        bool isInfo => FormModelFieldType.INFO.Equals(formModelField.FieldNature);
        bool isAttachment => FormModelFieldType.ATTACHMENT.Equals(formModelField.FieldNature);
        bool isSelect_ => FormModelFieldType.SELECTION.Equals(formModelField.FieldType);
        bool isSelectAndEdition => FormModelFieldType.SELECTION_AND_EDITION.Equals(formModelField.FieldType);
        bool isSelectLinkAttrib => FormModelFieldType.SELECTION_FROM_LINKED_ATTRIBUTE.Equals(formModelField.FieldType);
        bool isSelectLinkField => FormModelFieldType.SELECTION_FROM_LINKED_FIELD.Equals(formModelField.FieldType);
        bool isSelect => isSelect_ || isSelectAndEdition || isSelectLinkAttrib || isSelectLinkField;
        bool isInput => FormModelFieldNature.INPUT.Equals(formModelField.FieldNature);
    }
}
